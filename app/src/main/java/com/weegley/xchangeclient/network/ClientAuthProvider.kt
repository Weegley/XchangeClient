package com.weegley.xchangeclient.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Достаёт CLIENT_AUTH_TOKEN для Basic auth у /api/oauth/token.
 * Стратегия: скачиваем главную/индекс и stores.js, собираем .js ссылки, затем сканируем их на наличие токена.
 *
 * ВАЖНО: регэксп теперь возвращает ИМЕННО значение токена из группы,
 * а не весь JS-файл.
 */
object ClientAuthProvider {
    private const val TAG = "ClientAuthProvider"
    private const val BASE = "https://m.xchange-box.com"

    // <script src="...js">
    private val jsHrefRegex = Regex("""<script[^>]+src=["']([^"']+\.js)["']""", RegexOption.IGNORE_CASE)

    // Прямое присваивание CLIENT_AUTH_TOKEN в JS (как в index-*.js):
    //  D(w, "CLIENT_AUTH_TOKEN", "base64...");
    //  или CLIENT_AUTH_TOKEN = "base64..." ;
    //  или "CLIENT_AUTH_TOKEN":"base64..."
    private val tokenDirectRegexes = listOf(
        Regex("""CLIENT_AUTH_TOKEN["']?\s*[:=]\s*["']([A-Za-z0-9+/=]{8,})["']"""),
        Regex("""["']CLIENT_AUTH_TOKEN["']\s*,\s*["']([^"']+)["']""") // на всякий случай (вар. со стат.инициализацией)
    )

    // Иногда в коде встречается явное зашивание Basic-заголовка:
    //   "Authorization", "Basic <base64>"
    private val tokenFromAuthHeaderRegex = Regex(
        """Authorization["']\s*,\s*["']Basic\s+([A-Za-z0-9+/=]{8,})["']"""
    )

    /**
     * Возвращает Base64-токен (часть после "Basic "), либо null.
     */
    fun fetchClientAuthToken(client: OkHttpClient): String? {
        return try {
            val seen = LinkedHashSet<String>()
            val candidates = mutableListOf<String>()

            // Собираем HTML + JS источники
            getAndCollect("$BASE/", client, candidates, seen)
            getAndCollect("$BASE/index.html", client, candidates, seen)
            getAndCollect("$BASE/lib/scripts/user-hmi-api/stores.js", client, candidates, seen)

            Log.d(TAG, "JS candidates total: ${candidates.size}")

            // Сканируем каждый .js
            for (url in candidates) {
                val body = httpGet(client, url) ?: continue

                // 1) Прямое присваивание (вернуть именно ГРУППУ 1!)
                for (rx in tokenDirectRegexes) {
                    rx.find(body)?.groupValues?.getOrNull(1)?.let { token ->
                        if (token.isNotBlank()) {
                            Log.i(TAG, "CLIENT_AUTH_TOKEN found in ${short(url)} (direct)")
                            return token
                        }
                    }
                }

                // 2) Из заголовка Authorization (Basic <token>) в JS
                tokenFromAuthHeaderRegex.find(body)?.groupValues?.getOrNull(1)?.let { token ->
                    if (token.isNotBlank()) {
                        Log.i(TAG, "CLIENT_AUTH_TOKEN found in ${short(url)} (auth-header)")
                        return token
                    }
                }
            }

            Log.e(TAG, "CLIENT_AUTH_TOKEN not found in any JS")
            null
        } catch (t: Throwable) {
            Log.e(TAG, "fetchClientAuthToken() failed: ${t.message}", t)
            null
        }
    }

    private fun getAndCollect(
        url: String,
        client: OkHttpClient,
        out: MutableList<String>,
        seen: MutableSet<String>
    ) {
        val body = httpGet(client, url) ?: return

        // Если это HTML — вытащим <script src="...">
        val matches = jsHrefRegex.findAll(body).map { it.groupValues[1] }.toList()
        if (matches.isNotEmpty()) {
            for (href in matches) {
                val full = absolutize(href)
                if (seen.add(full)) out += full
            }
        } else if (url.endsWith(".js")) {
            // Если это уже JS — добавим сам URL (на всякий случай)
            if (seen.add(url)) out += url
        }
    }

    private fun httpGet(client: OkHttpClient, url: String): String? {
        val req = Request.Builder().url(url).get().build()
        client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) return null
            return resp.body?.string()
        }
    }

    private fun absolutize(href: String): String {
        return if (href.startsWith("http")) href
        else if (href.startsWith("/")) "$BASE$href"
        else "$BASE/$href"
    }

    private fun short(url: String): String = url.removePrefix(BASE)
}
