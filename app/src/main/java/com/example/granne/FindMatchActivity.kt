package com.example.granne

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.granne.Constants.FB_REF
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FindMatchActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    lateinit var interestButton: Button
    lateinit var searchMatchButton: Button
    lateinit var recyclerView: RecyclerView
    var persons = mutableListOf<PersonFindMatch>()
    lateinit var matchingpplAdapter: PersonFindMatchRecycleViewAdapter
    lateinit var userLocation: Any
    var userInterests = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_match)
        auth = Firebase.auth

        val currentUser = auth.currentUser

        interestButton = findViewById(R.id.interestButton)
        searchMatchButton = findViewById(R.id.searchMatchButton)
        recyclerView = findViewById(R.id.rwFindMatch)


        matchingpplAdapter = PersonFindMatchRecycleViewAdapter(this, persons)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = matchingpplAdapter


        interestButton.setOnClickListener {
            val dialog = InterestDialogFragment()
            dialog.show(supportFragmentManager, "interestdialog")
            recyclerView.isVisible = false
        }

        searchMatchButton.setOnClickListener {

             FB_REF.get()
                .addOnSuccessListener { documents ->
                    userLocation = documents.data!!.getValue("location")

                    FB_REF.collection("interests").document("interestlist")
                        .get()

                        .addOnSuccessListener { document ->
                            if (document.data.isNullOrEmpty()) {
                                Toast.makeText(applicationContext, "Add your interests before searching!", Toast.LENGTH_SHORT).show()

                            } else {
                                persons.clear()
                                recyclerView.isVisible = true
                                userInterests.clear()
                                for (item in document.data!!.values) {
                                    userInterests.add(item.toString())
                                }

                                db.collection("userData")
                                    .get()
                                    .addOnSuccessListener { documents ->
                                        for (userID in documents) {
                                            if (userID.id != currentUser!!.uid)
                                                checkUserLocation(userID.id)
                                        }
                                    }
                            }
                        }
                }
                .addOnFailureListener { e ->
                    Log.d("!", "Error:", e)
                }
        }

    }

    private fun checkUserLocation(matchingUserID: String) {
        db.collection("userData").document(matchingUserID)
            .get()
            .addOnSuccessListener { documents ->
                val matchingUserLocation = documents.data!!.getValue("location")

                if (userLocation.toString() == matchingUserLocation.toString()) checkUserInterests(matchingUserID)
            }
    }

    private fun checkUserInterests(matchinguserId: String) {
        db.collection("userData").document(matchinguserId)
            .collection("interests").document("interestlist")
            .get()
            .addOnSuccessListener { documents ->

                if (!documents.data.isNullOrEmpty()) {
                    Log.d("!", "Other users interests: ${documents.data!!.values}")

                    val matchingUserInterests = documents.data!!.values
                    val sameInterestsList = mutableListOf<String>()
                    sameInterestsList.clear()

                    userInterests.forEachIndexed { index, value ->
                        if (matchingUserInterests.contains(value)) {
                            sameInterestsList.add(value)
                        }
                    }

                    if (sameInterestsList.isNotEmpty()) {
                        val numOfInterests = sameInterestsList.size
                        showMatchedUsersInfo(matchinguserId,
                            matchingUserInterests,
                            sameInterestsList,
                            numOfInterests)
                    }
                }
            }
    }

    private fun showMatchedUsersInfo(
        matchinguserId: String,
        matchingUserInterests : MutableCollection<Any>,
        sameInterestsList: MutableList<String>,
        numOfInterests: Int,
    ) {
        db.collection("userData").document(matchinguserId).get()
            .addOnSuccessListener { document ->

                val matchedUsersNickname = document.data!!.getValue("nickname").toString()
                val matchedUsersLocation = document.data!!.getValue("location").toString()
                val matchedUsersAboutMe = document.data!!.getValue("aboutme").toString()
                val matchedUsersUid = document.data!!.getValue("uid").toString()

                Log.d("!", "<------------------------ Matched user info ------------------------>")
                Log.d("!", "You matched with: $matchedUsersNickname")
                Log.d("!", "The persons location: $matchedUsersLocation")
                Log.d("!", "About this person: $matchedUsersAboutMe")
                Log.d("!",
                    "You have: $numOfInterests common interests, you both like $sameInterestsList")
                Log.d("!", "The other person also likes these things: $matchingUserInterests")


                persons.add(PersonFindMatch(matchedUsersNickname,
                    matchedUsersAboutMe,
                    matchingUserInterests,
                    matchedUsersUid))

            }
        matchingpplAdapter.notifyDataSetChanged()

    }

    private fun addToList(
        nickname: String,
        aboutMe: String,
        allInterests: MutableCollection<Any>,
        matchedUID: String,
    ) {

        persons.add(PersonFindMatch(nickname, aboutMe, allInterests, matchedUID))

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = PersonFindMatchRecycleViewAdapter(this, persons)


    }



}
