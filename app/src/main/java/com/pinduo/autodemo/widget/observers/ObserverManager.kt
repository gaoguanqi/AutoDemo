package com.pinduo.autodemo.widget.observers


class ObserverManager : SubjectListener {

    companion object {
        val instance: ObserverManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ObserverManager()
        }
    }


    /**
     * 观察者集合
     */
    private val list = mutableListOf<ObserverListener>()

    override fun add(observerListener: ObserverListener) {
        // 加入队列
//        map.put(key, observerListener)
        list.add(observerListener)
    }

    override fun notifyObserver(content: String) {
        // 通知观察者刷新数据
//        map.forEach { (k, v) ->
//            if (TextUtils.equals(key, k)) {
//                v.observer(content)
//            }
//        }

        list.forEach {
            it.observer(content)
        }


    }

    override fun remove() {
        // 从监听队列删除
//        map.remove(key)
        list.clear()
    }

}