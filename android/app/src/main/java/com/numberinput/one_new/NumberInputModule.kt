package com.numberinput.one_new

import android.content.Context
import android.os.Build
import android.view.inputmethod.InputMethodManager
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.WritableMap
import java.text.DecimalFormatSymbols
import java.util.*

class NumberInputModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    override fun getName(): String {
        return "NumberInputModule"
    }

    private fun getSystemLocale(reactContext: ReactApplicationContext): Locale {
        val config = reactContext.resources.configuration

        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> config.locales[0]
            else -> config.locale
        }
    }

    private fun getNumberFormatSettings(reactContext: ReactApplicationContext): WritableMap {
        val symbols = DecimalFormatSymbols(getSystemLocale(reactContext))

        return Arguments.createMap().apply {
            putString("decimalSeparator", symbols.decimalSeparator.toString())
            putString("groupingSeparator", symbols.groupingSeparator.toString())
        }
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    fun getNumberFormatSettings(): WritableMap {
        return getNumberFormatSettings(reactApplicationContext)
    }

    @ReactMethod
    fun dismissKeyboard() {
        val inputMethodManager = reactApplicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentActivity = currentActivity
        currentActivity?.let {
            val currentFocus = it.currentFocus
            currentFocus?.let { view ->
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }
}