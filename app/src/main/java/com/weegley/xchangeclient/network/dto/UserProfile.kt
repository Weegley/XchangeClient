package com.weegley.xchangeclient.network.dto

import com.google.gson.annotations.SerializedName

data class UserProfile(
    @SerializedName("firstName") val firstName: String? = null,
    @SerializedName("lastName")  val lastName: String? = null,
    @SerializedName("username")  val username: String? = null,

    @SerializedName("corporateCredit") val corporateCredit: Double? = null,
    @SerializedName("personalCredit")  val personalCredit: Double? = null,

    @SerializedName("group") val group: Group? = null
) {
    data class Group(
        @SerializedName("id") val id: Int? = null,
        @SerializedName("name") val name: String? = null,
        @SerializedName("channels") val channels: List<Channel>? = null
    )

    data class Channel(
        @SerializedName("id") val id: Int? = null,
        @SerializedName("name") val name: String? = null,
        @SerializedName("trafficType") val trafficType: String? = null,
        @SerializedName("rank") val rank: Int? = null
    )

    /** Удобный хелпер для первого DATA-канала */
    val firstDataChannelId: Int?
        get() = group?.channels?.firstOrNull { it.trafficType.equals("DATA", ignoreCase = true) }?.id
}
