package com.example.todoapp

import com.example.todoapp.R
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView


class ToDoAdapter(db: DataBaseHandler, activity: MainActivity) :
    RecyclerView.Adapter<ToDoAdapter.ViewHolder>() {
     var todoList: ArrayList<ToDoModel>? = null
    private val db: DataBaseHandler
    private val activity: MainActivity

    init {
        this.db = db
        this.activity = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        db.openDatabase()
        val item = todoList!![position]
        holder.task.text = item.task
        holder.task.isChecked = toBoolean(item.status!!)
        holder.task.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                db.updateStatus(item.id!!, 1)
            } else {
                db.updateStatus(item.id!!, 0)
            }
        }
    }


    private fun toBoolean(n: Int): Boolean {
        return n != 0
    }

    override fun getItemCount(): Int {
        return todoList!!.size
    }

    val context: Context
        get() = activity

    fun setTasks(todoList: ArrayList<ToDoModel>?) {
        this.todoList = todoList
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        val item = todoList!![position]
        item.id?.let { db.deleteTask(it) }
        todoList!!.removeAt(position)
        notifyItemRemoved(position)
    }

    fun editItem(position: Int) {
        val item = todoList!![position]
        val bundle = Bundle()
        bundle.putInt("id", item.id!!)
        bundle.putString("task", item.task)
        val fragment = AddNewTask()
        fragment.arguments = bundle
        fragment.show(activity.supportFragmentManager, AddNewTask.TAG)
    }

    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var task: CheckBox

        init {
            task = view.findViewById(R.id.todoCheckBox)
        }
    }
}