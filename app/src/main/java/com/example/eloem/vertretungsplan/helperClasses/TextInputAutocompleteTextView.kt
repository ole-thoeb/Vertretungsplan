package com.example.eloem.vertretungsplan.helperClasses

import android.content.Context
import com.google.android.material.textfield.TextInputLayout
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatEditText
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import com.example.eloem.vertretungsplan.R

class TextInputAutocompleteTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                                              defStyleAttr: Int = R.attr.editTextStyle) :
        AppCompatAutoCompleteTextView(context, attrs, defStyleAttr) {
    
    private val textInputLayout: TextInputLayout?
        get() {
            var parent = this.parent
            while (parent is View) {
                if (parent is TextInputLayout) {
                    return parent
                }
                parent = parent.getParent()
            }
            
            return null
        }
    
    private val hintFromLayout: CharSequence? get() = textInputLayout?.hint
    
    override fun getHint(): CharSequence? {
        val layout = this.textInputLayout
        return layout?.hint ?: super.getHint()
    }
    
    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection? {
        val ic = super.onCreateInputConnection(outAttrs)
        if (ic != null && outAttrs.hintText == null) {
            outAttrs.hintText = this.hintFromLayout
        }
        
        return ic
    }
}
