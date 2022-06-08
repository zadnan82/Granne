package com.example.granne.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.ImageButton
import com.example.granne.Chat.ChatListActivity
import com.example.granne.Extras.Constants.NICKNAME
import com.example.granne.Extras.Constants.update
import com.example.granne.Fragments.CustomDialogFragment
import com.example.granne.Fragments.SettingsDialogFragment
import com.example.granne.Fragments.StatsDialogFragment
import com.example.granne.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    lateinit var nicknameUnderIcon: TextView
    lateinit var findMatchBtn: ImageButton
    lateinit var optionsBtn: ImageButton
    lateinit var chatBtn: ImageButton
    lateinit var infoBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        update()

        auth = Firebase.auth

        nicknameUnderIcon = findViewById(R.id.nicknameUnderIcon)
        findMatchBtn = findViewById(R.id.findMatchBtn)
        optionsBtn = findViewById(R.id.optionsBtn)
        chatBtn = findViewById(R.id.chatBtn)
        infoBtn = findViewById(R.id.infoBtn)

        // Nickname is already downloaded in Constants File
        nicknameUnderIcon.text = NICKNAME

        chatBtn.setOnClickListener {
            startActivity(Intent(this, ChatListActivity::class.java))
        }

        optionsBtn.setOnClickListener {
            SettingsDialogFragment().show(supportFragmentManager, "optionsdialog")
        }

        findMatchBtn.setOnClickListener {
            startActivity(Intent(this, FindMatchActivity::class.java))
        }

        infoBtn.setOnClickListener {
            CustomDialogFragment().show(supportFragmentManager, "customDialog")
        }
    }

    fun statsDialogButton(view: View) {
        StatsDialogFragment().show(supportFragmentManager, "statsDialog")
    }

    override fun onBackPressed() {
        // When user presses back, application closes
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}
