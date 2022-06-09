package com.example.granne.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.granne.Chat.ChatRoomActivity
import com.example.granne.R


class ChatRecyclerAdapter(
    private var nickname: List<String>,
    private var userUid: MutableList<String>,
) : RecyclerView.Adapter<ChatRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chatNickNameTV: TextView = itemView.findViewById(R.id.chatNickNameTV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.user_item_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.chatNickNameTV.text = nickname[position]

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ChatRoomActivity::class.java)
                .putExtra("secondUserNickname", nickname[position])
                .putExtra("secondUserUid", userUid[position])
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return nickname.size
    }

}