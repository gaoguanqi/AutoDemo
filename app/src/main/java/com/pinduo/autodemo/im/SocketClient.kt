package com.pinduo.auto.im

import android.text.TextUtils
import com.google.gson.Gson
import com.pinduo.auto.app.config.Config
import com.pinduo.auto.http.entity.TaskEntity
import com.pinduo.autodemo.app.MyApplication
import com.pinduo.autodemo.utils.IMEIUtils
import com.pinduo.autodemo.utils.LogUtils
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.engineio.client.transports.WebSocket
import java.net.URISyntaxException

class SocketClient private constructor(){

    //任务消息
    private val EVENT_TASK:String = "task"

    //收到消息回馈
    private val RECEIVE_STATUS:String = "receive_status"

    private var socket:Socket? = null
    private var listener:OnSocketListener? = null

    private val uiHandler by lazy { MyApplication.instance.getUiHandler() }


    companion object{
        val instance: SocketClient by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED){
            SocketClient()
        }
    }

    init {
        val opts:IO.Options = IO.Options()
        opts.reconnectionDelay = 1000L
        opts.transports = arrayOf(WebSocket.NAME)
        try {
            socket = IO.socket(Config.IO_SERVER_URL,opts)
        }catch (e: URISyntaxException){
            e.printStackTrace()
        }
        socket?.on(Socket.EVENT_CONNECT,object : Emitter.Listener{
            override fun call(vararg args: Any?) {
                socket?.let {
                    if(it.connected()){
                        uiHandler.sendMessage("已连接："+ it.id())
                        val imei:String = IMEIUtils.getIMEI()
                        if(TextUtils.isEmpty(imei)){
                            uiHandler.sendMessage("imei empty")
                        }else{
                            uiHandler.sendMessage(imei)
                            sendMessage("login",imei)
                            LogUtils.logGGQ("imei:${imei}")
                        }
                    }
                }
            }
        })?.on(Socket.EVENT_CONNECTING,object : Emitter.Listener{
            override fun call(vararg args: Any?) {
                LogUtils.logGGQ("EVENT_CONNECTING")
                uiHandler.sendMessage("连接中...")
            }
        })?.on(Socket.EVENT_DISCONNECT,object : Emitter.Listener{
            override fun call(vararg args: Any?) {
                LogUtils.logGGQ("EVENT_DISCONNECT")
                uiHandler.sendMessage("连接断开")
            }
        })?.on(Socket.EVENT_CONNECT_TIMEOUT,object : Emitter.Listener{
            override fun call(vararg args: Any?) {
                LogUtils.logGGQ("EVENT_CONNECT_TIMEOUT")
                uiHandler.sendMessage("连接超时")
            }
        })?.on(Socket.EVENT_ERROR,object : Emitter.Listener{
            override fun call(vararg args: Any?) {
                LogUtils.logGGQ("EVENT_ERROR")
                uiHandler.sendMessage("连接错误")
            }
        })?.on(Socket.EVENT_RECONNECT,object : Emitter.Listener{
            override fun call(vararg args: Any?) {
                LogUtils.logGGQ("EVENT_RECONNECT")
                uiHandler.sendMessage("重连")
            }
        })?.on(Socket.EVENT_RECONNECTING,object : Emitter.Listener{
            override fun call(vararg args: Any?) {
                LogUtils.logGGQ("EVENT_RECONNECTING")
                uiHandler.sendMessage("重连中...")
            }
        })?.on(Socket.EVENT_RECONNECT_FAILED,object : Emitter.Listener{
            override fun call(vararg args: Any?) {
                LogUtils.logGGQ("EVENT_RECONNECT_FAILED")
                uiHandler.sendMessage("重连失败")
            }
        })?.on(Socket.EVENT_RECONNECT_ERROR,object : Emitter.Listener{
            override fun call(vararg args: Any?) {
                LogUtils.logGGQ("EVENT_RECONNECT_ERROR")
                uiHandler.sendMessage("重连错误")
            }
        })?.on(EVENT_TASK,object :Emitter.Listener{
            override fun call(vararg args: Any?) {
                try {
                    args?.let {
                        val arg = it[0]
                        arg?.let {
                            LogUtils.logGGQ("收到消息:${it}")
                            val taskEntity:TaskEntity = Gson().fromJson<TaskEntity>(it.toString(),TaskEntity::class.java)
                            listener?.let { it.call(taskEntity) }?:let {  LogUtils.logGGQ("listener is null") }
                        }?:let {
                            LogUtils.logGGQ("arg is null")
                        }
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    LogUtils.logGGQ("${EVENT_TASK} error -->>>${e}")
                    uiHandler.sendMessage("数据异常！！！")
                }
            }
        })
    }

    fun setListener(listener: OnSocketListener){
        this.listener = listener
    }

    fun onConnect(){
        socket?.connect()
    }

    private fun sendMessage(msg:String){
        sendMessage(EVENT_TASK,msg)
    }

    private fun sendMessage(key:String,msg:String){
        socket?.emit(key,msg)
        uiHandler.sendMessage("${key}---${msg}")
    }

    // 回馈
    fun onReceiveStatus(){
        sendMessage(RECEIVE_STATUS,"1")
    }

    // 父任务成功
    fun sendParentSuccess(){
        sendMessage(EVENT_TASK,"status=101")
    }

    // 父任务失败
    fun sendParentError(){
        sendMessage(EVENT_TASK,"status=100")
    }

    // 子任务成功
    fun sendSuccess(){
        sendMessage(EVENT_TASK,"status=1")
    }

    // 子任务失败
    fun sendError(){
        sendMessage(EVENT_TASK,"status=0")
    }


}