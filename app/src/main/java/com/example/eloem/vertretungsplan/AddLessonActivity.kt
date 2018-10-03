package com.example.eloem.vertretungsplan

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import com.example.eloem.vertretungsplan.helperClasses.Timetable
import com.example.eloem.vertretungsplan.util.*
import kotlinx.android.synthetic.main.activity_add_lesson.*
import java.util.*

class AddLessonActivity : AppCompatActivity() {
    
    private var day = 0
    private var lesson = 0
    private var color = Color.parseColor("#FFFAFAFA")
    private lateinit var timetable: Timetable
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_lesson)
        
        timetable = readTimetable(this)
        
        val extras = intent.extras
        if(extras != null){
            day = extras.getInt("Day")
            lesson = extras.getInt("Lesson")
        }
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        when(day){
            0 -> supportActionBar?.title = "${resources.getString(R.string.timetable_monday)}  ${lesson+1}. Stunde"
            1 -> supportActionBar?.title = "${resources.getString(R.string.timetable_tuesday)}  ${lesson+1}. Stunde"
            2 -> supportActionBar?.title = "${resources.getString(R.string.timetable_wednesday)}  ${lesson+1}. Stunde"
            3 -> supportActionBar?.title = "${resources.getString(R.string.timetable_thursday)}  ${lesson+1}. Stunde"
            4 -> supportActionBar?.title = "${resources.getString(R.string.timetable_friday)}  ${lesson+1}. Stunde"
        }
        
        //setup autocomplete text view
        val distinctLessons = timetable.distinctLessons()
        val autoList = ArrayList<String>()
        for (i in distinctLessons){
            autoList.add(i.pSubject)
        }
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, autoList)
        subjectAuto.setAdapter(adapter)
        //schließt soft keyboard, wenn vorschlag ausgewählt wird
        subjectAuto.setOnItemClickListener { _, _, _, _ ->
            val view = currentFocus
            view?.hideKeyboard()
            
            //vervollständigung der anderen felder
            val selected = subjectAuto.text.toString()
            for ((i, subject) in autoList.withIndex()){
                if (subject == selected){
                    teacher.setText(distinctLessons[i].pTeacher)
                    color = Color.parseColor(distinctLessons[i].pColor)
                    supportActionBar?.setBackgroundDrawable(ColorDrawable(color))
                    break
                }
            }
        }
        //schließt soft keyboard, wenn enter gedrückt wird
        subjectAuto.setOnEditorActionListener { textView, i, keyEvent ->
            if ((keyEvent != null && (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)){
                textView.hideKeyboard()
            }
            return@setOnEditorActionListener false
        }
        
        //setup color picker
        colorPicker.setColors(resources.getIntArray(R.array.lessonColor))
        colorPicker.setOnColorSelectedListener {
            color = it
            setToolbarColor(color)
        }
        
        //fill textviews
        subjectAuto.setText(timetable.getSubject(day, lesson))
        room.setText(timetable.getRoom(day, lesson))
        teacher.setText(timetable.getTeacher(day, lesson))
        //set color
        color = Color.parseColor(timetable.getColor(day, lesson))
        setToolbarColor(color)
        
        fabConfirm.setOnClickListener {
            timetable.changeContent(day = day, lesson = lesson, subject = subjectAuto.text.toString(),
                    room = room.text.toString(), teacher = teacher.text.toString().toUpperCase(), color = colorIntToString(color))
            writeTimetable(timetable, this)
            NavUtils.navigateUpFromSameTask(this)
        }
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        when(item.itemId){
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
            }
            R.id.delete ->{
                //build dialog
                val builder = AlertDialog.Builder(this)
                builder.setMessage(R.string.dialog_deleteLesson_message)
                        .setPositiveButton(R.string.ok, DialogInterface.OnClickListener(){ dialog, id ->
                            //delete lesson
                            timetable.changeContent(day, lesson)
                            writeTimetable(timetable, this)
                            NavUtils.navigateUpFromSameTask(this)
                        })
                        .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener(){ dialog, id ->
                            //do nothing
                        }).show()
            }
        
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }
    
    override fun onCreateOptionsMenu(menu: Menu):Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.add_lesson_actions, menu)
        return true
    }
    
    private fun setToolbarColor(newColor: Int){
        if(newColor != Color.parseColor("#FFFAFAFA")){ //wenn nicht weiß/ keine farbe gewählt
            supportActionBar?.setBackgroundDrawable(ColorDrawable(newColor))
            if (android.os.Build.VERSION.SDK_INT > 21) window.statusBarColor = differentshade(newColor, -0.15f)
        }
    }
    
    override fun onBackPressed() {
        NavUtils.navigateUpFromSameTask(this)
    }
}
