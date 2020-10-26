package com.pinduo.autodemo.utils

import android.content.Context
import android.provider.Settings
import android.text.TextUtils
import com.pinduo.autodemo.service.MyAccessibilityService

class AccessibilityServiceUtils {
    companion object{
         fun isAccessibilitySettingsOn(context: Context): Boolean {
            var accessibilityEnabled = 0
            val service =
                context.packageName + "/" + MyAccessibilityService::class.java.canonicalName
            try {
                accessibilityEnabled = Settings.Secure.getInt(
                    context.applicationContext.contentResolver,
                    Settings.Secure.ACCESSIBILITY_ENABLED
                )
            } catch (e: Settings.SettingNotFoundException) {
                //Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.message)
            }
            val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')
            if (accessibilityEnabled == 1) {
                val settingValue = Settings.Secure.getString(
                    context.applicationContext.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                )
                if (settingValue != null) {
                    mStringColonSplitter.setString(settingValue)
                    while (mStringColonSplitter.hasNext()) {
                        val accessibilityService = mStringColonSplitter.next()
                        if (accessibilityService.equals(service, ignoreCase = true)) {
                            return true
                        }
                    }
                }
            } else {
                //Log.v(TAG, "***ACCESSIBILITY IS DISABLED***")
            }
            return false
        }
    }
}