package com.example.granne

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChatActivity : AppCompatActivity() {

    val db = Firebase.firestore

    lateinit var sendMsgBtn: Button
    lateinit var nickNameTV: TextView
    lateinit var newMsgET: EditText
    lateinit var textDisplay: TextView

    companion object {
        const val COLLECTION_KEY = "Chat"
        const val DOCUMENT_KEY = "Message"
        const val NAME_FIELD = "Name"
        const val TEXT_FIELD = "Text"
    }

    private val firestoreChat by lazy {
        FirebaseFirestore.getInstance().collection(COLLECTION_KEY).document(DOCUMENT_KEY)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        textDisplay = findViewById(R.id.textDisplay)
        sendMsgBtn = findViewById(R.id.sendMsgBtn)
        nickNameTV = findViewById(R.id.nickNameTV)
        newMsgET = findViewById(R.id.newMsgET)
        nickNameTV.text = "User"


        realtimeUpdateListener()

        sendMsgBtn.setOnClickListener {
            sendMessage()
        }
    }

    private fun sendMessage() {
        val newMessage = mapOf(
            NAME_FIELD to nickNameTV.toString(),
            TEXT_FIELD to newMsgET.text.toString()
        )
        firestoreChat.set(newMessage).addOnSuccessListener {
            Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e -> e.message?.let { Log.e("ERROR", it) } }

    }

    private fun realtimeUpdateListener() {
        firestoreChat.addSnapshotListener { documentSnapshot, e ->
            when {
                e != null -> e.message?.let { Log.e("ERROR", it) }
                documentSnapshot != null && documentSnapshot.exists() -> {
                    with(documentSnapshot) {
                        if (data != null)
                            textDisplay.text = "${data!![NAME_FIELD]}: ${data!![TEXT_FIELD]}"
                    }
                }
            }

        }
    }
}




