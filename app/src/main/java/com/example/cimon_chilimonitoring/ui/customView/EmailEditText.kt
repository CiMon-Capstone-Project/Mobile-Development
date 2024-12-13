package com.example.cimon_chilimonitoring.ui.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.cimon_chilimonitoring.R

class EmailEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : AppCompatEditText(context, attrs), View.OnTouchListener {
    private var clearButton: Drawable
    val startIconImage =
        ContextCompat.getDrawable(context, R.drawable.ic_baseline_email_24) as Drawable

    init {
        clearButton = ContextCompat.getDrawable(context, R.drawable.ic_close_24) as Drawable
        setButtonDrawables(startOfTheText = startIconImage)
        setOnTouchListener(this)

        // batasi inputan hanya 1 baris saja
        maxLines = 1
        inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            // show clear button when there is a text change
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(text.toString()).matches()) {
                    error = context.getString(R.string.warning_incorrect_email_format)
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isClearButtonClicked = false

            // "RTL = Right to Left" in case future me forgor
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (clearButton.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < clearButtonEnd -> isClearButtonClicked = true
                }
            } else {
                clearButtonStart = (width - paddingEnd - clearButton.intrinsicWidth).toFloat()
                when {
                    event.x > clearButtonStart -> isClearButtonClicked = true
                }
            }

            if (isClearButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> { // action_down = ketika tombol ditekan
                        clearButton =
                            ContextCompat.getDrawable(context, R.drawable.ic_close_24) as Drawable
                        showClearButton()
                        return true
                    }

                    MotionEvent.ACTION_UP -> { // ketika tombol dilepas setelah ditekan
                        clearButton =
                            ContextCompat.getDrawable(context, R.drawable.ic_close_24) as Drawable
                        when {
                            text != null -> text?.clear()
                        }
                        hideClearButton()
                        return true
                    }

                    else -> return false
                }
            } else return false
        }
        return false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = context.getString(R.string.example_email_format)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        textSize = 16f
    }

    private fun showClearButton() {
        setButtonDrawables(endOfTheText = clearButton)
        setButtonDrawables(startOfTheText = startIconImage)
    }

    private fun hideClearButton() {
        setButtonDrawables()
        setButtonDrawables(startOfTheText = startIconImage)
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        // menampilkan gambar pada EditText (left, top, right, bottom)
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }
}