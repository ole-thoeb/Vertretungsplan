package com.example.eloem.vertretungsplan.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eloem.vertretungsplan.R
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.network.ResponseModel
import com.example.eloem.vertretungsplan.util.Result
import com.example.eloem.vertretungsplan.util.exhaustive
import kotlinx.android.synthetic.main.fragment_plan.*

abstract class PlanFragment : Fragment() {
    
    private lateinit var planAdapter: MyAdapter
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_plan, container, false)
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        
        progressBar.visibility = ProgressBar.VISIBLE
        
        val viewManager = LinearLayoutManager(context)
        planAdapter = MyAdapter(Vertretungsplan.Plan.EMPTY)
    
        list.apply {
            layoutManager = viewManager
            adapter = planAdapter
        }
        list.emptyView = errorTV
        
        planResponse.observe(viewLifecycleOwner) { updateContent(it) }
    }
    
    abstract val planResponse: LiveData<Result<Vertretungsplan.Plan, ResponseModel.Error>>
    
    private fun updateContent(verPlanResult: Result<Vertretungsplan.Plan, ResponseModel.Error>) {
        when(verPlanResult) {
            is Result.Success -> {
                val plan = verPlanResult.value
                errorTV.text = when (plan.status) {
                    Vertretungsplan.PlanStatus.OK -> ""
                    Vertretungsplan.PlanStatus.WRONG_DAY -> resources.getString(R.string.error_message_wrong_day)
                    Vertretungsplan.PlanStatus.NO_PLAN -> resources.getString(R.string.error_message_no_plan)
                    Vertretungsplan.PlanStatus.CALCULATION_ERROR -> resources.getString(R.string.error_message_calc)
                    Vertretungsplan.PlanStatus.NO_TIMETABLE -> resources.getString(R.string.error_message_no_timetable)
                }
                planAdapter.values = plan
                planAdapter.notifyDataSetChanged()
                progressBar.visibility = ProgressBar.GONE
            }
            is Result.Failure -> {
                errorTV.text = when (verPlanResult.error) {
                    ResponseModel.Error.NO_INTERNET -> resources.getString(R.string.error_message_no_connection)
                    ResponseModel.Error.PARSE_ERROR -> resources.getString(R.string.error_message_pars)
                }
            
                planAdapter.values = Vertretungsplan.Plan.EMPTY
                planAdapter.notifyDataSetChanged()
                progressBar.visibility = ProgressBar.GONE
            }
        }.exhaustive()
    }
    
    class MyAdapter(var values: Vertretungsplan.Plan): RecyclerView.Adapter<MyAdapter.ViewHolder>() {
        
        class ViewHolder(layout: View): RecyclerView.ViewHolder(layout){
            val lesson: TextView = layout.findViewById(R.id.lessonTV)
            val teacher: TextView = layout.findViewById(R.id.teacherTV)
            val verTeacher: TextView = layout.findViewById(R.id.verTeacherTV)
            val room: TextView = layout.findViewById(R.id.roomTV)
            val verRoom: TextView = layout.findViewById(R.id.verRoomTV)
            val verText: TextView = layout.findViewById(R.id.verTextTV)
        }
    
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val layout = LayoutInflater.from(parent.context).inflate(R.layout.plan_row, parent,false)
            return ViewHolder(layout)
        }
    
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val row = values.plan[position]
            holder.lesson.text = row.lesson.toString()
            holder.teacher.text = row.teacher
            holder.verTeacher.text = row.verTeacher
            holder.room.text = row.room
            holder.verRoom.text = row.verRoom
            holder.verText.text = row.verText
        }
    
        override fun getItemCount(): Int {
            return values.plan.size
        }
    }
    
    companion object {
        private const val TAG = "PlanFragment"
    }
}

class MyPlanFragment : PlanFragment() {
    override val planResponse: LiveData<Result<Vertretungsplan.Plan, ResponseModel.Error>>
        get() = (requireParentFragment() as PlanResponseHolder).customPlan
}


class GeneralPlanFragment : PlanFragment() {
    override val planResponse: LiveData<Result<Vertretungsplan.Plan, ResponseModel.Error>>
        get() = (requireParentFragment() as PlanResponseHolder).generalPlan
}