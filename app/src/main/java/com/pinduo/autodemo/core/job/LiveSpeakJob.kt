package com.pinduo.autodemo.core.job

import com.birbit.android.jobqueue.RetryConstraint
import com.pinduo.autodemo.core.LivePlayAccessibility
import com.pinduo.autodemo.core.data.TaskData
import com.pinduo.autodemo.utils.LogUtils

class LiveSpeakJob(data: TaskData):BaseJob(data) {

    @Throws(Throwable::class)
    override fun onRun() {
        LogUtils.logGGQ("job onRun:${data}")
        LivePlayAccessibility.INSTANCE.doSpeak(data.content)
    }

    override fun shouldReRunOnThrowable(
        throwable: Throwable,
        runCount: Int,
        maxRunCount: Int
    ): RetryConstraint {
        LogUtils.logGGQ("job shouldReRunOnThrowable")
        return RetryConstraint.createExponentialBackoff(runCount, 1000)
    }

    override fun onAdded() {
        LogUtils.logGGQ("job onAdded")
    }

    override fun onCancel(cancelReason: Int, throwable: Throwable?) {
        LogUtils.logGGQ("job onCancel")
    }
}