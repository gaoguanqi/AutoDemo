package com.pinduo.autodemo.core

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import com.pinduo.auto.app.global.Constants
import com.pinduo.auto.widget.observers.ObserverListener
import com.pinduo.auto.widget.observers.ObserverManager
import com.pinduo.autodemo.app.MyApplication
import com.pinduo.autodemo.utils.LogUtils

class LivePlayAccessibility private constructor() : BaseAccessbility(), ObserverListener {

    companion object {
        val INSTANCE: LivePlayAccessibility by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            LivePlayAccessibility()
        }
    }

    private var isInRoom:Boolean = false
    private var liveURI:String = ""

    fun isInLiveRoom():Boolean = isInRoom
    fun setInLiveRoom(b:Boolean){
        isInRoom = b
    }

    fun getLiveURI():String = liveURI
    fun setLiveURI(s:String){
        liveURI = s
    }



    override fun initService(service: AccessibilityService) {
        super.initService(service)
    }




    //进入直播间
    fun doLiveRoom(zhiboNum: String) {
        startLiveRoom(zhiboNum)
    }


    // 发评论
    fun doSpeak(content: String) {

    }

    // 过滤 SPACE_TIME 事件内的重复页面
    var lastClickTime: Long = 0
    var SPACE_TIME: Long = 3000

    private fun startLiveRoom(zhiboNum:String) {
        setLiveURI(zhiboNum)
        ObserverManager.instance.add(Constants.Task.task3,this)
        val currentTime = System.currentTimeMillis()
        if(!isInLiveRoom() && !TextUtils.isEmpty(getLiveURI()) && currentTime - lastClickTime > SPACE_TIME){
            val intent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse(getLiveURI()))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            MyApplication.instance.startActivity(intent)
            setInLiveRoom(true)
            MyApplication.instance.getUiHandler().sendMessage("<<<直播间>>>")
        }
    }


    override fun observer(content: String) {
        LogUtils.logGGQ("监听到页面：${content}")
        when (content) {
            Constants.Douyin.PAGE_MAIN -> {
                MyApplication.instance.getUiHandler().sendMessage("回到首页")
                setInLiveRoom(false)
                //如果在任务内回到首页,进入直播间
                startLiveRoom(getLiveURI())
            }

            Constants.Douyin.PAGE_LIVE_ROOM -> {
                MyApplication.instance.getUiHandler().sendMessage("进入直播间")
                setInLiveRoom(true)
            }
        }
    }

}