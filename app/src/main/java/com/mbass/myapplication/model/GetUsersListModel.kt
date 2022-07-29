package com.mbass.myapplication.model


import com.google.gson.annotations.SerializedName

data class GetUsersListModel(
    @SerializedName("message") var message: String?,
    @SerializedName("users") var users: List<User?>?
) {
    data class User(
        @SerializedName("createdAt") var createdAt: String?,
        @SerializedName("email") var email: String?,
        @SerializedName("_id") var id: String?,
        @SerializedName("name") var name: String?,
        @SerializedName("updatedAt") var updatedAt: String?,
        @SerializedName("__v") var v: Int?
    )
}