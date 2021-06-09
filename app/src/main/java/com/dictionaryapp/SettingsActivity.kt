package com.dictionaryapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dictionaryapp.objects.DictionaryEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val usernameTV: TextView = findViewById(R.id.usernameTextView)
        usernameTV.text = FirebaseAuth.getInstance().currentUser!!.email

        val logoutButton: Button = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        val deleteButton: Button = findViewById(R.id.deleteButton)
        deleteButton.setOnClickListener {

                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setTitle("Delete account")
                alertDialogBuilder.setMessage("Are you sure you want to delete account?")

                alertDialogBuilder.setPositiveButton("Delete") { dialog, which ->


                    val user = Firebase.auth.currentUser!!
                    val id = Firebase.auth.currentUser!!.uid

                    user.delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    this@SettingsActivity,
                                    "User account deleted.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            val db = Firebase.firestore

                            db.collection("dictionaries")
                                .whereEqualTo("owner_uid", id)
                                .get()
                                .addOnSuccessListener { documents ->
                                    for (document in documents) {
                                        val itemRef = db.collection("dictionaries/").document(document.id)
                                        itemRef.delete()
                                    }
                                }
                            val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { exception ->
                            val alertDialogBuilder2 = AlertDialog.Builder(this)
                            alertDialogBuilder2.setTitle("Can't delete account")
                            alertDialogBuilder2.setMessage("Please logout, login and try again")

                            alertDialogBuilder2.setPositiveButton("Ok"){ dialog, which ->
                                println("not deleting")
                            }
                        }

                }
                alertDialogBuilder.setNegativeButton("Cancel") { dialog, which ->
                    println("not deleting")

                }
                alertDialogBuilder.show()

        }
    }

}