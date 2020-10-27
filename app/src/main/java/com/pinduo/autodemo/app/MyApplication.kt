package com.pinduo.autodemo.app

import android.annotation.SuppressLint
import android.app.Application
import android.view.View
import androidx.lifecycle.ProcessLifecycleOwner
import cn.vove7.andro_accessibility_api.AccessibilityApi
import com.birbit.android.jobqueue.JobManager
import com.birbit.android.jobqueue.config.Configuration
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.Utils
import com.pinduo.auto.app.manager.AppLifeCycleCallBack
import com.pinduo.auto.app.manager.ForebackLifeObserver
import com.pinduo.auto.extensions.layoutInflater
import com.pinduo.autodemo.R
import com.pinduo.autodemo.service.MyAccessibilityService
import com.pinduo.autodemo.utils.LogUtils
import com.pinduo.autodemo.utils.UiHandler
import com.yhao.floatwindow.*

class MyApplication:Application() {

    private lateinit var uiHandler:UiHandler
    private lateinit var jobManager:JobManager

    companion object {
        @JvmStatic
        lateinit var instance: MyApplication
            private set
    }

    fun getUiHandler(): UiHandler {
        return uiHandler
    }

    fun getJobManager():JobManager{
        return jobManager
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        this.uiHandler = UiHandler()
        configureJobManager()
        initConfig()
    }

    private fun configureJobManager() {
        jobManager = JobManager(Configuration.Builder(this).build())
    }

    private fun initConfig() {
        Utils.init(this)
        SPUtils.getInstance(AppUtils.getAppPackageName())
        registerActivityLifecycleCallbacks(AppLifeCycleCallBack())
        ProcessLifecycleOwner.get().lifecycle.addObserver(ForebackLifeObserver())
        AccessibilityApi.apply {
            BASE_SERVICE_CLS = MyAccessibilityService::class.java // 基础无障碍
            GESTURE_SERVICE_CLS = MyAccessibilityService::class.java // 高级无障碍
        }
        initFloatWindow()
    }

    @SuppressLint("InflateParams")
    private fun initFloatWindow() {
        val view: View = this.baseContext.layoutInflater.inflate(R.layout.layout_float_view,null)
        FloatWindow.with(getApplicationContext())
            .setView(view)
            .setWidth(Screen.width, 0.4f)                               //设置控件宽高
            .setHeight(Screen.height, 0.2f)
            .setX(2)                                   //设置控件初始位置
            .setY(2)
            .setDesktopShow(true)                        //桌面显示
            .setMoveType(MoveType.active) //可拖动，释放后自动回到原位置
            .setViewStateListener(MyViewStateListener())    //监听悬浮控件状态改变
            .setPermissionListener(MyPermissionListener())
            .build()
    }


    inner class MyViewStateListener: ViewStateListener {
        override fun onBackToDesktop() {
            LogUtils.logGGQ("onBackToDesktop")
        }

        override fun onMoveAnimStart() {
            LogUtils.logGGQ("onMoveAnimStart")

        }

        override fun onMoveAnimEnd() {
            LogUtils.logGGQ("onMoveAnimEnd")
        }

        override fun onPositionUpdate(x: Int, y: Int) {
            LogUtils.logGGQ("onPositionUpdate")
        }


        override fun onDismiss() {
            LogUtils.logGGQ("onDismiss")
        }

        override fun onShow() {
            LogUtils.logGGQ("onShow")
            FloatWindow.get()?.view?.let {
                uiHandler.initFloater(it.findViewById(R.id.sv_container),it.findViewById(R.id.tv_msg))
            }
        }

        override fun onHide() {
            LogUtils.logGGQ("onHide")
        }
    }

    inner class MyPermissionListener: PermissionListener {
        override fun onSuccess() {
            LogUtils.logGGQ("FloatWindow onSuccess")
        }

        override fun onFail() {
            LogUtils.logGGQ("FloatWindow onFail")
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        LogUtils.logGGQ("app onTerminate")
        FloatWindow.destroy()
    }

}