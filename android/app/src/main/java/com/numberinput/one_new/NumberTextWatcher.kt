package com.numberinput.one_new

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.FrameLayout
import com.facebook.react.bridge.Arguments
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.events.RCTEventEmitter
import java.math.BigDecimal
import java.math.BigInteger
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class NumberTextWatcher(
    private val frameLayout: FrameLayout,
    private val editText: EditText,
    maxDecimal: Int,
    private val reactContext: ThemedReactContext
) : TextWatcher {
    private val decimalFormat: DecimalFormat
    private val decimalFormatNoFractional: DecimalFormat
    private var hasFractionalPart: Boolean

    init {
        val patternInt = "#,###"
        val patternDec = patternInt + "." + replicate('#', maxDecimal)
        decimalFormat = DecimalFormat(patternDec, DecimalFormatSymbols(Locale.getDefault()))
        decimalFormat.isDecimalSeparatorAlwaysShown = true
        decimalFormatNoFractional = DecimalFormat(patternInt, DecimalFormatSymbols(Locale.getDefault()))
        hasFractionalPart = false

        editText.setOnFocusChangeListener {_, hasFocus ->
            if (hasFocus) {
                sendEventToReact("onFocus", "")
            } else {
                sendEventToReact("onBlur", "")
            }
        }
    }

    private fun sendEventToReact(eventName: String, text: String) {
        val event = Arguments.createMap()
        event.putString("text", text)

        reactContext.getJSModule(RCTEventEmitter::class.java)
            .receiveEvent(frameLayout.id, eventName, event)
    }

    override fun afterTextChanged(s: Editable) {
        editText.removeTextChangedListener(this)

        try {
            if (s.toString().isEmpty()) {
                sendEventToReact("onChange", "")
            } else {
                val iniLength = editText.text.length

                val v = s.toString()
                    .replace(decimalFormat.decimalFormatSymbols.groupingSeparator.toString(), "")

                val cursorPosition = editText.selectionStart

                val formattedText = if (hasFractionalPart) {
                    val bigDecimal = BigDecimal(v)
                    decimalFormat.format(bigDecimal)
                } else {
                    val bigInteger = BigInteger(v)
                    decimalFormatNoFractional.format(bigInteger)
                }

                editText.setText(formattedText)

                val endLength = editText.text.length
                val sel = (cursorPosition + (endLength - iniLength))
                if (sel > 0 && sel <= editText.text.length) {
                    editText.setSelection(sel)
                } else {
                    editText.setSelection(editText.text.length - 1)
                }

                if (editText.tag == null) {
                    sendEventToReact("onChange", formattedText)
                }
            }
        } catch (nfe: NumberFormatException) {
            // TODO
        } finally {
            editText.addTextChangedListener(this)
        }

    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        hasFractionalPart =
            s.toString().contains(decimalFormat.decimalFormatSymbols.decimalSeparator.toString())
    }

    private fun replicate(c: Char, n: Int): String {
        return String(CharArray(n)).replace("\u0000", "" + c)
    }

    companion object {
        @Suppress("unused")
        private val TAG = "NumberTextWatcher"
    }
}