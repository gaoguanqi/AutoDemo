package com.pinduo.autodemo.utils

import android.text.TextUtils
import com.blankj.utilcode.util.AppUtils
import com.pinduo.autodemo.app.global.Constants
import kotlin.random.Random

class TaskUtils{
    companion object{
        ///截取评论内容
        fun handContent(content:String):String{
            if(TextUtils.isEmpty(content)) return "~"
            if(content.contains(";")){
                content.split(";").let {
                    return it[Random.nextInt(it.size)]
                }
            }
            return content
        }

        fun randomMark(list:List<String>):String{
            return list.get(Random.nextInt(0,list.size-1))
        }

        fun isDouyin1270():Boolean{
            if(TextUtils.equals("12.77.0",AppUtils.getAppVersionName(Constants.GlobalValue.douyinPackage))){
                return true
            }
            return false
        }
    }
}