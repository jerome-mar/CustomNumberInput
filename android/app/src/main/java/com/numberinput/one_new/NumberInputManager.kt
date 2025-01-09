package com.numberinput.one_new

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp

class NumberInputManager : SimpleViewManager<FrameLayout>() {
    override fun getName(): String {
        return REACT_CLASS
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun createViewInstance(reactContext: ThemedReactContext): FrameLayout {
        val frameLayout = FrameLayout(reactContext)

        val editText = EditText(reactContext).apply {
            setTextColor(Color.BLACK)
            setHintTextColor(Color.GRAY)
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            imeOptions = EditorInfo.IME_ACTION_DONE
        }

        editText.id = View.generateViewId()

        frameLayout.addView(editText, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ))

        frameLayout.setOnTouchListener { _, _ -> true }

        return frameLayout
    }

    @ReactProp(name = "placeholder")
    fun setPlaceholder(view: FrameLayout, placeholder: String?) {
        val editText = view.getChildAt(0) as EditText
        editText.hint = placeholder
    }

    @ReactProp(name = "text")
    fun setValue(view: FrameLayout, text: String?) {
        val editText = view.getChildAt(0) as EditText

        editText.tag = true

        if (editText.text.toString() != text) {
            editText.setText(text)
        }

        editText.post {
            editText.tag = null
        }
    }

    @ReactProp(name = "textColor", customType = "Color")
    fun setTextColor(view: FrameLayout, color: Int?) {
        val editText = view.getChildAt(0) as EditText
        if (color != null) {
            editText.setTextColor(color)
        }
    }

    @ReactProp(name = "fontSize")
    fun setFontSize(view: FrameLayout, fontSize: Float) {
        val editText = view.getChildAt(0) as EditText
        editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize)
    }

    @ReactProp(name = "config")
    fun setInputConfig(view: FrameLayout, config: ReadableMap) {
        val editText = view.getChildAt(0) as EditText
        var preventDecimal = false
        var maxDecimal: Int? = null
        var maxInteger: Int? = null

        if (config.hasKey("preventDecimal") && !config.isNull("preventDecimal")) {
            preventDecimal = config.getBoolean("preventDecimal")
            if (preventDecimal) {
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
        }
        if (config.hasKey("maxDecimal") && !config.isNull("maxDecimal")) {
            maxDecimal = config.getInt("maxDecimal")
            editText.tag = maxDecimal
        }
        if (config.hasKey("maxInteger") && !config.isNull("maxInteger")) {
            maxInteger = config.getInt("maxInteger")
        }

        editText.filters = arrayOf<InputFilter>(
            DecimalInputFilter(preventDecimal, maxDecimal, maxInteger)
        )

        val context = editText.context as ThemedReactContext
        val numberTextWatcher = NumberTextWatcher(view, editText, maxDecimal ?: 0, context)
        editText.addTextChangedListener(numberTextWatcher)
    }

    override fun getExportedCustomBubblingEventTypeConstants(): Map<String, Any> {
        return MapBuilder.builder<String, Any>()
            .put("onChange", MapBuilder.of("phasedRegistrationNames",
                MapBuilder.of("bubbled", "onChange")
            ))
            .put("onFocus", MapBuilder.of("phasedRegistrationNames",
                MapBuilder.of("bubbled", "onFocus")
            ))
            .put("onBlur", MapBuilder.of("phasedRegistrationNames",
                MapBuilder.of("bubbled", "onBlur")
            ))
            .build()
    }

    companion object {
        const val REACT_CLASS: String = "NumberInput"
    }
}