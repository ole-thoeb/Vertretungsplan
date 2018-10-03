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

class Plan : Fragment() {
    
    private var isMyPlan: Boolean = false
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_plan, container, false)
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    
        isMyPlan = arguments!!.getBoolean(IS_MY_PLAN)
        
        progressBar.visibility = ProgressBar.VISIBLE
        request()
    
        swiperefresh.setOnRefreshListener {
            swiperefresh.isRefreshing = true
            request(true)
        }
    }
    
    fun fillContent(verPlan: Vertretungsplan, needToUpdate: Boolean){
        val mainActivity = (activity as MainActivity)
        if(needToUpdate) mainActivity.updateOtherChild(isMyPlan) //update anderen tab
        mainActivity.setUpToolbarText()
        
        if(verPlan.getError(isMyPlan) == Vertretungsplan.ERROR_NO){
            errorTV.visibility = TextView.GONE
            setUpAdapter(verPlan)
        }else{
            when(verPlan.getError(isMyPlan)){
                Vertretungsplan.ERROR_NO_PLAN -> errorTV.text = resources.getString(R.string.error_message_no_plan)
                Vertretungsplan.ERROR_WRONG_DAY -> errorTV.text = resources.getString(R.string.error_message_wrong_day)
                Vertretungsplan.ERROR_CONNECTION -> errorTV.text = resources.getString(R.string.error_message_no_connection)
                else -> errorTV.text = resources.getString(R.string.error_message_universal)
            }
            
            setUpAdapter(verPlan)
            errorTV.visibility = TextView.VISIBLE
        }
    
        progressBar.visibility = ProgressBar.GONE
        swiperefresh.isRefreshing = false
    }
    
    private fun setUpAdapter(plan: Vertretungsplan){
        val viewManager = LinearLayoutManager(context)
        val viewAdapter = if(isMyPlan){
            MyAdapter(plan.getCustPlan())
        }else{
            MyAdapter(plan.getPlan())
        }
    
        list.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }
    
    private fun request(needToUpdate: Boolean = false){
        if (planIsUpToDate(context) && !needToUpdate){
            val verPlan = readVertretungsplan(context)
            fillContent(verPlan, needToUpdate)
        }else{
            val queue = Volley.newRequestQueue(context)
    
            val url = getUrl(readGrade(context))
    
            val stringRequest = StringRequest(Request.Method.GET, url,
                    Response.Listener<String> { response ->
                        doAsync {
                            val verPlan = parseHtml(context, response)
                            uiThread {
                                it.fillContent(verPlan, needToUpdate)
                            }
                        }
                    },
                    Response.ErrorListener { _ ->
                        val verPlan = Vertretungsplan()
                        verPlan.setError(Vertretungsplan.ERROR_CONNECTION)
                        writeVertretungsplan(verPlan, context)
                        fillContent(verPlan, needToUpdate)
                    })
    
            queue.add(stringRequest)
        }
    }
    
    class MyAdapter(values: ArrayList<Vertretungsplan.Row>): RecyclerView.Adapter<MyAdapter.ViewHolder>() {
        
        val values: ArrayList<Vertretungsplan.Row>
        
        init {
            this.values = values
        }
    
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
            val row = values[position]
            holder.lesson.text = row.lesson.toString()
            holder.teacher.text = row.teacher
            holder.verTeacher.text = row.verTeacher
            holder.room.text = row.room
            holder.verRoom.text = row.verRoom
            holder.verText.text = row.verText
        }
    
        override fun getItemCount(): Int {
            return values.size
        }
    }
    
    companion object {
        private const val IS_MY_PLAN = "my_plan"
        
        fun newInstance(isMyPlan: Boolean): Fragment {
            val fragment = Plan()
            val args = Bundle()
            args.putBoolean(IS_MY_PLAN, isMyPlan)
            fragment.arguments = args
            return fragment
        }
    }
}
