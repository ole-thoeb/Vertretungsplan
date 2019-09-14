package com.example.eloem.vertretungsplan.helperClasses

import android.content.Context
import android.graphics.drawable.Animatable
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import com.example.eloem.vertretungsplan.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.lang.Error
import java.lang.IllegalArgumentException

class AnimatedIconFab @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = com.google.android.material.R.attr.floatingActionButtonStyle
) : FloatingActionButton(context, attrs, defStyleAttr) {
    
    enum class Icon {
        ADD, NEXT, CHECK, REFRESH;
        
        fun simpleDrawableRes(): Int = when(this) {
            ADD -> R.drawable.add_to_check
            NEXT -> R.drawable.next_to_refresh
            CHECK -> R.drawable.check_to_add
            REFRESH -> R.drawable.refresh_to_check
        }
    }
    
    private lateinit var currentIcon: Icon
    
    var icon: Icon
        get() = currentIcon
        set(value) {
            currentIcon = value
            setImageResource(icon.simpleDrawableRes())
        }
    
    fun animateToIcon(icon: Icon) {
        if (icon == currentIcon) return
        setAnimatableAndStart(getDrawableResTransition(currentIcon, icon))
        currentIcon = icon
    }
    
    private fun setAnimatableAndStart(@DrawableRes resourceId: Int) {
        val drawable = context.getDrawable(resourceId)
        val animatable = drawable as Animatable
        setImageDrawable(drawable)
        animatable.start()
    }
    
    private fun getDrawableResTransition(from: Icon, to: Icon): Int = when (from) {
        Icon.ADD -> when (to) {
            Icon.CHECK -> R.drawable.add_to_check
            Icon.REFRESH -> R.drawable.add_to_refresh
            Icon.NEXT -> R.drawable.add_to_next
            Icon.ADD -> throwTransitionError(from, to)
        }
        Icon.CHECK -> when (to) {
            Icon.CHECK -> throwTransitionError(from, to)
            Icon.REFRESH -> R.drawable.check_to_refresh
            Icon.NEXT -> R.drawable.check_to_next
            Icon.ADD -> R.drawable.check_to_add
        }
        Icon.REFRESH -> when (to) {
            Icon.CHECK -> R.drawable.refresh_to_check
            Icon.REFRESH -> throwTransitionError(from, to)
            Icon.NEXT -> R.drawable.refresh_to_next
            Icon.ADD -> R.drawable.refresh_to_add
        }
        Icon.NEXT -> when (to) {
            Icon.CHECK -> R.drawable.next_to_check
            Icon.REFRESH -> R.drawable.next_to_refresh
            Icon.NEXT -> throwTransitionError(from, to)
            Icon.ADD -> R.drawable.next_to_add
        }
    }
    
    private fun throwTransitionError(from: Icon, to: Icon): Nothing {
        throw IllegalArgumentException("can't transition from $from to $to")
    }
}