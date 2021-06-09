package com.dictionaryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import com.dictionaryapp.R
import com.dictionaryapp.objects.DictionaryEntity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DictionaryEditActivity : AppCompatActivity() {


    var item = DictionaryEntity()
    lateinit var dictionary_id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary_edit)

        dictionary_id = intent.getStringExtra("dictionary_id").toString()

        val lang1EditSpinner: Spinner = findViewById(R.id.lang1Editspinner)
        val lang2EditSpinner: Spinner = findViewById(R.id.lang2Editspinner)

        val locale: Array<String> = arrayOf("Other","English", "Polish", "Russian", "German", "Korean", "Spanish", "French", "Portuguese", "Italian", "Chinese", "Japanese", "Arabic", "Turkish")

        val adapterLang1: ArrayAdapter<Any?> =
            ArrayAdapter<Any?>(this, R.layout.spinner_item, locale)
        val adapterLang2: ArrayAdapter<Any?> =
            ArrayAdapter<Any?>(this, R.layout.spinner_item, locale)

        adapterLang1.setDropDownViewResource(R.layout.spinner_dropdown_item)
        adapterLang2.setDropDownViewResource(R.layout.spinner_dropdown_item)

        lang1EditSpinner.adapter = adapterLang1
        lang2EditSpinner.adapter = adapterLang2


        val publicSwitch: Switch = findViewById(R.id.publicEditSwitch)
        val et_name: TextInputEditText = findViewById(R.id.etEditname)



        val db = Firebase.firestore
        val itemRef = db.collection("dictionaries/").document(dictionary_id)
        itemRef.get()
            .addOnSuccessListener { document ->
                val data = document.toObject(DictionaryEntity::class.java)
                data!!.dictId_notOnline = document.id
                item = data

                val dictEditTopTV: TextView = findViewById(R.id.dictEditTopTextView)
                dictEditTopTV.text = "Edit " + item.dictName

                //fill items on screen with data of dictionary
                lang1EditSpinner.setSelection(locale.indexOf(item.lang1))
                lang2EditSpinner.setSelection(locale.indexOf(item.lang2))

                et_name.setText(item.dictName)
                publicSwitch.isChecked=item.public

            }
            .addOnFailureListener { exception ->
                println("Nothing found")
                Toast.makeText(
                    this@DictionaryEditActivity,
                    "error: Dictionary not found",
                    Toast.LENGTH_SHORT
                ).show()
            }

        val saveButton: Button = findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            when {
                TextUtils.isEmpty(et_name.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@DictionaryEditActivity,
                        "Please enter dictionary name.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {

                    println("NAME: " + et_name.text.toString())
                    println(lang1EditSpinner.selectedItem.toString())
                    println(lang2EditSpinner.selectedItem.toString())
                    println("Public: " + publicSwitch.isChecked)

                    with(item) {
                        dictName = et_name.text.toString()
                        lang1 = lang1EditSpinner.selectedItem.toString()
                        lang2 = lang2EditSpinner.selectedItem.toString()
                        timestampLastView = System.currentTimeMillis()
                        public = publicSwitch.isChecked()

                        val db = Firebase.firestore

                        db.collection("dictionaries/").document(dictionary_id)
                            .delete()
                            .addOnSuccessListener { documentReference ->
                                val intent = Intent(this@DictionaryEditActivity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }.addOnFailureListener { e ->
                                Toast.makeText(
                                    this@DictionaryEditActivity,
                                    "Error while deleting document: "+e,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        db.collection("dictionaries")
                            .add(item)
                            .addOnSuccessListener { documentReference ->
                                Toast.makeText(
                                    this@DictionaryEditActivity,
                                    "Dictionary " + et_name.text.toString() + " changed",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                                onBackPressed()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this@DictionaryEditActivity,
                                    "Error changing document" + e,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }

                }
            }
        }

        val deleteButton: Button = findViewById(R.id.deleteDictButton)
        deleteButton.setOnClickListener {
            val db = Firebase.firestore
            db.collection("dictionaries/").document(dictionary_id)
                .delete()
                .addOnSuccessListener { documentReference ->
                    val intent = Intent(this@DictionaryEditActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }.addOnFailureListener { e ->
                    Toast.makeText(
                        this@DictionaryEditActivity,
                        "Error while deleting document: "+e,
                        Toast.LENGTH_SHORT
                    ).show()
                }


        }

    }
}