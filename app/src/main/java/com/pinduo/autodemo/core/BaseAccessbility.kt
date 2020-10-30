package com.pinduo.autodemo.core

import android.accessibilityservice.AccessibilityService
abstract class BaseAccessbility<out T> {

    lateinit var service: AccessibilityService

    open fun initService(service:AccessibilityService):T{
        this.service = service
        return this as T
    }


}