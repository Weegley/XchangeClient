package com.weegley.xchangeclient.network.dto

import com.google.gson.annotations.SerializedName

/**
 * Обёртка именно под ответ /api/user/{username}.
 * В нём вершина НЕ такая же, как у ApiEnvelope<T>, поэтому даём свой тип.
 */
data class UserProfileEnvelope(
    @SerializedName("return") val `return`: UserProfileReturn,
    val label: String?,
    @SerializedName("label_parameters") val labelParameters: Any?,
    @SerializedName("success_description") val successDescription: String?
)

data class UserProfileReturn(
    val firstName: String?,
    val lastName: String?,
    val username: String,
    val corporateCredit: Double?,
    val personalCredit: Double?,
    val group: UserGroup
)

data class UserGroup(
    val id: Int,
    val name: String?,
    val channels: List<UserChannel> = emptyList()
)

data class UserChannel(
    val id: Int,
    val name: String?,
    val description: String?,
    val trafficType: String?, // ждём "DATA" для дата-канала
    val device: UserDevice?
)

data class UserDevice(
    val id: Int,
    val name: String?,
    val shortName: String?
)
