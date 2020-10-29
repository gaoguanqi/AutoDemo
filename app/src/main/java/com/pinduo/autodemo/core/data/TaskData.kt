package com.pinduo.autodemo.core.data

import java.io.Serializable


class TaskData(var isExecute:Boolean = false, val delayTime:Long = 1000L,val task:String = "",val zxTime:Long = 0, val content:String = "~"): Serializable