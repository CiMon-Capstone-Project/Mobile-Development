package com.example.cimon_chilimonitoring.ui.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.cimon_chilimonitoring.R

class customButtonLogin : AppCompatButton {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var txtColor: Int = 0
    private var enabledBackground: Drawable
    private var disabledBackground: Drawable
    private var clickableBackground: Drawable

    init {
        txtColor = ContextCompat.getColor(context, android.R.color.background_light)
        enabledBackground =
            ContextCompat.getDrawable(context, R.drawable.bg_button_enable) as Drawable
        disabledBackground =
            ContextCompat.getDrawable(context, R.drawable.bg_button_disable) as Drawable
        clickableBackground =
            ContextCompat.getDrawable(context, R.drawable.bg_button_disable) as Drawable
    }

    override fun onDraw(canvas: Canvas) {
        background = when {
            isEnabled -> enabledBackground
            isClickable -> clickableBackground
            else -> disabledBackground
        }
        txtColor = if (isEnabled) ContextCompat.getColor(
            context,
            android.R.color.background_light
        ) else ContextCompat.getColor(context, android.R.color.white)
        setTextColor(txtColor)
        textSize = 14f
        gravity = Gravity.CENTER
        text = if (isEnabled) "Masuk" else "Isi form terlebih dahulu"
        typeface = ResourcesCompat.getFont(context, R.font.plus_jakarta_sans)
        super.onDraw(canvas)
    }
}