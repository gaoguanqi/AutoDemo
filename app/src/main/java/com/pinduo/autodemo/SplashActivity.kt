package com.pinduo.autodemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ToastUtils
import com.pinduo.autodemo.app.global.Constants
import com.pinduo.autodemo.ui.AccountActivity
import com.pinduo.autodemo.utils.PermissionUtil
import com.pinduo.autodemo.utils.RequestPermission
import com.pinduo.autodemo.ui.HomeActivity
import com.pinduo.autodemo.ui.TestActivity
import com.pinduo.autodemo.utils.IMEIUtils
import com.tbruyelle.rxpermissions2.RxPermissions

class SplashActivity : AppCompatActivity() {

    val rxPermissions: RxPermissions = RxPermissions(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        applyPermissions()
    }


    override fun onRestart() {
        super.onRestart()
        applyPermissions()
    }

    private fun applyPermissions() {
        PermissionUtil.applyPermissions(object : RequestPermission {
            override fun onRequestPermissionSuccess() {
                launchTarget()
//                launchTest()
            }

            override fun onRequestPermissionFailure(permissions: List<String>) {
                ToastUtils.showShort("权限未通过")
            }

            override fun onRequestPermissionFailureWithAskNeverAgain(permissions: List<String>) {
                ToastUtils.showShort("权限未通过")
            }
        }, rxPermissions)
    }

    private fun launchTarget() {
        val imei: String? = FileIOUtils.readFile2String(Constants.Path.IMEI_PATH)
        if(TextUtils.isEmpty(imei)){
            startActivity(Intent(MainActivity@ this, AccountActivity::class.java))
            this.finish()
        }else{
            IMEIUtils.setIMEI(imei!!)
            startActivity(Intent(MainActivity@ this, HomeActivity::class.java))
            finish()
        }
    }
    
    
    private fun launchTest(){
        startActivity(Intent(MainActivity@ this, TestActivity::class.java))
        finish()
    }

}