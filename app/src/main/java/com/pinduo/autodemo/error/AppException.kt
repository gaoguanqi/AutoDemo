package com.pinduo.autodemo.error

import java.lang.RuntimeException

class AppException:RuntimeException {
    constructor():super()
    constructor(message:String):super(message)
}