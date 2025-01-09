package com.numberinput

import android.os.Bundle
import android.widget.EditText
import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled
import com.facebook.react.defaults.DefaultReactActivityDelegate
import com.numberinput.one_new.KeyboardStateEvent

class MainActivity : ReactActivity() {
    /**
     * Returns the name of the main component registered from JavaScript. This is used to schedule
     * rendering of the component.
     */
    override fun getMainComponentName(): String {
        return "NumberInput"
    }

    /**
     * Returns the instance of the [ReactActivityDelegate]. Here we use a util class [ ] which allows you to easily enable Fabric and Concurrent React
     * (aka React 18) with two boolean flags.
     */
    override fun createReactActivityDelegate(): ReactActivityDelegate =
        DefaultReactActivityDelegate(this, mainComponentName, fabricEnabled)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        KeyboardStateEvent.setEventListener(this) { isOpen->
            if (!isOpen) {
                val currentFocus = this.currentFocus
                currentFocus?.let { view ->
                    if (view is EditText) {
                        this.runOnUiThread {
                            view.clearFocus()  // Clear focus from EditText
                        }
                    }
                }
            }
        }
    }
}
