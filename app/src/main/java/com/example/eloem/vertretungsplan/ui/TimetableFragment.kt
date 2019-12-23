package com.example.eloem.vertretungsplan.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eloem.vertretungsplan.R
import com.example.eloem.vertretungsplan.helperClasses.Timetable
import com.example.eloem.vertretungsplan.recyclerView.ContextAdapter
import com.example.eloem.vertretungsplan.recyclerView.FirstSmallerSpanLookup
import com.example.eloem.vertretungsplan.util.*
import kotlinx.android.synthetic.main.fragment_timetable.*
import org.jetbrains.anko.attr

class TimetableFragment : ChildFragment() {
    
    private val args: TimetableFragmentArgs by navArgs()
    
    private var timetable: Timetable? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_timetable, container, false)
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(TAG, "onActivityCreated")
        super.onActivityCreated(savedInstanceState)
        
        configureSupportActionBar {
            setDisplayHomeAsUpEnabled(true)
            title = resources.getString(R.string.title_timetable)
        }
        
        withHost {
            hideFab()
        }
        
        val spanLookup = FirstSmallerSpanLookup(6, 3)
        val tableLayoutManager = GridLayoutManager(requireContext(), spanLookup.neededSpans)
        tableLayoutManager.spanSizeLookup = spanLookup
    
        val timetableAdapter = TableAdapter(Timetable.newDefaultInstance(-1), false)
        table.apply {
            table.adapter = timetableAdapter
            table.layoutManager = tableLayoutManager
        }
        
        val timetableId = if (args.timetableId == -1L) generalPreferences { favoriteTimetableId } else args.timetableId
        globalViewModel.getTimetableLive(timetableId).observeNotNull(viewLifecycleOwner) { tTable ->
            Log.d(TAG, "updated timetable $tTable")
            timetable = tTable
            
            timetableAdapter.timetable = tTable
            timetableAdapter.isEditable = args.isEditable
            spanLookup.columns = tTable.days + 1
            tableLayoutManager.spanCount = spanLookup.neededSpans
            timetableAdapter.notifyDataSetChanged()
    
            configureSupportActionBar {
                title = tTable.name.ifBlank { resources.getString(R.string.title_timetable) }
            }
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_actions_timetable, menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.deleteTimetable -> {
            timetable?.let {
                globalViewModel.deleteTimetable(it)
            }
            findNavController().navigateUp()
            true
        }
        R.id.settings -> {
            findNavController().navigate(TimetableFragmentDirections.actionGlobalSettingsFragment())
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
    
    private class TableAdapter(var timetable: Timetable, var isEditable: Boolean) : ContextAdapter<TableAdapter.TextViewViewHolder>() {
        
        class TextViewViewHolder(layout: View) : RecyclerView.ViewHolder(layout) {
            val textView: TextView = layout.findViewById(R.id.textView)
            val root: View = layout.rootView
        }
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewViewHolder = when (viewType) {
            VIEW_TYPE_DAY -> TextViewViewHolder(inflate(R.layout.item_timetable_tv_day, parent))
            VIEW_TYPE_LESSON -> TextViewViewHolder(inflate(R.layout.item_timetable_tv_lesson, parent))
            VIEW_TYPE_SUBJECT -> TextViewViewHolder(inflate(R.layout.item_timetable_tv_subject, parent))
            else -> throw IllegalArgumentException("unknown view type: $viewType")
        }
    
        override fun getItemCount(): Int {
            return (timetable.days + 1) * (timetable.lessons + 1)
        }
    
        override fun getItemViewType(position: Int): Int = when {
            position == 0 -> VIEW_TYPE_DAY
            position in 1..timetable.days -> VIEW_TYPE_DAY
            (timetable.days + 1) divides position -> VIEW_TYPE_LESSON
            else -> VIEW_TYPE_SUBJECT
        }
    
        override fun onBindViewHolder(holder: TextViewViewHolder, position: Int) {
            val defaultBackgroundColor = Color.TRANSPARENT
            val defaultTextColor = ctx.attr(R.attr.colorOnBackground).data
            when (holder.itemViewType) {
                VIEW_TYPE_DAY -> holder.textView.apply {
                    text = if (position == 0) {
                        ""
                    } else {
                        WeekDay.values()[position - 1].shortName
                    }
                    setBackgroundColor(defaultBackgroundColor)
                    setTextColor(defaultTextColor)
                }
                VIEW_TYPE_LESSON -> holder.textView.apply {
                    text = (position / (timetable.days + 1)).toString()
                    setBackgroundColor(defaultBackgroundColor)
                    setTextColor(defaultTextColor)
                }
                VIEW_TYPE_SUBJECT -> {
                    val day = dayFromAdapterPos(position)
                    val lessonNr =  lessonFromAdapterPos(position)
                    val lesson = timetable[day][lessonNr]
    
                    val textColor = if (lesson.color == Timetable.Lesson.DEFAULT_COLOR) {
                        ctx.attr(R.attr.colorOnBackground).data
                    } else {
                        textColorOn(lesson.color)
                    }
                    holder.textView.apply {
                        text = lesson.subject
                        setTextColor(textColor)
                    }
                    holder.root.apply {
                        setBackgroundColor(lesson.color)
                        
                        setOnClickListener {
                            //real timetable was not yet set
                            if (timetable.id == -1L) return@setOnClickListener
                            if (!isEditable) return@setOnClickListener
                            val currentPos = holder.adapterPosition
                            val currentDay = dayFromAdapterPos(currentPos)
                            val currentLessonNr =  lessonFromAdapterPos(currentPos)
                            it.findNavController().navigate(TimetableFragmentDirections
                                    .actionTimetableFragmentToEditLessonFragment(currentDay, currentLessonNr, timetable.id)
                            )
                        }
                    }
                    if (generalPreferences { showLessonTimes } && lesson.subject.isNotBlank()) {
                        val time = timetable.lessonTimes[day][lessonNr]
                        holder.root.findViewById<TextView>(R.id.timeFromTV).apply {
                            visibility = View.VISIBLE
                            text = time.start.toString()
                            setTextColor(textColor)
                        }
                        holder.root.findViewById<TextView>(R.id.timeToTV).apply {
                            visibility = View.VISIBLE
                            text = time.endInclusive.toString()
                            setTextColor(textColor)
                        }
                    } else {
                        holder.root.findViewById<TextView>(R.id.timeFromTV).visibility = View.GONE
                        holder.root.findViewById<TextView>(R.id.timeToTV).visibility = View.GONE
                    }
                }
                else -> Log.e(TAG, "Unknown itemViewType: ${holder.itemViewType}")
            }
        }
        
        private fun dayFromAdapterPos(pos: Int): Int = (pos % (timetable.days + 1)) - 1
        private fun lessonFromAdapterPos(pos: Int): Int = pos / (timetable.days + 1) - 1
        
        companion object {
            const val VIEW_TYPE_DAY = 0
            const val VIEW_TYPE_LESSON = 1
            const val VIEW_TYPE_SUBJECT = 2
        }
    }
    
    companion object {
        private const val TAG = "TimetableFragment"
        const val ACTION_SHORTCUT_TIMETABLE = "com.example.eloem.vertretungsplan.SHORTCUT_TIMETABLE"
    }
}
