package com.pinduo.autodemo.core

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.net.Uri
import com.pinduo.autodemo.app.MyApplication

class CommonAccessbility private constructor():BaseAccessbility() {

    companion object {
        val INSTANCE:CommonAccessbility by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED){
            CommonAccessbility()
        }
    }

    override fun initService(service: AccessibilityService) {
        super.initService(service)
    }


    fun douyin2Main(){
        val intent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse("snssdk1128://feed?refer=web"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        MyApplication.instance.startActivity(intent)
    }



}