package com.example.eloem.vertretungsplan.ui.editlesson


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

import com.example.eloem.vertretungsplan.R
import com.example.eloem.vertretungsplan.helperClasses.AnimatedIconFab
import com.example.eloem.vertretungsplan.helperClasses.Timetable
import com.example.eloem.vertretungsplan.helperClasses.Timetable19_20
import com.example.eloem.vertretungsplan.ui.ChildFragment
import com.example.eloem.vertretungsplan.util.*
import kotlinx.android.synthetic.main.fragment_edit_lesson.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.attr

/**
 * A simple [Fragment] subclass.
 */
class EditLessonFragment : ChildFragment() {
    
    private val args: EditLessonFragmentArgs by navArgs()
    
    private val editViewModel: EditLessonViewModel by viewModels()
    
    private var timetableToEdit: Timetable? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_lesson, container, false)
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val day = WeekDay.values()[args.day]
        val lessonNr = args.lesson
        
        configureSupportActionBar {
            setDisplayHomeAsUpEnabled(true)
            title = resources.getString(R.string.title_edit_lesson, day.shortName, lessonNr + 1)
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
            editViewModel.color = it
            setToolbarColor(it)
        }
        
        subjectAuto.doOnTextChanged { text, _, _, _ ->
            editViewModel.subject = text.orEmpty()
        }
    
        teacher.doOnTextChanged { text, _, _, _ ->
            editViewModel.teacher = text.orEmpty()
        }
    
        room.doOnTextChanged { text, _, _, _ ->
            editViewModel.room = text.orEmpty()
        }
        
        viewLifecycleOwner.lifecycle.coroutineScope.launch(Dispatchers.Main) {
            val timetable = globalViewModel.getTimetable(args.timetableId).ifNull {
                Log.e(TAG, "timetable with id ${args.timetableId} does not exist. Can't edit nothing")
                return@launch
            }
            timetableToEdit = timetable
        
            //setup autocomplete text view
            val distinctLessons = timetable.distinctLessons
            val autoList = List(distinctLessons.size) { distinctLessons[it].subject}
        
            val adapter = ArrayAdapter<String>(requireContext(), R.layout.expandable_list_item, autoList)
            subjectAuto.setAdapter(adapter)
            //schließt soft keyboard, wenn vorschlag ausgewählt wird
            subjectAuto.setOnItemClickListener { _, view, _, _ ->
                view?.hideKeyboard()
            
                //vervollständigung der anderen felder
                val selected = subjectAuto.text.toString()
                val selectedLesson = distinctLessons.find { it.subject == selected}
                teacher.setText(selectedLesson?.teacher)
                editViewModel.color = selectedLesson?.color ?: Timetable.Lesson.DEFAULT_COLOR
                setToolbarColor(editViewModel.color)
            }
        
            val lesson = timetable[day][lessonNr]
            editViewModel.applyLessonOnce(lesson)
            //fill textViews
            subjectAuto.setText(editViewModel.subject)
            teacher.setText(editViewModel.teacher)
            room.setText(editViewModel.room)
            //set color
            setToolbarColor(editViewModel.color)
            colorPicker.setSelectedColor(editViewModel.color)
            
            withHost {
                showFab {
                    val newLesson = Timetable19_20.Lesson(
                            subject = editViewModel.subject,
                            teacher = editViewModel.teacher.toUpperCase(),
                            room = editViewModel.room,
                            color = editViewModel.color
                    )
                    globalViewModel.updateTimetableLesson(newLesson, day, lessonNr, timetable)
                    this@EditLessonFragment.findNavController().popBackStack()
                }
                mainFab.animateToIcon(AnimatedIconFab.Icon.CHECK)
            }
        }
    }
    
    override fun onStop() {
        super.onStop()
        val defaultColor = requireContext().attr(R.attr.toolbarColor).data
        withHost {
            window.setStatusBarAndIconColors(defaultColor)
            toolbar.setBackgroundColor(defaultColor)
        }
        view?.findFocus()?.hideKeyboard()
    }
    
    private fun setToolbarColor(newColor: Int){
        if(newColor != Timetable.Lesson.DEFAULT_COLOR){ //if no color selected
            withHost {
                window.setStatusBarAndIconColors(differentShade(newColor, -0.15f))
                toolbar.setBackgroundColor(newColor)
            }
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_actions_edit_lesson, menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.delete -> {
            timetableToEdit?.let { timetable ->
                globalViewModel.updateTimetableLesson(Timetable19_20.Lesson(), WeekDay.values()[args.day], args.lesson, timetable)
            }
            editViewModel.apply {
                subject = ""
                teacher = ""
                room = ""
                color = Timetable.Lesson.DEFAULT_COLOR
            }
            findNavController().navigateUp()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
    
    companion object {
        private const val TAG = "EditLessonFragment"
    }
}