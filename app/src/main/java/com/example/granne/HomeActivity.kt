package com.example.granne

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.granne.Constants.update
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val REQUEST_CODE_IMAGE_PICK = 0
const val CAMERA_REQUEST_CODE = 1
const val START_REQUEST_CAMERA = 2


class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    lateinit var nicknameUnderIcon: TextView
    lateinit var findMatchBtn: ImageButton
    lateinit var optionsBtn: ImageButton
    lateinit var chatBtn: ImageButton
    lateinit var infoBtn: ImageButton
    lateinit var profilePicture : ImageView

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
        profilePicture = findViewById(R.id.profilePicture)


        val imageRef = FirebaseStorage.getInstance().getReference().child("${Constants.UID}/profileimage")
        val storageReference = Firebase.storage.reference

        imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener {
            Glide.with(this )
                .load(storageReference)
                .into(profilePicture)
        }.addOnFailureListener {
        }

        nicknameUnderIcon.text = Constants.NICKNAME

        chatBtn.setOnClickListener {
            startActivity(Intent(this, ChatListActivity::class.java))
        }

        optionsBtn.setOnClickListener {
            val dialog = SettingsDialogFragment()
            dialog.show(supportFragmentManager, "optionsdialog")
        }

        findMatchBtn.setOnClickListener {
            startActivity(Intent(this, FindMatchActivity::class.java))
        }

        infoBtn.setOnClickListener {
            val dialog = CustomDialogFragment()
            dialog.show(supportFragmentManager, "customDialog")
        }
    }

    fun statsDialogButton(view: View) {
        val statsDialogFragment = StatsDialogFragment()
        statsDialogFragment.show(supportFragmentManager, "statsDialog")
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}
