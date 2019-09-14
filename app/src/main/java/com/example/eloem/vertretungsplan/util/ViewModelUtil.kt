package com.example.eloem.vertretungsplan.util

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


inline fun <reified T: ViewModel> Fragment.fragmentViewModel(): ViewModelDelegateProvider<T> {
    return ViewModelDelegateProvider({ ViewModelProvider(this) }, T::class.java)
}

inline fun <reified T: ViewModel> Fragment.activityViewModel(): ViewModelDelegateProvider<T> {
    return ViewModelDelegateProvider({ ViewModelProvider(this.requireActivity()) }, T::class.java)
}

inline fun <reified T: ViewModel> AppCompatActivity.viewModel(): ViewModelDelegateProvider<T> {
    return ViewModelDelegateProvider({ ViewModelProvider(this) }, T::class.java)
}

class ViewModelDelegateProvider<T: ViewModel>(lazyFactory: () -> ViewModelProvider,
                                              private val viewModelClass: Class<T>): Lazy<T> {
    
    private var cached: T? = null
    
    private val factory by lazy(LazyThreadSafetyMode.NONE, lazyFactory)
    
    override val value: T
        get() {
            return cached ?: factory.get(viewModelClass).also { cached = it }
        }
    
    override fun isInitialized(): Boolean = cached == null
}