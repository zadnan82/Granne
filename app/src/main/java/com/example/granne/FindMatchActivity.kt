package com.example.granne

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.granne.Constants.FB_REF
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FindMatchActivity : AppCompatActivity() {

    val db = Firebase.firestore
    lateinit var interestButton: Button
    lateinit var searchMatchButton: Button
    lateinit var recyclerView: RecyclerView
    var persons = mutableListOf<PersonFindMatch>()
    var userInterests = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_match)

        interestButton = findViewById(R.id.interestButton)
        searchMatchButton = findViewById(R.id.searchMatchButton)
        recyclerView = findViewById(R.id.rwFindMatch)

        interestButton.setOnClickListener {
            InterestDialogFragment().show(supportFragmentManager, "interestdialog")
            recyclerView.isVisible = false
        }

        searchMatchButton.setOnClickListener {
            searchMatchingUsers()
        }

    }

    fun searchMatchingUsers() {

        FB_REF.collection("interests")
            .document("interestlist").get()
            .addOnSuccessListener { document ->
                if (document.data.isNullOrEmpty()) {
                    Toast.makeText(this, R.string.add_interests, Toast.LENGTH_SHORT).show()

                } else {
                    persons.clear()
                    recyclerView.isVisible = true
                    userInterests.clear()
                    for (item in document.data!!.values) {
                        userInterests.add(item.toString())
                    }
                    getUsersList()
                }
            }
    }

    private fun getUsersList(){

        db.collection("userData").get()
            .addOnSuccessListener { documents ->
                for (userID in documents) {
                    if (userID.id != Constants.UID)
                        checkUserLocation(userID.id)
                }
            }
    }

    private fun checkUserLocation(matchingUserID: String) {

        db.collection("userData").document(matchingUserID)
            .get()
            .addOnSuccessListener { documents ->
                val matchingUserLocation = documents.data!!.getValue("location")
                if (Constants.LOCATION == matchingUserLocation.toString())
                    checkUserInterests(matchingUserID)
            }
    }

    private fun checkUserInterests(matchingUserID: String) {

        db.collection("userData").document(matchingUserID)
            .collection("interests").document("interestlist")
            .get()
            .addOnSuccessListener { documents ->

                if (!documents.data.isNullOrEmpty()) {
                    val matchingUserInterests = documents.data!!.values
                    val sameInterestsList = mutableListOf<String>()
                    sameInterestsList.clear()

                    userInterests.forEachIndexed { index, value ->
                        if (matchingUserInterests.contains(value)) {
                            sameInterestsList.add(value)
                        }
                    }

                    if (sameInterestsList.isNotEmpty()) {
                        showMatchedUsersInfo(
                            matchingUserID,
                            matchingUserInterests
                        )
                    }
                }
            }
    }

    private fun showMatchedUsersInfo(
        matchingUserID: String,
        matchingUserInterests: MutableCollection<Any>
    ) {
        db.collection("userData").document(matchingUserID).get()
            .addOnSuccessListener { document ->

                val matchedUsersNickname = document.data!!.getValue("nickname").toString()
                val matchedUsersAboutMe = document.data!!.getValue("aboutme").toString()

                addToList(
                    matchedUsersNickname,
                    matchedUsersAboutMe,
                    matchingUserInterests,
                    matchingUserID
                )

            }
    }

    private fun addToList(
        matchedUsersNickname: String,
        matchedUsersAboutMe: String,
        matchingUserInterests: MutableCollection<Any>,
        matchingUserID: String,
    ) {

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = PersonFindMatchRecycleViewAdapter(this, persons)

        persons.add(
            PersonFindMatch(
                matchedUsersNickname,
                matchedUsersAboutMe,
                matchingUserInterests,
                matchingUserID
            )
        )

    }


}