package com.pinduo.autodemo.service

import android.content.Context
import android.text.TextUtils
import android.view.accessibility.AccessibilityManager
import cn.vove7.andro_accessibility_api.AccessibilityApi
import cn.vove7.andro_accessibility_api.AppScope
import com.birbit.android.jobqueue.CancelResult
import com.birbit.android.jobqueue.Job
import com.birbit.android.jobqueue.TagConstraint
import com.birbit.android.jobqueue.callback.JobManagerCallback
import com.pinduo.autodemo.widget.observers.ObserverManager
import com.pinduo.autodemo.widget.timer.MyScheduledExecutor
import com.pinduo.autodemo.widget.timer.TimerTickListener
import com.pinduo.autodemo.app.MyApplication
import com.pinduo.autodemo.app.global.Constants
import com.pinduo.autodemo.core.CommonAccessbility
import com.pinduo.autodemo.core.LivePlayAccessibility
import com.pinduo.autodemo.core.data.TaskData
import com.pinduo.autodemo.core.job.*
import com.pinduo.autodemo.http.entity.TaskEntity
import com.pinduo.autodemo.im.OnSocketListener
import com.pinduo.autodemo.im.SocketClient
import com.pinduo.autodemo.utils.LogUtils
import com.yhao.floatwindow.FloatWindow
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
            LogUtils.logQ("className：${it.pageName}")
//            if(TextUtils.equals(it.pageName,"android.app.Dialog")){
//                withId("com.ss.android.ugc.aweme:id/xd")?.globalClick()
//            }
            if(TextUtils.equals(Constants.GlobalValue.douyinPackage,it.packageName)){

                when(it.pageName){
                    Constants.Douyin.PAGE_MAIN ->{

                        ObserverManager.instance.notifyObserver(it.pageName)
                    }

                    Constants.Douyin.PAGE_LIVE_ROOM ->{
                        ObserverManager.instance.notifyObserver(it.pageName)
                    }

                    Constants.Douyin.PAGE_LIVE_ANCHOR ->{
                        ObserverManager.instance.notifyObserver(it.pageName)
                    }
                }
            }
        }
    }

    override fun onCreate() {
        //must 基础无障碍
        baseService = this
        super.onCreate()
        //must 高级无障碍
        gestureService = this

//        for (index in 0..30){
//            haList.add(index.toString())
//        }


        CommonAccessbility.INSTANCE.initService(this)
        LivePlayAccessibility.INSTANCE.initService(this).setSocketClient(socketClient)
        MyApplication.instance.getJobManager().addCallback(object :
            JobManagerCallback{
            override fun onJobRun(job: Job, resultCode: Int) {
                LogUtils.logGGQ("onJobRun：${(job as BaseJob).data.task}---${resultCode}")
            }

            override fun onDone(job: Job) {
                LogUtils.logGGQ("onDone：${(job as BaseJob).data.task}")
            }

            override fun onAfterJobRun(job: Job, resultCode: Int) {
                LogUtils.logGGQ("onAfterJobRun：${(job as BaseJob).data.task}---${resultCode}")
            }

            override fun onJobCancelled(
                job: Job,
                byCancelRequest: Boolean,
                throwable: Throwable?) {
                LogUtils.logGGQ("onJobCancelled：${(job as BaseJob).data.task}---${byCancelRequest}---${throwable}")

            }

            override fun onJobAdded(job: Job) {
                LogUtils.logGGQ("onJobAdded：${(job as BaseJob).data.task}")
            }
        })

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

                if(!TextUtils.isEmpty(task) && TextUtils.equals(task,Constants.Task.task3)){
                    // 任务3 接收到数据 要回馈
                    socketClient.onReceiveStatus()
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
                            var newContent:String = "."
                            if(!TextUtils.isEmpty(entity.fayan)){
                                newContent = entity.fayan
                            }

                            MyApplication.instance.getJobManager().addJobInBackground(LiveTaskJob(TaskData(task = task,content = newContent))){
                                //回调
                            }
                        }

                        Constants.Task.task6 ->{
                            val zxTime:String = entity.zhixing_time
                            if(!TextUtils.isEmpty(zxTime) && zxTime.toLong() > 0){
                                MyApplication.instance.getJobManager().addJobInBackground(LiveTaskJob(TaskData(task = task,zxTime = (zxTime.toLong() * 1000L)))){
                                    //回调
                                }
                            }
                        }

                        Constants.Task.task9 ->{
                            MyApplication.instance.getJobManager().addJobInBackground(LiveTaskJob(TaskData(task = task))){
                                //回调
                            }
                        }
                    }
                }
            }
        })
    }

    private val haList = mutableListOf<String>()


    private fun stopTask(isNormal:Boolean = true) {
        //任务结束 停止所有job
        MyApplication.instance.getJobManager().cancelJobsInBackground(CancelResult.AsyncCancelCallback {cancelResult ->
            LogUtils.logGGQ("任务结束停止所有job")
        }, TagConstraint.ALL,"job")


        if (isNormal && LivePlayAccessibility.INSTANCE.isInLiveRoom()) {
            socketClient.sendParentSuccess()
        } else {
            socketClient.sendParentError()
        }
        LivePlayAccessibility.INSTANCE.setInLiveRoom(false)
        LivePlayAccessibility.INSTANCE.setLiveURI("")
        ObserverManager.instance.remove()
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


    override fun onInterrupt() {
        super.onInterrupt()
        FloatWindow.destroy()
    }

    override fun onDestroy() {
        //must 基础无障碍
        baseService = null
        super.onDestroy()
        //must 高级无障碍
        gestureService = null
        FloatWindow.destroy()
    }
}