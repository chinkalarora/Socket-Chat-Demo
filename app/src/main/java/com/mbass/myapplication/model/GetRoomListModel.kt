package com.mbass.myapplication.model
import com.google.gson.annotations.SerializedName


data class GetRoomListModel(
    @SerializedName("message") var message: String?,
    @SerializedName("rooms") var rooms: List<Room>?
) {
    data class Room(
        @SerializedName("createdAt") var createdAt: String?,
        @SerializedName("_id") var id: String?,
        @SerializedName("member1Id") var member1Id: Member1Id?,
        @SerializedName("member2Id") var member2Id: Member2Id?,
        @SerializedName("updatedAt") var updatedAt: String?,
        @SerializedName("__v") var v: Int?
    ) {
        data class Member1Id(
            @SerializedName("createdAt") var createdAt: String?,
            @SerializedName("email") var email: String?,
            @SerializedName("_id") var id: String?,
            @SerializedName("name") var name: String?,
            @SerializedName("password") var password: String?,
            @SerializedName("updatedAt") var updatedAt: String?,
            @SerializedName("__v") var v: Int?
        )

        data class Member2Id(
            @SerializedName("createdAt") var createdAt: String?,
            @SerializedName("email") var email: String?,
            @SerializedName("_id") var id: String?,
            @SerializedName("name") var name: String?,
            @SerializedName("password") var password: String?,
            @SerializedName("updatedAt") var updatedAt: String?,
            @SerializedName("__v") var v: Int?
        )
    }
}