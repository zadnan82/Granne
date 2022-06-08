package com.example.granne

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var languageSpinner: Spinner
    lateinit var locale: Locale
    private var currentLanguage = "en"
    private var currentLang: String? = null
    private var TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        auth = Firebase.auth

        autoLogin()

        currentLanguage = intent.getStringExtra(currentLang).toString()
        languageSpinner = findViewById(R.id.languageSpinner)

        val list = ArrayList<String>()
        list.add("Language")
        list.add("English")
        list.add("Svenska")

        val adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long,
            ) {
                when (position) {
                    0 -> {
                    }
                    1 -> setLocale("en")
                    2 -> setLocale("sv")
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    fun autoLogin() {

        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            Log.d(TAG,"Auto logged in with email: ${auth.currentUser!!.email}")
        } else {
            Log.d(TAG, "No user logged in")
        }
    }
    private fun setLocale(localeName: String) {
        if (localeName != currentLanguage) {
            locale = Locale(localeName)
            val res = resources
            val dm = res.displayMetrics
            val conf = res.configuration

            conf.locale = locale
            res.updateConfiguration(conf, dm)
            val refresh = Intent(this, MainActivity::class.java)
            refresh.putExtra(currentLang, localeName)
            startActivity(refresh)
        } else {
            Toast.makeText(this, R.string.language_selected, Toast.LENGTH_SHORT
            ).show();
        }
    }


    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
        exitProcess(0)
    }

    fun startLoginActivity(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    fun startCreateAccount(view: View) {
        startActivity(Intent(this, CreateAccountActivity::class.java))
    }


}


