package com.pinduo.auto.widget.observers

interface SubjectListener {

    /**
     * 添加
     */
    fun add(key:String,observerListener: ObserverListener)

    /**
     * 通知的内容
     */
    fun notifyObserver(key:String,content: String)

    /**
     * 删除
     */
    fun remove(key:String)
}