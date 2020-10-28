package com.pinduo.autodemo.extensions

import com.pinduo.autodemo.app.config.Config

internal inline fun Int.isResultSuccess():Boolean{
    return Config.SUCCESS_CODE == this || Config.REBIND_CODE == this
}
