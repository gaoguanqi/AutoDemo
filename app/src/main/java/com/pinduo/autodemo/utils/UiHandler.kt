package com.pinduo.autodemo.utils

import android.os.Handler
import android.os.Looper
import android.widget.ScrollView
import android.widget.TextView

class UiHandler:Handler(Looper.getMainLooper()) {

    private val sBuilder by lazy { java.lang.StringBuilder() }


    private var svContainer:ScrollView? = null
    private var tvMsg:TextView? = null

    fun initFloater(sv:ScrollView,tv:TextView){
        this.svContainer = sv
        this.tvMsg = tv
    }

    fun sendMessage(msg:String){
        post {
            sBuilder.append(msg)
            sBuilder.append("\n")
            tvMsg?.setText(sBuilder.toString())
            svContainer?.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    fun clearMessage(){
        sBuilder.setLength(0)
    }

}