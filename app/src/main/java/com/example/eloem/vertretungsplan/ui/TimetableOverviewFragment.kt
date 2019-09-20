package com.example.eloem.vertretungsplan.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eloem.vertretungsplan.R
import com.example.eloem.vertretungsplan.helperClasses.AnimatedIconFab
import com.example.eloem.vertretungsplan.helperClasses.Timetable
import com.example.eloem.vertretungsplan.helperClasses.Timetable19_20
import com.example.eloem.vertretungsplan.recyclerView.ContextAdapter
import com.example.eloem.vertretungsplan.util.editDialog
import com.example.eloem.vertretungsplan.util.generalPreferences
import com.example.eloem.vertretungsplan.util.newTimetableId
import kotlinx.android.synthetic.main.fragment_timetable_overview.*

class TimetableOverviewFragment : ChildFragment() {
    
    private lateinit var timetableAdapter: TimetableAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timetable_overview, container, false)
    }
    
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        
        configureSupportActionBar {
            title = resources.getString(R.string.title_timetable_overview)
            setDisplayHomeAsUpEnabled(true)
        }
        
        withHost {
            showFab {
                requireContext().editDialog(
                        hint = R.string.dialog_newTimetable_nameHint,
                        startString = "",
                        positiveText = R.string.dialog_newTimetable_positive,
                        invalid = { cSequence: CharSequence? -> cSequence.isNullOrBlank() },
                        errorMessage = R.string.dialog_newTimetable_errorMsg,
                        positiveAction = { name: String, _: DialogInterface, _: Int ->
                            val timetable = Timetable19_20.newDefaultInstance(newTimetableId(requireContext()))
                            timetable.name = name
                            globalViewModel.insertTimetable(timetable)
                            this@TimetableOverviewFragment.findNavController()
                                    .navigate(TimetableOverviewFragmentDirections
                                            .actionTimetableOverviewFragmentToTimetableFragment(timetable.id)
                                    )
                        },
                        negativeText = R.string.dialog_negative
                )
            }
            mainFab.animateToIcon(AnimatedIconFab.Icon.ADD)
        }
        
        timetableAdapter = TimetableAdapter(emptyList(), globalViewModel)
        
        timetableList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = timetableAdapter
        }
        
        globalViewModel.timetables.observe(viewLifecycleOwner) {
            timetableAdapter.apply {
                timetables = it
                notifyDataSetChanged()
            }
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_actions_timetable_overview, menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.settings -> {
            findNavController().navigate(TimetableFragmentDirections.actionGlobalSettingsFragment())
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
    
    class TimetableAdapter(
            var timetables: List<Timetable>,
            private val globalViewModel: GlobalViewModel
    ) : ContextAdapter<TimetableAdapter.TimetableViewHolder>() {
    
        class TimetableViewHolder(layout: View) : RecyclerView.ViewHolder(layout) {
            val root: View = layout.findViewById(R.id.root)
            val nameTV: TextView = layout.findViewById(R.id.timteableNameTV)
            val optionsButton: ImageButton = layout.findViewById(R.id.optionsButton)
            val favorite: View = layout.findViewById(R.id.favorite)
        }
    
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimetableViewHolder {
            return TimetableViewHolder(inflate(R.layout.item_timetable_overview, parent))
        }
    
        override fun getItemCount(): Int = timetables.size
    
        override fun onBindViewHolder(holder: TimetableViewHolder, position: Int) {
            with(holder) {
                val timetable = timetables[position]
                nameTV.text = timetable.name.ifBlank { ctx.getString(R.string.timetableNamePlaceHolder) }
                optionsButton.setOnClickListener { button ->
                    val clickedTimetable = timetables[holder.adapterPosition]
                    
                    PopupMenu(ctx, button).apply {
                        menuInflater.inflate(R.menu.options_timetable_overview, menu)
                        setOnMenuItemClickListener {
                            when (it.itemId) {
                                R.id.delete -> {
                                    globalViewModel.deleteTimetable(clickedTimetable)
                                    true
                                }
                                R.id.edit -> {
                                    button.findNavController().navigate(TimetableOverviewFragmentDirections.
                                            actionTimetableOverviewFragmentToTimetableFragment(clickedTimetable.id)
                                    )
                                    true
                                }
                                R.id.rename -> {
                                    ctx.editDialog(
                                            hint = R.string.dialog_newTimetable_nameHint,
                                            startString = clickedTimetable.name,
                                            positiveText = R.string.dialog_rename_positive,
                                            invalid = { cSequence: CharSequence? -> cSequence.isNullOrBlank() },
                                            errorMessage = R.string.dialog_newTimetable_errorMsg,
                                            positiveAction = { name: String, _: DialogInterface, _: Int ->
                                                globalViewModel.updateTimetableName(clickedTimetable, name)
                                            },
                                            negativeText = R.string.dialog_negative
                                    )
                                    true
                                }
                                R.id.favorite -> {
                                    generalPreferences {
                                        favoriteTimetableId = clickedTimetable.id
                                    }
                                    notifyDataSetChanged()
                                    true
                                }
                                else -> false
                            }
                        }
                    }.show()
                }
                root.setOnClickListener {
                    val clickedTimetable = timetables[holder.adapterPosition]
                    it.findNavController().navigate(TimetableOverviewFragmentDirections.
                            actionTimetableOverviewFragmentToTimetableFragment(clickedTimetable.id)
                    )
                }
                generalPreferences {
                    favorite.visibility = if (favoriteTimetableId == timetable.id) View.VISIBLE else View.INVISIBLE
                }
            }
        }
    }
}