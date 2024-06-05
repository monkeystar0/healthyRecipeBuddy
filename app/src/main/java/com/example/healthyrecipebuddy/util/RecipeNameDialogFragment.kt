package com.example.healthyrecipebuddy.util

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.healthyrecipebuddy.databinding.RecipeNameDialogBinding

class RecipeNameDialogFragment(private val onOkClicked: (String) -> Unit) : DialogFragment() {
    private lateinit var binding: RecipeNameDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = RecipeNameDialogBinding.inflate(layoutInflater)

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setPositiveButton("OK") { _, _ ->
                val recipeName = binding.recipeNameEditText.text.toString()
                onOkClicked(recipeName) // Call the lambda function with the recipe name
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}