package com.example.eloem.vertretungsplan.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import com.example.eloem.vertretungsplan.R
import com.example.eloem.vertretungsplan.widget.VerPlanWidgetProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.dialog_edit_text.view.*


fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.focusAndShowKeyboard(showFlag: Int = InputMethodManager.SHOW_FORCED) {
    requestFocusFromTouch()
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(showFlag, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

var Window.isLightStatusBar: Boolean
    set(value) {
        var flags = decorView.systemUiVisibility
        //Log.d("isLightStatusBar", View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.toString(2))
        //Log.d("isLightStatusBar", View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv().toString(2))
        //Log.d("isLightStatusBar", flags.toString(2))
        flags = if (value) {
            flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
        //Log.d("isLightStatusBar", flags.toString(2))
        decorView.systemUiVisibility = flags
    }
    get() = decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR != 0

fun Window.setStatusBarAndIconColors(newStatusBarColor: Int) {
    statusBarColor = newStatusBarColor
    isLightStatusBar = !newStatusBarColor.isDarkColor
}

fun View.removeOnClickListener() {
    setOnClickListener(null)
}

fun View.setNoDoubleClickListener(timeInterval: Long = 500, action: (View) -> Unit){
    setOnClickListener(makeNoDoubleActivation(timeInterval, action))
}

fun <T> makeNoDoubleActivation(coolDown: Long, action: (T) -> Unit): (T) -> Unit {
    var lastClick = 0L
    return {
        val currTime = System.currentTimeMillis()
        if (lastClick + coolDown < currTime) {
            lastClick = currTime
            action(it)
        }
    }
}

/**
 * observe `this` [LiveData] skipping `null` null values
 * @param owner determines the lifespan of the added [Observer]
 * @param onChange code block that is invoked when the value of the [LiveData] changes and is not `null`
 * @return the [Observer] added to the [LiveData]
 */
inline fun <T> LiveData<T?>.observeNotNull(owner: LifecycleOwner, crossinline onChange: (T) -> Unit) : Observer<T?> {
    return observe(owner) {
        if (it != null) onChange(it)
        else Log.e("Util", "value was null and is ignored")
    }
}

inline fun Context.editDialog(
        hint: Int,
        startString: String,
        positiveText: Int,
        negativeText: Int,
        crossinline invalid: (CharSequence?) -> Boolean,
        errorMessage: Int,
        crossinline positiveAction: (String, DialogInterface, Int) -> Unit,
        crossinline negativeAction: (DialogInterface, Int) -> Unit = { _, _ -> },
        hideSoftInput: Boolean = true
) {
    editDialog(
            hint = resources.getString(hint),
            startString = startString,
            positiveText = resources.getString(positiveText),
            negativeText = resources.getString(negativeText),
            invalid = invalid,
            errorMessage = resources.getString(errorMessage),
            positiveAction = positiveAction,
            negativeAction = negativeAction,
            hideSoftInput = hideSoftInput
    )
}

inline fun Context.editDialog(
        hint: String,
        startString: String,
        positiveText: String,
        negativeText: String,
        crossinline invalid: (CharSequence?) -> Boolean,
        errorMessage: String,
        crossinline positiveAction: (String, DialogInterface, Int) -> Unit,
        crossinline negativeAction: (DialogInterface, Int) -> Unit = { _, _ -> },
        hideSoftInput: Boolean = true
) {
    var content = ""
    @SuppressLint("InflateParams")
    val custView = LayoutInflater
            .from(this)
            .inflate(R.layout.dialog_edit_text, null, false)
    custView.valueET.apply {
        setText(startString)
        setSelection(startString.length)
        doOnTextChanged { text, _, _, _ ->
            if (invalid(text)) {
                error = resources.getString(R.string.dialog_newTimetable_errorMsg)
            } else {
                content = text.toString()
                error = null
            }
        }
        focusAndShowKeyboard()
    }
    custView.textInputLayout.hint = hint
    
    MaterialAlertDialogBuilder(this)
            .setView(custView)
            .setPositiveButton(positiveText) { dialog, which ->
                if (hideSoftInput) custView.hideKeyboard()
                if (invalid(content)) {
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT)
                            .show()
                    return@setPositiveButton
                }
                positiveAction(content, dialog, which)
            }
            .setNegativeButton(negativeText) { dialog, which ->
                if (hideSoftInput) custView.hideKeyboard()
                negativeAction(dialog, which)
            }
            .setCancelable(false)
            .show()
}


fun Context.getAttribute(resourceId: Int, resolveRef: Boolean): TypedValue {
    val tv = TypedValue()
    theme.resolveAttribute(resourceId, tv, resolveRef)
    return tv
}

fun Context.refreshVerPlanWidget() {
    sendBroadcast(Intent(this, VerPlanWidgetProvider::class.java).apply {
        action = VerPlanWidgetProvider.ACTION_REFRESH
    })
}

fun Context.updateVerPlanWidget() {
    sendBroadcast(Intent(this, VerPlanWidgetProvider::class.java).apply {
        action = VerPlanWidgetProvider.ACTION_UPDATE
    })
}