package com.example.eloem.vertretungsplan

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.example.eloem.vertretungsplan.helperClasses.Timetable
import com.example.eloem.vertretungsplan.util.dpToPx
import com.example.eloem.vertretungsplan.util.isDarkColor
import com.example.eloem.vertretungsplan.util.readTimetable
import com.example.eloem.vertretungsplan.util.writeTimetable
import kotlinx.android.synthetic.main.activity_timetable.*

class TimetableActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timetable)
        fillTable()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = resources.getString(R.string.title_timetable)
    }
    
    override fun onCreateOptionsMenu(menu: Menu):Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.top_actions_timetable, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId){
        R.id.settings -> {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            true
        }
        R.id.deleteTimetable -> {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(R.string.dialog_deleteTimetable_message)
                    .setPositiveButton(R.string.ok, DialogInterface.OnClickListener(){ dialog, id ->
                        writeTimetable(Timetable(4, 10), this)
                        //refresh activity
                        finish()
                        startActivity(intent)
                    })
                    .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener(){ dialog, id ->
                        //do nothing
                    }).show()
            true
        }
        R.id.home -> {
            NavUtils.navigateUpFromSameTask(this)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
    
    private fun fillTable(){
        val dp5 = dpToPx(5f, this)//sehr recourcen aufwändig
        val dp45 = dpToPx(45f, this)
        val timetable = readTimetable(this)
        for(i in 0..10){
            //neue Zeile
            val tr = TableRow(this)
            tr.layoutParams = TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            tr.isBaselineAligned = false //damit multiline textview nicht nach unten geschoben ist
            
            //Stunde
            val tvLesson = TextView(this)
            tvLesson.text = (i+1).toString()
            tvLesson.textSize = 16f
            tvLesson.gravity = Gravity.CENTER
            tvLesson.setPadding(dp5, 0, dp5, 0)
            
            tr.addView(tvLesson)
            for(j in 0..4){
                //textfeld configurieren
                val tvSubject = TextView(this)
                tvSubject.text = timetable.getSubject(j, i)
                tvSubject.textSize = 15f
                tvSubject.setBackgroundColor(Color.parseColor(timetable.getColor(j, i)))
                if (isDarkColor(timetable.getColor(j, i))) tvSubject.setTextColor(Color.WHITE)
                else tvSubject.setTextColor(Color.BLACK)
                tvSubject.gravity = Gravity.CENTER
                tvSubject.minimumHeight = dp45
                tvSubject.width = 0
                tvSubject.setOnClickListener {
                    val editIntent = Intent(this, AddLessonActivity::class.java)
                    editIntent.putExtra("Day", j)
                    editIntent.putExtra("Lesson", i)
                    startActivity(editIntent)
                }
                tr.addView(tvSubject)
            }
            table.addView(tr)
        }
    }
}