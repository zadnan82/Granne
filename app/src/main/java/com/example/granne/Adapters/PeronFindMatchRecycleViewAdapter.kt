package com.example.granne

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.granne.Extras.Constants
import com.example.granne.Extras.Constants.NICKNAME
import com.example.granne.Extras.Constants.UID
import com.example.granne.Extras.PersonFindMatch
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PersonFindMatchRecycleViewAdapter(
    val context: Context, val persons: List<PersonFindMatch>
) : RecyclerView.Adapter<PersonFindMatchRecycleViewAdapter.ViewHolder>() {

    val db = Firebase.firestore
    val layoutInflater = LayoutInflater.from(context)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.list_item_match, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val person = persons[position]

        holder.nicknameTV.text = person.name.toString()
        holder.interestsTV.text = person.intressen.toString()
        holder.aboutmeTV.text = person.aboutMe.toString()

        holder.addBtn.setOnClickListener {
            val matchinguserNickname = person.name.toString()
            val matchinguserUID = person.uid.toString()
            var veryBadIdGenerator = (234234324..4343434345).random().toString()


            val mapOfDetails = hashMapOf(
                "nickname" to matchinguserNickname,
                "uid" to matchinguserUID,
                "chatId" to veryBadIdGenerator
            )

            Constants.FB_REF.collection("matchedUsers")
                .document(matchinguserUID).set(mapOfDetails)
                .addOnSuccessListener {

                    Toast.makeText(context, R.string.addtochat, Toast.LENGTH_SHORT).show()
                    addYourselfToSecondUserMatchedList(matchinguserUID, veryBadIdGenerator)
                }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nicknameTV = itemView.findViewById<TextView>(R.id.nicknameTV)
        val interestsTV = itemView.findViewById<TextView>(R.id.interestsTV)
        val aboutmeTV = itemView.findViewById<TextView>(R.id.aboutMeTV)
        val addBtn = itemView.findViewById<ImageButton>(R.id.addBtn)

    }

    private fun addYourselfToSecondUserMatchedList(matchinguserUID: String, chatId: String) {

        val mapOfDetails = hashMapOf(
            "nickname" to NICKNAME,
            "uid" to UID,
            "chatId" to chatId
        )

        db.collection("userData").document(matchinguserUID)
            .collection("matchedUsers").document(UID)
            .set(mapOfDetails)
            .addOnSuccessListener {
                // Added yourself to the other persons Matched users list!
            }
    }

    override fun getItemCount(): Int {
        return persons.size
    }
}