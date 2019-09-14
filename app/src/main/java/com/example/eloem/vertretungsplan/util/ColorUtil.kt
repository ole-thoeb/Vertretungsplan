package com.example.eloem.vertretungsplan.util

import android.graphics.Color

val Int.isDarkColor get() =
    Color.red(this) * 0.299 +
            Color.green(this) * 0.587 +
            Color.blue(this) * 0.114 < 160

fun textColorOn(background: Int): Int = if (background.isDarkColor) Color.WHITE else Color.BLACK

fun differentShade (color: Int, difference: Float): Int{
    val factor = 1 + difference
    val hsv = FloatArray(3)
    Color.colorToHSV(color, hsv)
    hsv[2] = hsv[2] * factor
    return Color.HSVToColor(hsv)
}
