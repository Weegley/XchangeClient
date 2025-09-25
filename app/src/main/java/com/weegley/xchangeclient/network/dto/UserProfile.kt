package com.weegley.xchangeclient.network.dto

data class UserProfile(
    val firstName: String? = null,
    val lastName: String? = null,
    val username: String? = null,
    val corporateCredit: Double? = null,
    val personalCredit: Double? = null,
    val group: Group? = null
) {
    data class Group(
        val channels: List<Channel>? = null
    )
    data class Channel(
        val id: Int,
        val name: String? = null,
        val trafficType: String? = null // "DATA", "ANALOG", ...
    )

    /** Первый канал с trafficType == "DATA" (то, что нам нужно для start/stop) */
    val firstDataChannelId: Int?
        get() = group?.channels?.firstOrNull { it.trafficType.equals("DATA", ignoreCase = true) }?.id

    /** Баланс как сумма корпоративного и персонального */
    val totalCredit: Double
        get() = (corporateCredit ?: 0.0) + (personalCredit ?: 0.0)
}
