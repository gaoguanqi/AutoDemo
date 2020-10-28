package com.pinduo.autodemo.core.job

import com.birbit.android.jobqueue.Job
import com.birbit.android.jobqueue.Params
import com.pinduo.autodemo.app.MyApplication
import com.pinduo.autodemo.core.data.TaskData

abstract class BaseJob(val data: TaskData): Job(Params(PRIORITY).requireNetwork().persist().groupBy("job").addTags("job")) {

    companion object {
        const val PRIORITY = 1
    }
}