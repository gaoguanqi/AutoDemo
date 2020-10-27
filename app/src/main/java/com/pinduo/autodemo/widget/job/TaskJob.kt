package com.pinduo.autodemo.widget.job

import androidx.annotation.Nullable
import com.birbit.android.jobqueue.CancelReason
import com.birbit.android.jobqueue.Job
import com.birbit.android.jobqueue.Params
import com.birbit.android.jobqueue.RetryConstraint
import com.blankj.utilcode.util.ToastUtils
import com.pinduo.autodemo.app.MyApplication
import com.pinduo.autodemo.utils.LogUtils


class TaskJob(val text: String?) : Job(
    Params(PRIORITY).requireNetwork().persist()
) {
    override fun onAdded() {
        LogUtils.logGGQ("job onAdded")
    }

    @Throws(Throwable::class)
    override fun onRun() {
        LogUtils.logGGQ("job onRun:${text}")
        MyApplication.instance.getUiHandler().postDelayed({
            ToastUtils.showShort("任务：${text}")
        },5000)
    }

    override fun shouldReRunOnThrowable(
        throwable: Throwable, runCount: Int,
        maxRunCount: Int
    ): RetryConstraint {
        LogUtils.logGGQ("job shouldReRunOnThrowable")

        return RetryConstraint.createExponentialBackoff(runCount, 1000)
    }

    override fun onCancel(
        @CancelReason cancelReason: Int,
        @Nullable throwable: Throwable?) {
        LogUtils.logGGQ("job onCancel")

    }

    companion object {
        const val PRIORITY = 1
    }
}