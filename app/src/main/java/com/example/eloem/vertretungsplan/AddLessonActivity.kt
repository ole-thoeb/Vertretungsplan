package com.example.eloem.vertretungsplan

import android.app.AlertDialog
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

class AddLessonActivity : AppCompatActivity() {
    
    private var day = 0
    private var l = 0
    private lateinit var lesson: Timetable.Lesson
    private var color = Timetable.Lesson.DEFAULT_COLOR
    private lateinit var timetable: Timetable
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    
        val darkTheme by booleanPref(SETTINGS_THEME_KEY)
        setTheme(if (darkTheme) R.style.DarkAppTheme else R.style.AppTheme)
        
        setContentView(R.layout.activity_add_lesson)
        
        timetable = getLatestTimetable(this)
        
        val extras = intent.extras
        day = extras?.getInt(EXTRA_DAY, 0) ?: 0
        l = extras?.getInt(EXTRA_LESSON, 0) ?: 0
        lesson = timetable[day][l]
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        when(day){
            0 -> supportActionBar?.title = resources.getString(R.string.addLessonTitle, resources.getString(R.string.timetable_monday), l + 1)
            1 -> supportActionBar?.title = resources.getString(R.string.addLessonTitle, resources.getString(R.string.timetable_tuesday), l + 1)
            2 -> supportActionBar?.title = resources.getString(R.string.addLessonTitle, resources.getString(R.string.timetable_wednesday), l + 1)
            3 -> supportActionBar?.title = resources.getString(R.string.addLessonTitle, resources.getString(R.string.timetable_thursday), l + 1)
            4 -> supportActionBar?.title = resources.getString(R.string.addLessonTitle, resources.getString(R.string.timetable_friday), l + 1)
        }
        
        //setup autocomplete text view
        val distinctLessons = timetable.distinctLessons
        val autoList = List(distinctLessons.size) { distinctLessons[it].subject}
       
        val adapter = ArrayAdapter<String>(this, R.layout.expandable_list_item, autoList)
        subjectAuto.setAdapter(adapter)
        //schließt soft keyboard, wenn vorschlag ausgewählt wird
        subjectAuto.setOnItemClickListener { adapterView, view, i, l ->
            view?.hideKeyboard()
            
            //vervollständigung der anderen felder
            val selected = subjectAuto.text.toString()
            val selectedLesson = distinctLessons.find { it.subject == selected}
            teacher.setText(selectedLesson?.teacher)
            color = selectedLesson?.color ?: Timetable.Lesson.DEFAULT_COLOR
            setToolbarColor(color)
        }
        //schließt soft keyboard, wenn enter gedrückt wird
        subjectAuto.setOnEditorActionListener { textView, i, keyEvent ->
            if ((keyEvent != null && (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)){
                textView.hideKeyboard()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        
        //setup color picker
        colorPicker.setColors(resources.getIntArray(R.array.lessonColor))
        colorPicker.setOnColorSelectedListener {
            color = it
            setToolbarColor(color)
        }
        
        //fill textViews
        subjectAuto.setText(lesson.subject)
        room.setText(lesson.room)
        teacher.setText(lesson.teacher)
        //set color
        color = lesson.color
        setToolbarColor(color)
        
        fabConfirm.setOnClickListener {
            val newLesson = Timetable.Lesson(
                    subject = subjectAuto.text.toString(),
                    room = room.text.toString(),
                    teacher = teacher.text.toString().toUpperCase(),
                    color = color
            )
            updateLesson(this, timetable.id, day, l, newLesson)
            finish()
            //NavUtils.navigateUpFromSameTask(this)
        }
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId){
        R.id.delete ->{
            //build dialog
            val builder = AlertDialog.Builder(this)
            builder.setMessage(R.string.dialog_deleteLesson_message)
                    .setPositiveButton(R.string.ok){ _, _ ->
                        //delete lesson
                        updateLesson(this, timetable.id, day, l, Timetable.Lesson())
                        NavUtils.navigateUpFromSameTask(this)
                    }
                    .setNegativeButton(R.string.cancel){ _, _ ->
                        //do nothing
                    }
                    .show()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
    
    override fun onCreateOptionsMenu(menu: Menu):Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.top_actions_add_lesson, menu)
        return true
    }
    
    private fun setToolbarColor(newColor: Int){
        if(newColor != Timetable.Lesson.DEFAULT_COLOR){ //wenn nicht weiß/ keine farbe gewählt
            supportActionBar?.setBackgroundDrawable(ColorDrawable(newColor))
            window.statusBarColor = differentShade(newColor, -0.15f)
        }
    }
    
    companion object {
        const val EXTRA_DAY = "dayExtra"
        const val EXTRA_LESSON = "lessonExtra"
    }
}
