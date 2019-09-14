package com.example.eloem.vertretungsplan.ui

import androidx.lifecycle.ViewModel
import com.example.eloem.vertretungsplan.helperClasses.EditTimetable
import com.example.eloem.vertretungsplan.helperClasses.Timetable

class EditLessonViewModel : ViewModel() {
    var subject = ""
    var teacher = ""
    var room = ""
    var color = Timetable.Lesson.DEFAULT_COLOR
    
    private var firstTime = true
    
    fun applyLessonOnce(lesson: Timetable.Lesson) {
        if (!firstTime) return
        firstTime = false
        
        subject = lesson.subject
        teacher = lesson.teacher
        room = lesson.room
        color = lesson.color
    }
}