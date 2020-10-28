package com.pinduo.autodemo.core

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import cn.vove7.andro_accessibility_api.api.*
import com.pinduo.autodemo.app.global.Constants
import com.pinduo.autodemo.im.SocketClient
import com.pinduo.autodemo.utils.WaitUtil
import com.pinduo.autodemo.widget.observers.ObserverListener
import com.pinduo.autodemo.widget.observers.ObserverManager
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

    private var socketClient: SocketClient? = null
    fun getSocketClient(): SocketClient? = socketClient
    fun setSocketClient(socket: SocketClient){
        this.socketClient = socket
    }





    //进入直播间
    fun doLiveRoom(zhiboNum: String) {
        startLiveRoom(zhiboNum)
    }


    // 发评论
    fun doSpeak(content: String) {
//        withText("说点什么...")?.await(3000L)?.globalClick()?.let {
//            if(it){
//                SystemClock.sleep(1000L)
//                withId("com.ss.android.ugc.aweme:id/b9q")?.await(3000L)?.childAt(0)?.trySetText(content)?.let {
//                    if(it){
//                        withDesc("发送")?.await(1000L)?.globalClick()?.let {
//                            if(it){
//                                LogUtils.logGGQ("评论成功")
//                            }
//                        }
//                    }
//                }
//            }
//        }

        withText("说点什么...")?.await(3000L)?.globalClick()?.let {
            // 3秒之内 成功查找到节点
            if(it){ //成功点击了该节点
                WaitUtil.sleep(1000L) //延时1秒
                withId("com.ss.android.ugc.aweme:id/b9q")?.await(3000L)?.childAt(0)?.trySetText(content)?.let { it1 ->
                    //3秒之内 成功查找到该节点的第0个子节点尝试设置评论内容
                    if(it1){
                        // 设置评论内容成功
                        withDesc("发送")?.await(1000L)?.globalClick()?.let { it2 ->
                            //1秒内 成功查找到该节点
                            if(it2){
                                //成功点击了该节点
                                MyApplication.instance.getUiHandler().sendMessage("评论成功:${content}")
                                getSocketClient()?.sendSuccess()
                            }
                        }?:let {
                            //
//                            back()
                            MyApplication.instance.getUiHandler().sendMessage("评论失败1->${content}")
                            getSocketClient()?.sendError()
                        }
                    }
                }?:let {
//                    back()
                    MyApplication.instance.getUiHandler().sendMessage("评论失败2->${content}")
                    getSocketClient()?.sendError()
                }
            }else{
                //点击该节点失败
                MyApplication.instance.getUiHandler().sendMessage("评论失败3->${content}")
                getSocketClient()?.sendError()
            }
        }?:let {
            // 3秒之内未找到该节点
            MyApplication.instance.getUiHandler().sendMessage("评论失败4->${content}")
            getSocketClient()?.sendError()
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