package com.mbass.myapplication.model


import com.google.gson.annotations.SerializedName

data class GetRoomChatResModel(
    @SerializedName("chats") var chats: List<Chat?>?,
    @SerializedName("message") var message: String?
) {
    data class Chat(
        @SerializedName("createdAt") var createdAt: String?,
        @SerializedName("_id") var id: String?,
        @SerializedName("message") var message: String?,
        @SerializedName("read") var read: Boolean?,
        @SerializedName("receiverId") var receiverId: ReceiverId?,
        @SerializedName("roomId") var roomId: String?,
        @SerializedName("senderId") var senderId: SenderId?,
        @SerializedName("updatedAt") var updatedAt: String?,
        @SerializedName("__v") var v: Int?
    ) {
        data class ReceiverId(
            @SerializedName("createdAt") var createdAt: String?,
            @SerializedName("email") var email: String?,
            @SerializedName("_id") var id: String?,
            @SerializedName("name") var name: String?,
            @SerializedName("password") var password: String?,
            @SerializedName("updatedAt") var updatedAt: String?,
            @SerializedName("__v") var v: Int?
        )

        data class SenderId(
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