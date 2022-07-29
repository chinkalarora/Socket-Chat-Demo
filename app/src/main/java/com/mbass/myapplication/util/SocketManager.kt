package com.mbass.myapplication.util

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import java.net.URISyntaxException

object SocketManager {
    private var mSocket: Socket? = null

    fun setSocket() {
        mSocket = try {
            IO.socket("https://0b86-182-70-121-2.in.ngrok.io")
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            throw e
        }
    }

    fun getSocket(): Socket? {
        Log.e("TAG", ": Connected------" + mSocket?.connected())
        return mSocket
    }

    fun getEmitterListener(event: String, listener: Emitter.Listener): Emitter? {
        return getSocket()!!.on(event, listener)
    }

    fun establishConnection() {
        mSocket?.connect()
    }
}


