package com.example.eloem.vertretungsplan

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.util.*
import kotlinx.android.synthetic.main.fragment_plan.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

abstract class Plan : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_plan, container, false)
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        
        progressBar.visibility = ProgressBar.VISIBLE
        //request()
    }
    
    abstract fun fillContent(verPlan: Vertretungsplan)
    
    fun setUpAdapter(plan: Vertretungsplan.Plan){
        val viewManager = LinearLayoutManager(context)
        val viewAdapter = MyAdapter(plan)
    
        list.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }
    
    class MyAdapter(val values: Vertretungsplan.Plan): RecyclerView.Adapter<MyAdapter.ViewHolder>() {
        
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
}

class GeneralPlan: Plan(){
    override fun fillContent(verPlan: Vertretungsplan) {
        if(verPlan.generalPlan.error == Vertretungsplan.ERROR_NO) {
            errorTV.visibility = TextView.GONE
        }else{
            errorTV.text = resources.getString(R.string.error_message_no_connection)
            errorTV.visibility = TextView.VISIBLE
        }

        progressBar.visibility = ProgressBar.GONE
        setUpAdapter(verPlan.generalPlan)
    }
}

class MyPlan: Plan(){
    override fun fillContent(verPlan: Vertretungsplan) {
        if(verPlan.customPlan.error == Vertretungsplan.ERROR_NO) {
            errorTV.visibility = TextView.GONE
        }else{
            when(verPlan.customPlan.error){
                Vertretungsplan.ERROR_NO_PLAN -> errorTV.text = resources.getString(R.string.error_message_no_plan)
                Vertretungsplan.ERROR_WRONG_DAY -> errorTV.text = resources.getString(R.string.error_message_wrong_day)
                Vertretungsplan.ERROR_CONNECTION -> errorTV.text = resources.getString(R.string.error_message_no_connection)
                else -> errorTV.text = resources.getString(R.string.error_message_universal)
            }
            errorTV.visibility = TextView.VISIBLE
        }
        
        progressBar.visibility = ProgressBar.GONE
        setUpAdapter(verPlan.customPlan)
    }
}