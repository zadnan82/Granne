package com.example.granne

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.granne.Constants.FB_REF


class ChatListActivity : AppCompatActivity() {

    private var nicknameList = mutableListOf<String>()
    private var userUidList = mutableListOf<String>()
    private lateinit var chatusersRV: RecyclerView
    lateinit var chatusersAdapter: ChatRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        chatusersRV = findViewById(R.id.chatusersRV)

        chatusersRV.layoutManager = LinearLayoutManager(this)
        chatusersAdapter = ChatRecyclerAdapter( nicknameList, userUidList)
        chatusersRV.adapter = chatusersAdapter

        getAllMatchedUsers()
    }

    private fun getAllMatchedUsers() {
        FB_REF.collection("matchedUsers").get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(applicationContext, "You have no active chats!",
                        Toast.LENGTH_SHORT).show()
                } else {
                    for (document in result) {
                        FB_REF.collection("matchedUsers").document(document.id)
                            .get()
                            .addOnSuccessListener { name ->
                                val nickname = name.data!!.getValue("nickname").toString()
                                nicknameList.add(nickname)
                                userUidList.add(document.id)
                            }
                    }
                    chatusersAdapter.notifyDataSetChanged()
                }
            }
    }
}