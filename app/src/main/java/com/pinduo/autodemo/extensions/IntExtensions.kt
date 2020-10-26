package com.pinduo.auto.extensions

import com.pinduo.auto.app.config.Config

internal inline fun Int.isResultSuccess():Boolean{
    return Config.SUCCESS_CODE == this || Config.REBIND_CODE == this
}
