package com.example.granne

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.granne.Constants.FB_REF


class ChatListActivity : AppCompatActivity() {

    private var nicknameList = mutableListOf<String>()
    private var userUidList = mutableListOf<String>()
    private lateinit var chatListRV: RecyclerView
    lateinit var cancelBtn : ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        cancelBtn = findViewById(R.id.cancelBtn)
        chatListRV = findViewById(R.id.chatListRV)

        getAllMatchedUsers()

        cancelBtn.setOnClickListener {
            finish()
        }

    }

    private fun getAllMatchedUsers() {
        FB_REF.collection("matchedUsers").get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(
                        this, R.string.nochat,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    for (document in result) {
                        FB_REF.collection("matchedUsers").document(document.id)
                            .get()
                            .addOnSuccessListener { name ->
                                val nickname = name.data!!.getValue("nickname").toString()

                                addToList(nickname, document.id)
                            }
                    }
                }
            }
    }

    private fun addToList(nickname: String, userUid: String) {

        chatListRV.layoutManager = LinearLayoutManager(this)
        chatListRV.adapter = ChatRecyclerAdapter(nicknameList, userUidList)
        nicknameList.add(nickname)
        userUidList.add(userUid)
    }

}