package com.example.healthyrecipebuddy.util

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class CustomDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Success")
            .setMessage("Food record inserted successfully.")
            .setPositiveButton("OK",null)
            .create()
    }
}