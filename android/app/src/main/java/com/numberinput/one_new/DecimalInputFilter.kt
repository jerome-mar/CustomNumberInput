package com.numberinput.one_new

import android.text.InputFilter
import android.text.Spanned

class DecimalInputFilter(
    private val preventDecimal: Boolean,
    private val maxDecimal: Int?,
    private val maxInteger: Int?
) : InputFilter {
    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dStart: Int, dEnd: Int): CharSequence? {
        val result = dest.toString() + source.toString()
        val parts = result.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (maxInteger != null && parts.isNotEmpty() && parts[0].length > maxInteger) {
            return ""
        }

        if (!preventDecimal && maxDecimal != null && parts.size > 1 && parts[1].length > maxDecimal) {
            return ""
        }

        return null
    }
}