package com.example.granne.Extras

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


object Constants {

    lateinit var FB_REF : DocumentReference
    val db = Firebase.firestore
    var uid = ""
    val user = Firebase.auth.currentUser
    val email = user?.email.toString()
    var NICKNAME : String = "nickname"
    var UID : String = ""
    var LOCATION : String = "location"
    var ABOUTME : String = ""
    private var TAG = "Constants"


    init {
        update()
    }

    fun update() {

        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            uid = currentUser.uid
            UID = uid
        }

        FB_REF = db.collection("userData").document(UID)

        db.collection("userData").document(UID).get() .addOnSuccessListener { document ->
            if (document != null) {
                val userdocument = document.toObject(UserClass::class.java)

                if (userdocument != null) {
                    NICKNAME = userdocument.nickname
                    LOCATION = userdocument.location
                    ABOUTME = userdocument.aboutme
                }
            } else {
                Log.d(TAG, "No such document")
            }
        }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }
}

data class UserClass(  var email: String = "", var nickname: String = "" ,
                       var uid: String = "", var location: String = "",
                       var aboutme: String = "")