package com.example.aidlapplication

import android.app.Service
import android.content.Intent
import android.os.IBinder

class ITArchAIDLService : Service() {

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    private val binder = object: IITArchAidlInterface.Stub(){
        override fun calcStatusURL(code: Int): String {
            return when((1..4).random()){
                1 -> "https://http.dog/${code}.jpg"
                2 -> "https://http.cat/${code}"
                3 -> "https://httpcats.com/${code}.jpg"
                else -> "https://http.garden/${code}.jpg"
            }
        }
    }
}