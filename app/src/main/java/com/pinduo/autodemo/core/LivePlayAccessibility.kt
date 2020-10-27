package com.pinduo.autodemo.core

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.net.Uri
import android.os.SystemClock
import android.text.TextUtils
import cn.vove7.andro_accessibility_api.api.editor
import cn.vove7.andro_accessibility_api.api.withDesc
import cn.vove7.andro_accessibility_api.api.withId
import cn.vove7.andro_accessibility_api.api.withText
import cn.vove7.andro_accessibility_api.utils.whileWaitTime
import com.pinduo.auto.app.global.Constants
import com.pinduo.auto.widget.observers.ObserverListener
import com.pinduo.auto.widget.observers.ObserverManager
import com.pinduo.autodemo.app.MyApplication
import com.pinduo.autodemo.utils.LogUtils
import okhttp3.internal.wait

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
        withText("说点什么...")?.await(3000L)?.globalClick()?.let {
            if(it){
                SystemClock.sleep(1000L)
                withId("com.ss.android.ugc.aweme:id/b9q")?.await(3000L)?.childAt(0)?.trySetText(content)?.let {
                    if(it){
                        withDesc("发送")?.await(1000L)?.globalClick()?.let {
                            if(it){
                                LogUtils.logGGQ("评论成功")
                            }
                        }
                    }
                }
            }
        }
    }

    // 过滤 SPACE_TIME 事件内的重复页面
    var lastClickTime: Long = 0L
    var SPACE_TIME: Long = 3000L

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
            doSpeak("我来啦，哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈")
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