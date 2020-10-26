package com.pinduo.autodemo.service

import android.content.Context
import android.text.TextUtils
import android.view.accessibility.AccessibilityManager
import cn.vove7.andro_accessibility_api.AccessibilityApi
import cn.vove7.andro_accessibility_api.AppScope
import com.pinduo.auto.app.global.Constants
import com.pinduo.auto.http.entity.TaskEntity
import com.pinduo.auto.im.OnSocketListener
import com.pinduo.auto.im.SocketClient
import com.pinduo.auto.widget.observers.ObserverManager
import com.pinduo.auto.widget.timer.MyScheduledExecutor
import com.pinduo.auto.widget.timer.TimerTickListener
import com.pinduo.autodemo.app.MyApplication
import com.pinduo.autodemo.core.CommonAccessbility
import com.pinduo.autodemo.core.LivePlayAccessibility
import com.pinduo.autodemo.utils.LogUtils
import com.yhao.floatwindow.FloatWindow
import okhttp3.internal.concurrent.TaskQueue
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MyAccessibilityService :AccessibilityApi(){

    private val socketClient by lazy { SocketClient.instance }
    private val runnable by lazy { MyScheduledExecutor.INSTANCE }

    private val initialDelay:Long = 1L
    private val period:Long = 1L
    private val max:Long = Long.MAX_VALUE

    private val service by lazy {  Executors.newScheduledThreadPool(4) }
    private val uiHandler by lazy { MyApplication.instance.getUiHandler() }


    override val enableListenAppScope: Boolean = true

    override fun onPageUpdate(currentScope: AppScope) {
        super.onPageUpdate(currentScope)
        currentScope?.let {
            if(TextUtils.equals(Constants.GlobalValue.douyinPackage,it.packageName)){
                when(it.pageName){
                    Constants.Douyin.PAGE_MAIN ->{
                        ObserverManager.instance.notifyObserver(Constants.Task.task3,it.pageName)
                    }

                    Constants.Douyin.PAGE_LIVE_ROOM ->{
                        ObserverManager.instance.notifyObserver(Constants.Task.task3,it.pageName)
                    }
                }
            }
        }

    }

    override fun onCreate() {
        super.onCreate()
        CommonAccessbility.INSTANCE.initService(this)
        LivePlayAccessibility.INSTANCE.initService(this)

        (getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager).addAccessibilityStateChangeListener {
            LogUtils.logGGQ("AccessibilityManager：${it}")
            if(it){
                FloatWindow.get()?.run {
                    if(!isShowing)show()
                }
            }else{
                FloatWindow.get()?.run {
                    if(isShowing)hide()
                }
            }
        }

        runnable.setListener(object : TimerTickListener {
            override fun onStart(name: String, job: String) {
                LogUtils.logGGQ("开始任务：${name} --- ${job}")
                uiHandler.sendMessage("开始任务：${name} --- ${job}")
            }

            override fun onTick(tick: Long) {
                LogUtils.logGGQ("tick：${tick}")
                uiHandler.sendMessage("tick：${tick}")
            }

            override fun onMark(mark: Long) {
                LogUtils.logGGQ("onMark：${mark}")
                uiHandler.clearMessage()
                uiHandler.sendMessage("onMark：${mark}")
            }

            override fun onStop(name: String, job: String) {
                LogUtils.logGGQ("结束任务：${name} --- ${job}")
                uiHandler.sendMessage("结束任务：${name} --- ${job}")
//                uiHandler.clearMessage()
                if(TextUtils.equals(Constants.Task.douyin,name)){
                    when(job){
                        Constants.Task.task3 ->{
                            if(LivePlayAccessibility.INSTANCE.isInLiveRoom()){
                                stopTask(false)
                            }
                        }
                    }
                }
            }
        })

        socketClient.setListener(object : OnSocketListener {
            override fun call(entity: TaskEntity) {
                LogUtils.logGGQ("收到数据：${entity.toString()}")
                val software:String = entity.software
                val task:String = entity.task
                val message:String = entity.message

                if(!TextUtils.isEmpty(message)){
                    uiHandler.sendMessage(message)
                }

                if(!TextUtils.isEmpty(message) && TextUtils.equals(message,"stop")){
                    stopTask()
                    return
                }

                if(TextUtils.equals(software,Constants.Task.douyin)){
                    when(task){
                        Constants.Task.task3 -> {
                            val zxTime:String = entity.zx_time
                            val zhiboNum:String = entity.zhibo_num
                            if(!TextUtils.isEmpty(zxTime) && !TextUtils.isEmpty(zhiboNum)){
                                runnable.onReStart(software,task,zxTime.toLong() + Constants.GlobalValue.plusTime)
                                LivePlayAccessibility.INSTANCE.doLiveRoom(zhiboNum)
                            }
                        }

                        Constants.Task.task4 -> {

                        }
                    }
                }
            }
        })
    }

    private fun stopTask(isNormal:Boolean = true) {
        LivePlayAccessibility.INSTANCE.setInLiveRoom(false)
        LivePlayAccessibility.INSTANCE.setLiveURI("")
        if (isNormal && LivePlayAccessibility.INSTANCE.isInLiveRoom()) {
            socketClient.sendParentSuccess()
        } else {
            socketClient.sendParentError()
        }
        ObserverManager.instance.remove(Constants.Task.task3)
        CommonAccessbility.INSTANCE.douyin2Main()
        if(isNormal){
            uiHandler.sendMessage("正常结束")
        }else{
            uiHandler.sendMessage("延时结束")
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        socketClient.onConnect()
        runnable.isRing().let {
            if(!it){
                service.scheduleAtFixedRate(runnable,initialDelay,period, TimeUnit.SECONDS)
                runnable.onReStart("app","task",max)
            }
        }
    }
}