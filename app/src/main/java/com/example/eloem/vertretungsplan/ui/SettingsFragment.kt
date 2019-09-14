package com.example.eloem.vertretungsplan.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.eloem.vertretungsplan.R

class SettingsFragment : PreferenceFragmentCompat() {
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_settings, rootKey)
        
        findPreference<ListPreference>("settingsTheme")?.setOnPreferenceChangeListener { preference, newValue ->
            val themeMode = when(newValue) {
                "0" -> AppCompatDelegate.MODE_NIGHT_NO
                "1" -> AppCompatDelegate.MODE_NIGHT_YES
                "2" -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                else -> throw Error("Unknown theme option: $newValue")
            }
        
            AppCompatDelegate.setDefaultNightMode(themeMode)
        
            true
        }
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    
        (activity as? HostActivity)?.apply{
            supportActionBar?.apply {
                title = resources.getString(R.string.action_settings)
                setDisplayShowTitleEnabled(true)
                setDisplayShowCustomEnabled(false)
                setDisplayHomeAsUpEnabled(true)
            }
            
            hideFab()
        }
    }
    
//    override fun onCreateRecyclerView(inflater: LayoutInflater?, parent: ViewGroup?, savedInstanceState: Bundle?): RecyclerView {
//        val rv = super.onCreateRecyclerView(inflater, parent, savedInstanceState)
//        rv.setBackgroundColor(requireContext().getAttribute(R.attr.backgroundColor).data)
//        return rv
//    }
}
