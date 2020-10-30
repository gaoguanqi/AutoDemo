package com.pinduo.autodemo.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import com.pinduo.autodemo.base.BaseActivity
import com.pinduo.autodemo.R
import com.pinduo.autodemo.app.MyApplication
import com.pinduo.autodemo.utils.AccessibilityServiceUtils
import com.pinduo.autodemo.utils.LogUtils
import com.yhao.floatwindow.FloatWindow

class HomeActivity : BaseActivity() {

    private val REQUESTCODE_ACCESSIBILITY: Int = 1001

    override fun getLayoutId(): Int = R.layout.activity_home

    override fun initData(savedInstanceState: Bundle?) {
        checkAccessibilityPermission()
    }


    private fun checkAccessibilityPermission() {

        if (AccessibilityServiceUtils.isAccessibilitySettingsOn(MyApplication.instance)) {
            LogUtils.logGGQ("无障碍已开启")
            FloatWindow.get()?.let {
                if (!it.isShowing) it.show()
            }
        } else {
            LogUtils.logGGQ("无障碍未开启")
            startActivityForResult(
                Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS),
                REQUESTCODE_ACCESSIBILITY
            )
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        LogUtils.logGGQ("requestCode:${requestCode} -- resultCode:${resultCode}")
        if (requestCode == REQUESTCODE_ACCESSIBILITY) {
            checkAccessibilityPermission()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.moveTaskToBack(true)
        }
        return super.onKeyDown(keyCode, event)
    }
}