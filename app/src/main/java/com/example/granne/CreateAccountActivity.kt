package com.example.granne

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CreateAccountActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    lateinit var registerBtn: Button
    lateinit var nicknameET: EditText
    lateinit var emailET: EditText
    lateinit var passwordET: EditText
    lateinit var termOfServiceTV: TextView
    lateinit var termOfServiceBox: CheckBox
    lateinit var cancelBtn: ImageButton
    lateinit var nickname: String
    lateinit var email: String
    lateinit var password: String
    private var TAG = "CreateAccountActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        auth = Firebase.auth

        registerBtn = findViewById(R.id.registerBtn)
        nicknameET = findViewById(R.id.nicknameET)
        emailET = findViewById(R.id.emailET)
        passwordET = findViewById(R.id.passwordET)
        termOfServiceTV = findViewById(R.id.tosTV)
        termOfServiceBox = findViewById(R.id.tosCheckBox)
        cancelBtn = findViewById(R.id.cancelBtn)

        cancelBtn.setOnClickListener {
            finish()
        }

        registerBtn.setOnClickListener {
            when {
                checkUserInputs() -> {
                    if (password.length >= 6 && nickname.length >= 6) {
                        if (termOfServiceBox.isChecked) {
                            createAccount(email, password, nickname)
                        } else Toast.makeText(
                            this, R.string.accept_tos, Toast.LENGTH_LONG
                        ).show()
                    } else Toast.makeText(
                        this, R.string.pass_6char, Toast.LENGTH_LONG
                    ).show()
                }

                !checkUserInputs() -> Toast.makeText(
                    this, R.string.empty, Toast.LENGTH_LONG
                ).show()
            }
        }

        termOfServiceTV.setOnClickListener {
            TosDialogFragment().show(supportFragmentManager, "tosDialog")
        }

    }

    private fun createAccount(email: String, password: String, nickname: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Toast.makeText(
                        this, R.string.success_create, Toast.LENGTH_LONG
                    ).show()

                    val user = auth.currentUser
                    val uid: String = user!!.uid

                    val currentUser = hashMapOf(
                        "nickname" to nickname,
                        "email" to email,
                        "uid" to user.uid,
                        "location" to "", //  These will be filled later in the app
                        "aboutme" to ""
                    )

                    db.collection("userData")
                        .document(uid).set(currentUser)
                        .addOnSuccessListener {
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                        }
                    updateUI(user)

                } else {
                    // Sign in failed
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        this, R.string.fail_login, Toast.LENGTH_LONG
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()

        } else Log.d(TAG, "User failed to log in")
    }

    private fun checkUserInputs(): Boolean {
        nickname = nicknameET.text.toString()
        email = emailET.text.toString().replaceFirstChar { it.lowercase() }
        password = passwordET.text.toString()

        return !(nickname.isEmpty() || email.isEmpty() || password.isEmpty())
    }
}
