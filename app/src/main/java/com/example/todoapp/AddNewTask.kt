package com.example.todoapp

import com.example.todoapp.R
import android.app.Activity
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.jetbrains.annotations.Nullable
import java.util.*


class AddNewTask: BottomSheetDialogFragment() {
    companion object {
        fun newInstance(): AddNewTask {
            return AddNewTask()
        }

        var TAG: String = "ActionBottomDialog"
        var isUpdate = false
    }

    lateinit var newTaskText: EditText
    lateinit var newTaskSaveButton: Button
    private lateinit var db: DataBaseHandler



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Dialogstyle)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View = inflater.inflate(R.layout.new_task,container,false)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newTaskText = view.findViewById(R.id.newTaskText)
        newTaskSaveButton = view.findViewById(R.id.newTaskButton)

        db = DataBaseHandler(activity)
        db.openDatabase()


        val bundle: Bundle? = arguments
        if (bundle!=null){
            isUpdate = true
            val task: String? = bundle.getString("task")
            newTaskText.setText(task)
            if (task?.length!!>0){
                newTaskSaveButton.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
            }
        }
        newTaskText.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString() == ""){
                    newTaskSaveButton.isEnabled = false
                    newTaskSaveButton.setTextColor(Color.GRAY)
                }else{
                    newTaskSaveButton.isEnabled = true
                    newTaskSaveButton.setTextColor(Color.GRAY)
                }
            }

            override fun afterTextChanged(s:Editable?){

            }
        })
        newTaskSaveButton.setOnClickListener {
            val text: String = newTaskText.text.toString()
            if (isUpdate) {
                db.updateTask(bundle!!.getInt("id"), text)
            } else {
                var task: ToDoModel = ToDoModel()
                task.status = 0
                task.task = text
                db.insertTask(task)
            }
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        var activity: FragmentActivity? = activity
        if (activity is DialogCloseListener){
            (activity as DialogCloseListener).handleDialogClose(dialog)
        }
    }
}