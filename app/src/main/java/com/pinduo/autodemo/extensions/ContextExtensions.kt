package com.pinduo.autodemo.extensions

import android.content.Context
import android.view.LayoutInflater

internal inline val Context.layoutInflater: LayoutInflater get() = LayoutInflater.from(this)
