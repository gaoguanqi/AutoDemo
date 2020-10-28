package com.pinduo.autodemo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pinduo.autodemo.R
import com.pinduo.autodemo.app.MyApplication
import com.pinduo.autodemo.core.job.TaskJob
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : AppCompatActivity() {
    var count:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)


        btn_job.setOnClickListener {
            onJob()
        }
    }


    private fun onJob() {
        count+=1
        MyApplication.instance.getJobManager().addJobInBackground(TaskJob(count.toString()))
    }
}