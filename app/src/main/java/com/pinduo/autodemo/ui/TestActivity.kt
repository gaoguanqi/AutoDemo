package com.pinduo.autodemo.ui

import android.os.Bundle
import com.pinduo.auto.base.BaseActivity
import com.pinduo.autodemo.R
import com.pinduo.autodemo.app.MyApplication
import com.pinduo.autodemo.core.job.TaskJob
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : BaseActivity() {
    var count:Int = 0
    override fun getLayoutId(): Int = R.layout.activity_test

    override fun initData(savedInstanceState: Bundle?) {
        btn_job.setOnClickListener {
            onJob()
        }
    }


    private fun onJob() {
        count+=1
        MyApplication.instance.getJobManager().addJobInBackground(TaskJob(count.toString()))
    }
}