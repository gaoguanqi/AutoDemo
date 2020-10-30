package com.pinduo.autodemo.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.NetworkUtils
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.pinduo.autodemo.base.BaseActivity
import com.pinduo.autodemo.R
import com.pinduo.autodemo.app.global.Constants
import com.pinduo.autodemo.extensions.isResultSuccess
import com.pinduo.autodemo.http.api.ApiService
import com.pinduo.autodemo.http.entity.CommonEntity
import com.pinduo.autodemo.utils.IMEIUtils
import com.pinduo.autodemo.utils.LogUtils
import kotlinx.android.synthetic.main.activity_account.*

class AccountActivity : BaseActivity() {
    override fun getLayoutId(): Int = R.layout.activity_account

    override fun initData(savedInstanceState: Bundle?) {

        tv_imei.setText(IMEIUtils.getDeviceId())
//        et_username.setText("gaoguanqi")


        btn_bind.setOnClickListener {
            val username:String? = et_username.text.toString().trim()
            val imei:String? = tv_imei.text.toString().trim()
            LogUtils.logGGQ("username:${username} --- imei:${imei}")
            if(TextUtils.isEmpty(username) || TextUtils.isEmpty(imei)){
                showTopMessage("请输入绑定的号码")
                return@setOnClickListener
            }

            if(!NetworkUtils.isConnected()){
                showTopMessage("网络异常")
                return@setOnClickListener
            }
            httpBindDevice(username!!,imei!!)
        }
    }



    private fun httpBindDevice(username: String, imei: String) {
        OkGo.get<String>(ApiService.URL_BINDDEVICE)
            .tag(this)
            .params(Constants.ApiParams.USERNAME,username)
            .params(Constants.ApiParams.IMEI,imei)
            .execute(object : StringCallback(){
                override fun onSuccess(response: Response<String>?) {
                    try {
                        response?.let { // 非空
                            val result:String? = it.body()
                            result?.let {it1 ->
                                val entity: CommonEntity? = Gson().fromJson<CommonEntity>(it1,CommonEntity::class.java)
                                entity?.let {it2 ->
                                    if(it2.code.isResultSuccess()){ 
                                        goHome(imei)
                                    }else{
                                        showTopMessage(it2.msg)
                                    }
                                }?:let {
                                    showTopMessage("绑定设备出错！")
                                }
                            }?:let {
                                showTopMessage("绑定设备出错！")
                            }
                        }?:let { //空
                            showTopMessage("绑定设备出错！")
                        }
                    }catch (e:Exception){
                        e.stackTrace
                        showTopMessage("绑定设备出错！")
                    }
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    showTopMessage("绑定设备错误！")
                }
            })
    }




    private fun goHome(imei: String) {
        if (FileUtils.createOrExistsFile(Constants.Path.IMEI_PATH)) {
            if(FileIOUtils.writeFileFromString(Constants.Path.IMEI_PATH,imei)){
                IMEIUtils.setIMEI(imei)
                startActivity(Intent(AccountActivity@this,HomeActivity::class.java))
                this.finish()
            }else{
                showTopMessage("写入文件出错！")
            }
        }
    }
}