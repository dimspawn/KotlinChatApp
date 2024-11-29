package com.kotlinchatapp.core.utils

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException
import javax.inject.Inject

class SocketHandler @Inject constructor() {
    private var mSocket: Socket? = null

    @Synchronized
    fun setSocket() {
        if (mSocket == null) {
            try {
                mSocket = IO.socket("http://192.168.100.77:3000")
                Log.d("SocketHandler", "Not Error")
            } catch (e: URISyntaxException) {
                Log.e("SocketHandler", "URI Syntax Error: ${e.message}")
            }
        }
    }

    @Synchronized
    fun getSocket(): Socket? {
        return mSocket
    }

    @Synchronized
    fun establishConnection() {
        mSocket?.let {
            if (!it.connected()) {
                mSocket?.connect()
            }
        }
    }

    @Synchronized
    fun closeConnection() {
        mSocket?.disconnect()
    }
}