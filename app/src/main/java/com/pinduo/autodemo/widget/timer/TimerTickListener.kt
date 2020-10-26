package com.pinduo.auto.widget.timer

interface TimerTickListener {
    fun onStart(name:String,job:String)
    fun onTick(tick:Long)
    fun onMark(mark:Long)
    fun onStop(name:String,job:String)

}