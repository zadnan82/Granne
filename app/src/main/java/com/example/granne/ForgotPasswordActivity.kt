package com.example.granne

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    lateinit var submitBtn: Button
    lateinit var enterEmailET: EditText
    lateinit var cancelBtn : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = Firebase.auth

        submitBtn = findViewById(R.id.submitBtn)
        enterEmailET = findViewById(R.id.enterEmailET)
        cancelBtn = findViewById(R.id.cancelBtn)

        cancelBtn.setOnClickListener {
            finish()
        }

        submitBtn.setOnClickListener {
            val email: String = enterEmailET.text.toString().trim { it <= ' ' }
            if (email.isEmpty()) {

                Toast.makeText(this, "Please enter your email adress.",
                    Toast.LENGTH_SHORT).show()
            } else {
                Firebase.auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            Toast.makeText(this, "Email successfully sent to reset your password.",
                                Toast.LENGTH_LONG).show()
                            finish()
                        }
                        else{
                            Toast.makeText(this, task.exception!!.message.toString(),
                                Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }
}