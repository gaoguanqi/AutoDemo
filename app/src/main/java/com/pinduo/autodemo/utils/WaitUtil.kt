package com.pinduo.autodemo.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class WaitUtil {
    companion object{
        fun sleep(m:Long = 500L){
            //SystemClock.sleep(m)
            runBlocking {
                delay(m)
            }
        }
    }
}