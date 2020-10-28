package com.pinduo.autodemo.im

import com.pinduo.autodemo.http.entity.TaskEntity


interface OnSocketListener {
    fun call(entity: TaskEntity)
}