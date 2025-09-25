package com.weegley.xchangeclient.network.dto

data class UserProfile(
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
        val trafficType: String? = null
    )
}
