package com.pinduo.auto.error

import java.lang.RuntimeException

class AppException:RuntimeException {
    constructor():super()
    constructor(message:String):super(message)
}