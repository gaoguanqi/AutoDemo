package com.pinduo.autodemo.core.data

import java.io.Serializable


class TaskData(var isExecute:Boolean = false, var priority:Int = 999,val task:String = "",val zxTime:Long = 0, val content:String = "~"): Serializable