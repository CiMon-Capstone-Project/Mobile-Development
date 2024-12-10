package com.example.cimon_chilimonitoring.ui.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.view.marginStart
import com.example.cimon_chilimonitoring.R

class PasswordEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    private var showButtonImage: Drawable
    private val startIconImage =
        ContextCompat.getDrawable(context, R.drawable.ic_baseline_lock_24) as Drawable

    init {
        showButtonImage =
            ContextCompat.getDrawable(context, R.drawable.visibility_24dp_black) as Drawable
        inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD // to hide password input
        setButtonDrawables(startOfTheText = startIconImage)
        setOnTouchListener(this)

        // batasi inputan hanya 1 baris saja
        maxLines = 1

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            // cek password minimal 8 karakter
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString()
                if (password.length < 8) {
                    error = "Password minimal 8 Karakter"
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing.
            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = context.getString(R.string.hint_password)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        textSize = 16f
    }

    private fun showClearButton() {
        setButtonDrawables(
            endOfTheText = showButtonImage,
            endPadding = 16
        )
        setButtonDrawables(startOfTheText = startIconImage)
    }

    private fun hideClearButton() {
        setButtonDrawables(endPadding = 16)
        setButtonDrawables(startOfTheText = startIconImage)
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null,
        endPadding: Int = 0
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
        compoundDrawablePadding = endPadding + resources.getDimensionPixelSize(R.dimen.margin_pw)
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isClearButtonClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (showButtonImage.intrinsicWidth + marginStart).toFloat()
                when {
                    event.x < clearButtonEnd -> isClearButtonClicked = true
                }
            } else {
                clearButtonStart = (width - paddingEnd - showButtonImage.intrinsicWidth).toFloat()
                when {
                    event.x > clearButtonStart -> isClearButtonClicked = true
                }
            }

            if (isClearButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        showButtonImage = ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_close_24
                        ) as Drawable
                        showClearButton()
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        showButtonImage = ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_close_24
                        ) as Drawable
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
}
