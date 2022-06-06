package com.example.granne

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    lateinit var signInBtn: Button
    lateinit var emailET: EditText
    lateinit var passwordET: EditText
    lateinit var forgotPasswordTV: TextView
    lateinit var cancelBtn: ImageButton
    lateinit var email: String
    lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth
        signInBtn = findViewById(R.id.signInBtn)
        emailET = findViewById(R.id.emailET)
        passwordET = findViewById(R.id.passwordET)
        forgotPasswordTV = findViewById(R.id.forgotPasswordTV)
        cancelBtn= findViewById(R.id.cancelBtn)

        cancelBtn.setOnClickListener{
            finish()
        }

        forgotPasswordTV.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        signInBtn.setOnClickListener {
            when {
                checkUserInputs() -> {
                    if (password.length < 6) {
                        Toast.makeText(
                            this, R.string.pass_6char, Toast.LENGTH_LONG
                        ).show()
                    } else signIn(email, password)
                }
                !checkUserInputs() -> Toast.makeText(
                    this, R.string.empty, Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun checkUserInputs(): Boolean {
        email = emailET.text.toString()
        password = passwordET.text.toString()
        return !(email.isEmpty() || password.isEmpty())
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("!", "signInWithEmail:success")
                    Toast.makeText(
                        this, R.string.loggedin, Toast.LENGTH_LONG
                    ).show()
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w("!", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        this, R.string.fail_to_log, Toast.LENGTH_LONG
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            Log.d("!", "Logged in")
            startActivity(Intent(this, HomeActivity::class.java))
        } else Log.d("!", "User failed to log in")
    }
}