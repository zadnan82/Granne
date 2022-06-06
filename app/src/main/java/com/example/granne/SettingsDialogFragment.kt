package com.example.granne

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import android.widget.AdapterView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.*

class SettingsDialogFragment : DialogFragment() {

    private lateinit var auth: FirebaseAuth
    lateinit var profileImage : ImageView
    val db = Firebase.firestore
    var choosenImageUrl: String? = null
    var choosenImageBitmap: Bitmap? = null
    var curFile: Uri? = null
    val imageRef = Firebase.storage.reference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.fragment_settings_dialog, container, false)
        val REQUEST_CODE_IMAGE_PICK = 0
        val nicknameET = rootView.findViewById<EditText>(R.id.newnicknameET)
        val nicknameBtn = rootView.findViewById<Button>(R.id.nicknameBtn)
        val browsePicturesBtn = rootView.findViewById<Button>(R.id.browsePicturesBtn)
        val spinner = rootView.findViewById<Spinner>(R.id.spinnerLocation)
        val signOutBtn = rootView.findViewById<Button>(R.id.signOutBtn)
        val locationBtn = rootView.findViewById<Button>(R.id.locationBtn)
        val deleteAccountBtn = rootView.findViewById<Button>(R.id.deleteAccountBtn)
        val applyPicturesBtn = rootView.findViewById<Button>(R.id.applyPicturesBtn)
        profileImage= rootView.findViewById(R.id.profileImage)
        val cancelBtn = rootView.findViewById<ImageButton>(R.id.cancelBtn)

        auth = Firebase.auth

        cancelBtn.setOnClickListener {
            dismiss()
        }

        nicknameBtn.setOnClickListener {
            val nickname = nicknameET.text.toString()
            when (nickname.isEmpty()) {
                true ->  Toast.makeText(activity, "Please enter a new username!", Toast.LENGTH_SHORT).show()
                false -> {
                    if (nickname.length < 6) {
                        Toast.makeText(activity, R.string.nickname_6char, Toast.LENGTH_SHORT).show()
                    } else {
                        db.collection("userData").document(Constants.UID)
                            .update("nickname", nickname)
                            .addOnSuccessListener {
                                Toast.makeText(activity, "Nickname changed!", Toast.LENGTH_SHORT).show()
                                nicknameET.setText("")
                            }

                            .addOnFailureListener { e -> Log.d("!", "Error:", e) }
                    }
                }
            }
        }

        val locations = listOf(
            "Svealand",
            "Götaland",
            "Norrland"
        )

        val arrayAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, locations)
        spinner.adapter = arrayAdapter

        locationBtn.setOnClickListener {
            val newLocation = spinner.selectedItem.toString()

            db.collection("userData").document(Constants.UID)
                .update("location", newLocation)
                .addOnSuccessListener {
                    Toast.makeText(activity, "Updated location", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.w("!", "Error adding document", e)
                }
        }

        browsePicturesBtn.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, REQUEST_CODE_IMAGE_PICK)
            }
        }

        applyPicturesBtn.setOnClickListener {
            uploadImage()
        }

        signOutBtn.setOnClickListener {
            auth.signOut()
            val intent = Intent(activity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        deleteAccountBtn.setOnClickListener {
            db.collection("userData").document(FirebaseAuth.getInstance().currentUser!!.uid)
                .delete()
                .addOnSuccessListener {
                    FirebaseAuth.getInstance().currentUser!!.delete()
                        .addOnCompleteListener {
                            Log.d("!", "User deleted in cloud database and auth")
                            Toast.makeText(activity, "Account deleted!", Toast.LENGTH_SHORT).show()
                            val returnToLoginScreen = Intent(activity, MainActivity::class.java)
                            startActivity(returnToLoginScreen)
                        }
                }
                .addOnFailureListener { error ->
                    Log.d("!", "Error! $error")
                }
        }
        return rootView
    }

    fun uploadImage() {
        if(choosenImageUrl != null || choosenImageBitmap == null) {
            uploadImageToStorage()
        }
        else if(choosenImageBitmap != null || choosenImageUrl != null) {
            uploadImageAsBitmapToStorage()
        }
    }

    fun uploadImageAsBitmapToStorage() {
        val bitmap = (profileImage.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
       // val uniqeString = UUID.randomUUID().toString()
        var uploadTask = imageRef.child("${Constants.UID}/profileimage").putBytes(data)

         uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
             imageRef.child("${Constants.UID}/profileimage").downloadUrl }
             .addOnCompleteListener { task ->
                 if (task.isSuccessful) {
                     val downloadUri = task.result
                     choosenImageUrl = downloadUri.toString()
            }
        }
    }

    private fun uploadImageToStorage() = CoroutineScope(Dispatchers.IO)
        .launch {
        try {
            curFile?.let {
                //val uniqeString = UUID.randomUUID().toString()
                val uploadTask = imageRef.child("${Constants.UID}/profileimage").putFile(it)
                 uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    imageRef.child("${Constants.UID}/profileimage")
                        .downloadUrl }.addOnCompleteListener { task ->
                     if (task.isSuccessful) {
                        val downloadUri = task.result
                        choosenImageUrl = downloadUri.toString()
                    }
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        fun innerCheck(name: String) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "$name tillåtelse nekad", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "$name tillåtelse godkänd", Toast.LENGTH_SHORT).show()
            }
        }
        when (requestCode) {
            CAMERA_REQUEST_CODE -> innerCheck("kamera")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_IMAGE_PICK) {
            data?.data?.let {
                curFile = it
                profileImage.setImageURI(it)
            }
        } else if (requestCode == START_REQUEST_CAMERA && resultCode == Activity.RESULT_OK && data != null) {
            val takenImage = data.extras?.get("data") as Bitmap
            profileImage.setImageBitmap(takenImage)
            choosenImageBitmap = takenImage
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}