package com.pinduo.auto.widget.observers

import android.text.TextUtils

class ObserverManager : SubjectListener {

    companion object {
        val instance: ObserverManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ObserverManager()
        }
    }


    /**
     * 观察者集合
     */
    private val map = hashMapOf<String,ObserverListener>()

    override fun add(key:String,observerListener: ObserverListener) {
        // 加入队列
        map.put(key,observerListener)
    }

    override fun notifyObserver(key:String,content: String) {
        // 通知观察者刷新数据
        map.forEach{(k, v) ->
            if(TextUtils.equals(key,k)){
                v.observer(content)
            }
        }
    }

    override fun remove(key:String) {
        // 从监听队列删除
        map.remove(key)
    }

}