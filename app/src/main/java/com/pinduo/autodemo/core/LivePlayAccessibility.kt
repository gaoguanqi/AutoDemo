package com.pinduo.autodemo.core

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.net.Uri
import android.text.TextUtils
import cn.vove7.andro_accessibility_api.AccessibilityApi
import cn.vove7.andro_accessibility_api.api.*
import com.blankj.utilcode.util.ScreenUtils
import com.pinduo.autodemo.app.MyApplication
import com.pinduo.autodemo.app.global.Constants
import com.pinduo.autodemo.im.SocketClient
import com.pinduo.autodemo.service.MyAccessibilityService
import com.pinduo.autodemo.utils.LogUtils
import com.pinduo.autodemo.utils.NodeUtils
import com.pinduo.autodemo.utils.TaskUtils
import com.pinduo.autodemo.utils.WaitUtil
import com.pinduo.autodemo.widget.observers.ObserverListener
import com.pinduo.autodemo.widget.observers.ObserverManager
import java.util.*

class LivePlayAccessibility private constructor() : BaseAccessbility<LivePlayAccessibility>(), ObserverListener {

    //com.bytedance.android.livesdk.chatroom.viewmodule.FollowGuideWidget$a
    //com.bytedance.android.livesdk.widget.LiveBottomSheetDialog

    companion object {
        val INSTANCE: LivePlayAccessibility by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            LivePlayAccessibility()
        }
    }

    //子任务是否执行成功
    private var isSuccess:Boolean = false

    private var isInRoom: Boolean = false
    private var liveURI: String = ""

    fun isInLiveRoom(): Boolean = isInRoom
    fun setInLiveRoom(b: Boolean) {
        isInRoom = b
    }


    fun getLiveURI(): String = liveURI
    fun setLiveURI(s: String) {
        liveURI = s
    }


    override fun initService(service: AccessibilityService): LivePlayAccessibility {
        return super.initService(service)
    }

    private var socketClient: SocketClient? = null
    fun getSocketClient(): SocketClient? = socketClient
    fun setSocketClient(socket: SocketClient) {
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
        withText("说点什么...")?.globalClick()?.let {
            // 3秒之内 成功查找到节点
            if (it) { //成功点击了该节点
                var b9q = "com.ss.android.ugc.aweme:id/b9q"
                if(!TaskUtils.isDouyin1270()){
                    b9q = "com.ss.android.ugc.aweme:id/b40"
                }
                withId(b9q)?.childAt(0)
                    ?.trySetText(content)?.let { it1 ->
                    //3秒之内 成功查找到该节点的第0个子节点尝试设置评论内容
                    if (it1) {
                        // 设置评论内容成功
                        var fvs = "com.ss.android.ugc.aweme:id/fvs"
                        if(!TaskUtils.isDouyin1270()){
                            fvs = "com.ss.android.ugc.aweme:id/fga"
                        }
//                        withDesc("发送")?.await(2000L)?.globalClick()?.let { it2 ->
                        withId(fvs)?.await(3000L)?.globalClick()?.let { it2 ->
                            //1秒内 成功查找到该节点
                            if (it2) {
                                //成功点击了该节点
                                MyApplication.instance.getUiHandler().sendMessage("评论成功:${content}")
                                getSocketClient()?.sendSuccess()
                            }else{
                                MyApplication.instance.getUiHandler().sendMessage("未点击发送")
                            }
                        }
                    }
                } ?: let {
                    MyApplication.instance.getUiHandler().sendMessage("评论失败2->${content}")
                    getSocketClient()?.sendError()
                }
            } else {
                //点击该节点失败
                MyApplication.instance.getUiHandler().sendMessage("评论失败3->${content}")
                getSocketClient()?.sendError()
            }
        } ?: let {
            // 3秒之内未找到该节点
            MyApplication.instance.getUiHandler().sendMessage("评论失败4->${content}")
            getSocketClient()?.sendError()
        }
    }





    private fun startLiveRoom(zhiboNum: String) {
        setLiveURI(zhiboNum)
        ObserverManager.instance.add(this)
        if (!isInLiveRoom() && !TextUtils.isEmpty(getLiveURI())) {
            setInLiveRoom(true)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getLiveURI()))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            MyApplication.instance.startActivity(intent)

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

            Constants.Douyin.PAGE_LIVE_ANCHOR,
            Constants.Douyin.PAGE_LIVE_GIFT,
            Constants.Douyin.PAGE_LIVE_Follow,
            Constants.Douyin.PAGE_LIVE_MORE ->{
                if(isInLiveRoom()) {
                    //被遮挡点击操作
                    MyApplication.instance.getUiHandler().sendMessage("被遮挡")
                    LogUtils.logGGQ("被遮挡...")
                    val x = ScreenUtils.getScreenWidth() / 2
                    val y = ScreenUtils.getScreenHeight() / 2
                    val isClick: Boolean = click(x, y)
                    LogUtils.logGGQ(if (isClick) "点击" else "未点击")
                }
            }
        }


    }


    //-----------------
    // 点赞
    private val x = ScreenUtils.getScreenWidth() - 50
    private val y = 10
    private val delay = 1
    private val period = 500
    private var clickCount = 0
    fun doGiveLike(s:Long){
        val count = (s / period)
        try {
            for (index in 1..count){
                if(isInLiveRoom() && AccessibilityApi.isGestureServiceEnable && pressWithTime(x,y,delay)){
                    clickCount++
                }
                MyApplication.instance.getUiHandler().sendMessage("${count}-->>>${index}次-->${clickCount}次成功")
                WaitUtil.sleep(500L)
            }
        }finally {
            if(clickCount >= count){
                getSocketClient()?.sendSuccess()
            }else{
                getSocketClient()?.sendError()
            }
        }
    }


    //---------------购物车-----

    fun doShopCart() {
        isSuccess = false
        try {

            var fhq = "com.ss.android.ugc.aweme:id/fhq"
            if(!TaskUtils.isDouyin1270()){
                fhq = "com.ss.android.ugc.aweme:id/f3t"
            }

            withId(fhq)?.globalClick()?.let {
                if (it) {
                    var fh9 = "com.ss.android.ugc.aweme:id/fh9"
                    if(!TaskUtils.isDouyin1270()){
                        fh9 = "com.ss.android.ugc.aweme:id/f3b"
                    }
                    withId(fh9)?.globalClick()?.let { it1 ->
                        if (it1) {
                            WaitUtil.sleep(2000L)
                            withText("立即购买")?.globalClick()?.let { it2 ->
                                if (it2) {
                                    WaitUtil.sleep(2000L)
                                    NodeUtils.onClickTextByNode(service.rootInActiveWindow)
                                    MyApplication.instance.getUiHandler().sendMessage("等待。。。")
                                    isSuccess = true
                                    WaitUtil.sleep(10000L)
                                    withType("WebView")?.find()?.let { it3 ->
                                        if (it3.size >= 2) {
                                            it3[1]?.childAt(0)?.childAt(0)?.childAt(0)?.globalClick()
                                                ?.let { it4 ->
                                                    if (it4) {
                                                        WaitUtil.sleep(2000L)
                                                        back()
                                                        WaitUtil.sleep(2000L)
                                                        back()
                                                        WaitUtil.sleep(1000L)
                                                        back()
                                                    }
                                                }
                                        }
                                    }?:let {
                                        WaitUtil.sleep(2000L)
                                        back()
                                        WaitUtil.sleep(2000L)
                                        back()
                                        WaitUtil.sleep(1000L)
                                        back()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }finally {
            if(isSuccess){
                getSocketClient()?.sendSuccess()
            }else {
                getSocketClient()?.sendError()
            }
        }


    }
}