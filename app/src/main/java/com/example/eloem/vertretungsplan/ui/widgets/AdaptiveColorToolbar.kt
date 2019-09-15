package com.example.eloem.vertretungsplan.ui.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.ColorInt
import com.example.eloem.vertretungsplan.util.textColorOn
import com.google.android.material.R
import com.google.android.material.appbar.MaterialToolbar

class AdaptiveColorToolbar @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = R.attr.toolbarStyle
) : MaterialToolbar(context, attrs, defStyleAttr) {
    
    override fun setBackgroundColor(@ColorInt color: Int) {
        super.setBackgroundColor(color)
        val colorOnBackground = textColorOn(color)
        navigationIcon = navigationIcon?.mutate()?.apply { setTint(colorOnBackground) }
        overflowIcon = overflowIcon?.mutate()?.apply { setTint(colorOnBackground) }
        collapseIcon = collapseIcon?.mutate()?.apply { setTint(colorOnBackground) }
        setTitleTextColor(colorOnBackground)
        setSubtitleTextColor(colorOnBackground)
    }
}