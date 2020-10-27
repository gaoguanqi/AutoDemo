package com.pinduo.autodemo.utils

import android.text.TextUtils
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
    }
}