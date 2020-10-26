package com.pinduo.auto.widget.timer


class MyScheduledExecutor private constructor(): Runnable {

    private var tick:Long = 0L
    private var totalTime:Long = 0L
    private var isRing:Boolean = false
    private var name:String = ""
    private var job:String = ""
    private var tickListener:TimerTickListener? = null

    companion object {
        val INSTANCE: MyScheduledExecutor by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED){
            MyScheduledExecutor()
        }
    }

    fun setListener(listener: TimerTickListener){
        this.tickListener = listener
    }


    fun isRing():Boolean{
        return isRing
    }

    fun onReStart(name:String,job:String,total:Long){
        if(total <= 0) return
        this.tick = 0L
        this.name = name
        this.job = job
        this.totalTime = total
        this.isRing = true
        this.tickListener?.onStart(name,job)
    }

    fun onStop(){
        this.isRing = false
        this.tickListener?.onStop(name,job)
    }

    override fun run() {
        this.tick++
        if(isRing()){
            if(tick < totalTime){
                val t:Long = tick % 10
                if(t == 0L){
                    this.tickListener?.onTick(tick)
                }
            }else if(tick == totalTime){
                this.onStop()
            }
        }
        val mark:Long = tick % 100
        if(mark == 0L){
            this.tickListener?.onMark(mark)
        }
    }


}