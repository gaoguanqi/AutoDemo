package com.pinduo.autodemo.app.global

import android.os.Environment


class Constants {
    object Path{
        val path = Environment.getExternalStorageDirectory().path
        val BASE_PATH:String = path + "/auto"
        val IMEI_PATH = BASE_PATH +"/imei.txt"
    }

    object ApiParams{
        const val USERNAME = "username"
        const val IMEI = "imei"
    }
    object SaveInfoKey {

    }

    object GlobalValue {
        const val douyinPackage = "com.ss.android.ugc.aweme"
        const val kuaishouPackage = "com.smile.gifmaker"

        const val plusTime:Long = 10L // 追加时间10秒
    }

    object BundleKey {

    }


    object Task{
        const val douyin = "1"
        const val kuaishou = "2"

        const val task1 = "1"
        const val task2 = "2"
        const val task3 = "3"
        const val task4 = "4"
        const val task5 = "5"
        const val task6 = "6"
        const val task9 = "9"

    }

    object Douyin {

        const val PAGE_MAIN = "com.ss.android.ugc.aweme.main.MainActivity"
        const val PAGE_LIVE_ROOM = "com.ss.android.ugc.aweme.live.LivePlayActivity"
        //主播头像弹出框
        const val PAGE_LIVE_ANCHOR= "com.bytedance.android.livesdk.widget.LiveBottomSheetDialog"

        //直播间购物车
        const val PAGE_LIVE_CART = "com.bytedance.android.livesdk.livecommerce.dialog.ECBottomDialog"
        //评论
        const val PAGE_LIVE_INPUT = "android.app.Dialog"
    }

    object IgnorePage{
        const val IGNORE_LIVE = "IgnoreLive"
        const val PAGE_LIVE1 = "com.bytedance.android.livesdk"

    }
}