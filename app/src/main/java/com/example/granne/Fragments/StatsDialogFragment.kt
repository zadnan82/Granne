package com.example.granne.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.granne.Extras.Constants
import com.example.granne.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class StatsDialogFragment : DialogFragment() {

    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val rootView: View = inflater.inflate(R.layout.stats_dialog_fragment, container, false)
        val nicknameTV = rootView.findViewById<TextView>(R.id.nicknameTV)
        val emailTV = rootView.findViewById<TextView>(R.id.emailTV)
        val aboutMeTV = rootView.findViewById<TextView>(R.id.aboutMeTV)
        val matchedUsersTV = rootView.findViewById<TextView>(R.id.matchedUsersTV)
        val cancelBtn = rootView.findViewById<ImageButton>(R.id.cancelBtn)

        cancelBtn.setOnClickListener {
            dismiss()
        }
                nicknameTV.text = Constants.NICKNAME
                emailTV.text = Constants.email
                aboutMeTV.text = Constants.ABOUTME

                Constants.FB_REF.collection("matchedUsers").get()
                    .addOnSuccessListener { usersMatched ->
                        val matchedList = mutableListOf<String>()

                        for (user in usersMatched) {
                            val matchedUserNickname = user.getString("nickname")

                            if (matchedUserNickname != null) {
                                matchedList.add(matchedUserNickname)
                            }
                        }
                        if (matchedList.isNotEmpty()) {
                            val matchtext = getString(R.string.active_match)
                            matchedUsersTV.text = " $matchtext $matchedList"
                        } else {
                            val nonmatchtext = getString(R.string.no_active_match)
                            matchedUsersTV.text = "$nonmatchtext"
                        }

                    }


        return rootView
    }
}