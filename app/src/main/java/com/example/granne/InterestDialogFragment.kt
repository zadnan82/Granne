package com.example.granne

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.granne.Constants.FB_REF
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class InterestDialogFragment : DialogFragment() {

    private lateinit var aboutMeET: EditText
    private lateinit var wildlifeBox: CheckBox
    private lateinit var travelBox: CheckBox
    private lateinit var foodBox: CheckBox
    private lateinit var socialisingBox: CheckBox
    private lateinit var booksBox: CheckBox
    private lateinit var gamesBox: CheckBox
    private lateinit var netflixBox: CheckBox
    private lateinit var cancelBtn : ImageButton
    lateinit var saveChangesBtn: Button

    lateinit var auth: FirebaseAuth

    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val rootView: View = inflater.inflate(R.layout.fragment_interest_dialog, container, false)
        aboutMeET = rootView.findViewById(R.id.aboutmeET)
        wildlifeBox = rootView.findViewById(R.id.wildlifeBox)
        travelBox = rootView.findViewById(R.id.travelBox)
        socialisingBox = rootView.findViewById(R.id.socialisingBox)
        booksBox = rootView.findViewById(R.id.booksBox)
        gamesBox = rootView.findViewById(R.id.gamesBox)
        foodBox = rootView.findViewById(R.id.foodBox)
        netflixBox = rootView.findViewById(R.id.netflixBox)
        saveChangesBtn = rootView.findViewById(R.id.saveChangesBtn)
        cancelBtn = rootView.findViewById(R.id.cancelBtn)

        cancelBtn.setOnClickListener {
            dismiss()
        }

        saveChangesBtn.setOnClickListener {
            val userInterests = hashMapOf<String, String>()
            var count = 0

            if (wildlifeBox.isChecked) {
                count++
                userInterests["interest$count"] = "Wildlife"
            }
            if (travelBox.isChecked) {
                count++
                userInterests["interest$count"] = "Travel"
            }
            if (foodBox.isChecked) {
                count++
                userInterests["interest$count"] = "Food"
            }
            if (socialisingBox.isChecked) {
                count++
                userInterests["interest$count"] = "Socialising"
            }
            if (booksBox.isChecked) {
                count++
                userInterests["interest$count"] = "Books"
            }
            if (gamesBox.isChecked) {
                count++
                userInterests["interest$count"] = "Games"
            }
            if (netflixBox.isChecked) {
                count++
                userInterests["interest$count"] = "Netflix"
            }

            when {
                count > 6 -> Toast.makeText(activity, "Max 6 interests allowed!", Toast.LENGTH_SHORT).show()

                count <= 0 -> Toast.makeText(activity, "Please select at least 1 interest!", Toast.LENGTH_SHORT).show()

                else -> {
                   FB_REF.collection("interests").document("interestlist")
                        .set(userInterests)
                        .addOnSuccessListener {
                            val aboutme = aboutMeET.text.toString()

                            if (aboutme.isNotEmpty()) {
                                FB_REF.update("aboutme", aboutme)
                                    .addOnSuccessListener {
                                        dismiss()
                                    }
                            }
                            Toast.makeText(activity, "Updated interest list", Toast.LENGTH_SHORT).show()
                            dismiss()
                        }
                }
            }
        }

        return rootView
    }

}


