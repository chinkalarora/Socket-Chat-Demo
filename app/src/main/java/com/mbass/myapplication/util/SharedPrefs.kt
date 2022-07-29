package com.mbass.myapplication.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.mbass.myapplication.model.LoginAPIResposne

object SharedPrefs {
    private  val SHARED_PREF = "sharedPreference"
    private  val TOKEN = "token"
    var USER_ID = "userId"
    var LOGIN_DATA = "LOGIN_DATA"

    private fun getSharedPreference(context: Context): SharedPreferences? {
        var sp: SharedPreferences? = null
        try {
            sp = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        } catch (ignored: Exception) {

        }
        return sp
    }

    fun storeToken(context: Context, string: String?) {
        val prefsEditor = getSharedPreference(context)?.edit()
        prefsEditor?.putString(TOKEN, string)
        prefsEditor?.apply()
    }

    fun storeUserId(context: Context, string: String?) {
        val prefsEditor = getSharedPreference(context)?.edit()
        prefsEditor?.putString(USER_ID, string)
        prefsEditor?.apply()
    }

    fun getToken(context: Context): String? {
        return getSharedPreference(context)?.getString(TOKEN, "")
    }


    fun getUserId(context: Context): String? {
        return getSharedPreference(context)?.getString(USER_ID, "")
    }

    fun getLoginDetail(context: Context): LoginAPIResposne? {
        val gson = Gson()
        val json = getSharedPreference(context)
            ?.getString(LOGIN_DATA, "")
        return gson.fromJson<LoginAPIResposne>(json, LoginAPIResposne::class.java)
    }
    
    fun storeLoginDetail(
        context: Context,
        userObject: LoginAPIResposne
    ) {
        val prefsEditor = getSharedPreference(context)?.edit()
        val gson = Gson()
        val json = gson.toJson(userObject)
        prefsEditor?.putString(LOGIN_DATA, json)
        prefsEditor?.apply()
    }


}