package com.example.granne.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import com.example.granne.R

class CustomDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val rootView: View =  inflater.inflate(R.layout.fragment_custom_dialog, container, false)

        val cancelBtn = rootView.findViewById<ImageButton>(R.id.cancelBtn)

        cancelBtn.setOnClickListener {
            dismiss()
        }


        return rootView
    }
}