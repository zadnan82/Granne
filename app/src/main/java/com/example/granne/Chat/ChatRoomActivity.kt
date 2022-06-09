package com.example.granne.Chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.granne.Extras.Constants.FB_REF
import com.example.granne.Extras.Constants.NICKNAME
import com.example.granne.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChatRoomActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var myNickname: String
    private lateinit var chatKey: String
    private lateinit var secondUserNickname: String
    private lateinit var secondUserUid: String
    private lateinit var newMessageET: EditText
    private lateinit var messageTV: TextView
    private lateinit var sendBtn: Button
    private lateinit var chatUserNickNameTV: TextView
    lateinit var messageList: ArrayList<String>
    val db = Firebase.firestore
    private var TAG = "ChatRoomActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        auth = Firebase.auth

        messageTV = findViewById(R.id.messageTV)
        newMessageET = findViewById(R.id.newMessageET)
        sendBtn = findViewById(R.id.sendBtn)
        chatUserNickNameTV = findViewById(R.id.chatuserNickNameTV)

        secondUserNickname = intent.getStringExtra("secondUserNickname").toString()
        secondUserUid = intent.getStringExtra("secondUserUid").toString()

        getChatID()

    }

    private fun getChatID() {

        FB_REF.collection("matchedUsers").document(secondUserUid).get()
            .addOnSuccessListener { documents ->
                chatKey = documents.data!!.getValue("chatId").toString()
                createChatChannel(chatKey)
            }
    }

    private fun createChatChannel(chatKey: String) {
        val chatDocRef = db.collection("chatRooms").document(chatKey)
        messageList = arrayListOf()

        val chatInfo = hashMapOf(
            "user1" to secondUserNickname,
            "user2" to NICKNAME,
            "messagelist" to messageList
        )

        chatDocRef.get()
            .addOnSuccessListener { task ->
                if (!task.exists()) {
                    chatDocRef.set(chatInfo)
                        .addOnSuccessListener {
                            updateChatUi()
                        }
                }
                if (task.exists()) {
                    updateChatUi()
                }
            }
    }

    private fun updateChatUi() {
        val chatDocRef = db.collection("chatRooms").document(chatKey)
        // Sets title for the chatroom
        chatUserNickNameTV.text = secondUserNickname

        chatDocRef.get()
            .addOnSuccessListener { list ->
                val oldList = list.data!!.getValue("messagelist").toString()
                messageList.add(oldList)
                // add $messageList to view
                sendBtn.setOnClickListener {
                    val text = "$myNickname: ${newMessageET.text}"
                    messageList.add(text)
                    newMessageET.text.clear()
                    // add $text to view
                    chatDocRef.update("messagelist", messageList)
                }
            }

        chatDocRef
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener // Stop listening to this snapshot
                }

                if (snapshot != null && snapshot.exists()) {
                    val currentList = snapshot.data!!.getValue("messagelist")
                    if (currentList.toString().isNotEmpty()) {
                        messageTV.text = currentList.toString()
                            .replace("]", "")
                            .replace("[", "")
                            .replace(",", "")
                    }
                } else {
                    Log.d(TAG, "Current data: null")
                }

            }
    }
}



