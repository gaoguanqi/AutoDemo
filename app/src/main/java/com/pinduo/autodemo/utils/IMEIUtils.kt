package com.pinduo.autodemo.utils

import com.blankj.utilcode.util.DeviceUtils

class IMEIUtils {

    companion object{
        private var imei:String = ""

        fun getIMEI():String{
            return imei
        }

        fun setIMEI(v:String){
            imei = v
        }

        fun getDeviceId():String?{
            return DeviceUtils.getAndroidID()
//            return "a1dc791e63a9d89a"
//            return "64f6e2a90e6cf164"
        }
    }
}