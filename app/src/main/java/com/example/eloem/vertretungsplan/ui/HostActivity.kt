package com.example.eloem.vertretungsplan.ui

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.findNavController
import androidx.room.RoomDatabase
import com.example.eloem.vertretungsplan.R
import com.example.eloem.vertretungsplan.database.PlanRoomDatabase
import com.example.eloem.vertretungsplan.helperClasses.AnimatedIconFab
import com.example.eloem.vertretungsplan.util.removeOnClickListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_host.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.defaultSharedPreferences

class HostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)
        setSupportActionBar(toolbar)
        
//        runBlocking(Dispatchers.IO) {
//            PlanRoomDatabase.getDatabase(this@HostActivity).clearAllTables()
//        }
        
        
        mainFab.icon = AnimatedIconFab.Icon.ADD
    }

    override fun onSupportNavigateUp() =
        findNavController(R.id.navHostFragment).navigateUp()
    
    val rootView: View by lazy { findViewById<CoordinatorLayout>(R.id.rootCoordinator) }
    
    val mainFab: AnimatedIconFab by lazy { findViewById<AnimatedIconFab>(R.id.mainFab) }
    
    val toolbar: Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    
    fun hideFab() {
//        val p = mainFab.layoutParams as CoordinatorLayout.LayoutParams
//        p.anchorId = View.NO_ID
//        p.gravity = Gravity.END or Gravity.BOTTOM
//        mainFab.layoutParams = p
        mainFab.removeOnClickListener()
        mainFab.hide()
    }
    
    fun showFab(onClick: (View) -> Unit) {
//        val p = mainFab.layoutParams as CoordinatorLayout.LayoutParams
//        p.anchorId = R.id.bottomSheet
//        p.gravity = Gravity.NO_GRAVITY
//        mainFab.layoutParams = p
        mainFab.setOnClickListener(onClick)
        mainFab.show()
    }
}
