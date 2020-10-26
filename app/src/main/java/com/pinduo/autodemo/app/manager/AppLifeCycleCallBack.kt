package com.pinduo.auto.app.manager
import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * 全局管理 Activity
 */
class AppLifeCycleCallBack : Application.ActivityLifecycleCallbacks {

    companion object {
        @JvmStatic
        var currentActivity: Activity? = null
            private set
    }


    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
    }
}
