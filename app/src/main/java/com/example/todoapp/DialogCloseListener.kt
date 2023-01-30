package com.example.todoapp

import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

interface DialogCloseListener {
    fun handleDialogClose(dialog:DialogInterface)
}