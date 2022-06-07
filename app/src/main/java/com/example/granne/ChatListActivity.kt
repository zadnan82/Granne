package com.example.granne

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.granne.Constants.FB_REF


class ChatListActivity : AppCompatActivity() {

    private var nicknameList = mutableListOf<String>()
    private var userUidList = mutableListOf<String>()
    private lateinit var chatUsersRV: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        chatUsersRV = findViewById(R.id.chatUsersRV)

        getAllMatchedUsers()
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

        chatUsersRV.layoutManager = LinearLayoutManager(this)
        chatUsersRV.adapter = ChatRecyclerAdapter(nicknameList, userUidList)
        nicknameList.add(nickname)
        userUidList.add(userUid)
    }

}