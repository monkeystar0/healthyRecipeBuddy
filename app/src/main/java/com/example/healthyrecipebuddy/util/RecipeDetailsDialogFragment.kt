package com.example.healthyrecipebuddy.util

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.DialogFragment
import com.example.healthyrecipebuddy.databinding.DialogRecipeDetailsBinding

class RecipeDetailsDialogFragment : DialogFragment() {
    private lateinit var binding: DialogRecipeDetailsBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogRecipeDetailsBinding.inflate(layoutInflater)
        val description = arguments?.getString(ARG_DESCRIPTION) ?: ""
        binding.recipeDescriptionTextView.text = description

        // Set up click listener for close button
        binding.closeButton.setOnClickListener {
            dismiss()
        }

        // Set up onTouchListener to dismiss dialog when touched outside
        val dialogWindow = dialog?.window
        dialogWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Make dialog background transparent
        dialogWindow?.setDimAmount(0.5f) // Set dim amount for outside area
        val onTouchListener = View.OnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val dialogBounds = Rect()
                dialog?.window?.decorView?.getHitRect(dialogBounds)
                if (!dialogBounds.contains(event.x.toInt(), event.y.toInt())) {
                    dismiss()
                }
            }
            false
        }
        dialogWindow?.decorView?.setOnTouchListener(onTouchListener)

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    companion object {
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_NAME = "name"

        fun newInstance(name: String, description: String): RecipeDetailsDialogFragment {
            val fragment = RecipeDetailsDialogFragment()
            val args = Bundle()
            args.putString(ARG_DESCRIPTION, description)
            args.putString(ARG_NAME, name)
            fragment.arguments = args
            return fragment
        }
    }
}