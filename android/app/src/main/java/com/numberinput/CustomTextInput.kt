package com.numberinput

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import com.facebook.react.bridge.Arguments
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.events.RCTEventEmitter

class CustomTextInput : SimpleViewManager<FrameLayout>() {
    override fun getName(): String {
        return "CustomTextInput"
    }

    override fun createViewInstance(reactContext: ThemedReactContext): FrameLayout {
        val frameLayout = FrameLayout(reactContext)
        val editText = EditText(reactContext)

        frameLayout.id = View.generateViewId() // Gán ID cho FrameLayout

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val event = Arguments.createMap()
                event.putString("value", s.toString())
                Log.d("CustomTextInput", ": " + editText.id)
                Log.d("CustomTextInput", ": " + frameLayout.id)
                reactContext.getJSModule(RCTEventEmitter::class.java)
                    .receiveEvent(frameLayout.id, "onChange", event)
            }

            override fun afterTextChanged(s: Editable?) { }
        })

        frameLayout.addView(editText)
        return frameLayout
    }

    override fun getExportedCustomBubblingEventTypeConstants(): Map<String, Any> {
        return MapBuilder.builder<String, Any>()
            .put(
                "onChange",
                MapBuilder.of(
                    "phasedRegistrationNames",
                    MapBuilder.of("bubbled", "onChange")
                )
            )
            .build()
    }
}