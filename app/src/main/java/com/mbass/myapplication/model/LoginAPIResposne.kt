package com.mbass.myapplication.model
import com.google.gson.annotations.SerializedName

 class LoginAPIResposne{
    @SerializedName("expiresIn") var expiresIn: Int? = null
     @SerializedName("token") var token: String? = null
     @SerializedName("message") var message: String? = null
     @SerializedName("user") var user: User? = null
     @SerializedName("userId") var userId: String? = null

     data class User(
        @SerializedName("createdAt") var createdAt: String?,
        @SerializedName("email") var email: String?,
        @SerializedName("_id") var id: String?,
        @SerializedName("name") var name: String?,
        @SerializedName("updatedAt") var updatedAt: String?,
        @SerializedName("__v") var v: Int?
    )
}