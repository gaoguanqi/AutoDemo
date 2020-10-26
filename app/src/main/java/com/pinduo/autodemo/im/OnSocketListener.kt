package com.pinduo.auto.im

import com.pinduo.auto.http.entity.TaskEntity


interface OnSocketListener {
    fun call(entity: TaskEntity)
}