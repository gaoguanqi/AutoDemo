package com.pinduo.autodemo.widget.observers

interface SubjectListener {

    /**
     * 添加
     */
    fun add(observerListener: ObserverListener)

    /**
     * 通知的内容
     */
    fun notifyObserver(content: String)

    /**
     * 删除
     */
    fun remove()
}