package com.example.todoapp

import com.example.todoapp.R
import android.content.DialogInterface
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), DialogCloseListener {
    private lateinit var db: DataBaseHandler
    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var tasksAdapter: ToDoAdapter
    private lateinit var fab: FloatingActionButton
    private lateinit var taskList: ArrayList<ToDoModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Objects.requireNonNull(supportActionBar)!!.hide()
        db = DataBaseHandler(this)
        db!!.openDatabase()
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView)
        tasksRecyclerView.layoutManager = LinearLayoutManager(this)
        tasksAdapter = ToDoAdapter(db!!, this@MainActivity)
        tasksRecyclerView.adapter = tasksAdapter
        /*val itemTouchHelper = ItemTouchHelper(RecyclerItemTouchHelper(tasksAdapter))
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView)*/

        val touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper. START or ItemTouchHelper.END,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val source = viewHolder.adapterPosition
                val dest = target.adapterPosition
                Collections.swap(taskList,source,dest)
                tasksAdapter.notifyDataSetChanged()
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (direction == ItemTouchHelper.LEFT) {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(tasksAdapter.context)
                    builder.setTitle("Delete Task")
                    builder.setMessage("Are you sure you want to delete this Task?")
                    builder.setPositiveButton("Confirm",
                        DialogInterface.OnClickListener { dialog, which -> tasksAdapter.deleteItem(position) })
                    builder.setNegativeButton(
                        android.R.string.cancel,
                        DialogInterface.OnClickListener { dialog, which ->
                            tasksAdapter.notifyItemChanged(
                                viewHolder.adapterPosition
                            )
                        })
                    val dialog: AlertDialog = builder.create()
                    dialog.setCanceledOnTouchOutside(false)
                    dialog.show()
                } else {
                    tasksAdapter.editItem(position)
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                val icon: Drawable?
                val background: ColorDrawable
                val itemView: View = viewHolder.itemView
                val backgroundCornerOffset = 20
                if (dX > 0) {
                    icon = ContextCompat.getDrawable(tasksAdapter.context, R.drawable.ic_baseline_edit)
                    background =
                        ColorDrawable(ContextCompat.getColor(tasksAdapter.context, R.color.black))
                } else {
                    icon = ContextCompat.getDrawable(tasksAdapter.context, R.drawable.ic_baseline_delete)
                    background = ColorDrawable(Color.RED)
                }
                assert(icon != null)
                val iconMargin: Int = (itemView.getHeight() - icon!!.intrinsicHeight) / 2
                val iconTop: Int = itemView.getTop() + (itemView.getHeight() - icon.intrinsicHeight) / 2
                val iconBottom = iconTop + icon.intrinsicHeight
                if (dX > 0) { // Swiping to the right
                    val iconLeft: Int = itemView.getLeft() + iconMargin
                    val iconRight: Int = itemView.getLeft() + iconMargin + icon.intrinsicWidth
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    background.setBounds(
                        itemView.getLeft(), itemView.getTop(),
                        itemView.getLeft() + dX.toInt() + backgroundCornerOffset, itemView.getBottom()
                    )
                } else if (dX < 0) { // Swiping to the left
                    val iconLeft: Int = itemView.getRight() - iconMargin - icon.intrinsicWidth
                    val iconRight: Int = itemView.getRight() - iconMargin
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    background.setBounds(
                        itemView.getRight() + dX.toInt() - backgroundCornerOffset,
                        itemView.getTop(), itemView.getRight(), itemView.getBottom()
                    )
                } else { // view is unSwiped
                    background.setBounds(0, 0, 0, 0)
                }
                background.draw(c)
                icon.draw(c)
            }

        })
        touchHelper.attachToRecyclerView(tasksRecyclerView)

        fab = findViewById(R.id.fab)
        taskList = db.allTasks as ArrayList<ToDoModel>
        taskList.reverse()
        tasksAdapter!!.setTasks(taskList)
        fab.setOnClickListener(View.OnClickListener {
            AddNewTask.newInstance().show(supportFragmentManager, AddNewTask.TAG)
        })
    }

    override fun handleDialogClose(dialog: DialogInterface) {
        taskList = db.allTasks as ArrayList<ToDoModel>
        taskList.reverse()
        tasksAdapter!!.setTasks(taskList)
        tasksAdapter!!.notifyDataSetChanged()
    }
}