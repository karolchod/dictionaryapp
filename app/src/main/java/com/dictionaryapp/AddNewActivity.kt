package com.dictionaryapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.dictionaryapp.objects.DictionaryEntity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AddNewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new)

        val lang1Spinner: Spinner = findViewById(R.id.lang1spinner)
        val lang2Spinner: Spinner = findViewById(R.id.lang2spinner)

        val locale: Array<String> = arrayOf("Other","English","Polish","Russian","German","Korean","Spanish","French","Portuguese","Italian","Chinese","Japanese","Arabic","Turkish")

        val adapterLang1: ArrayAdapter<Any?> =
            ArrayAdapter<Any?>(this, R.layout.spinner_item, locale)
        val adapterLang2: ArrayAdapter<Any?> =
            ArrayAdapter<Any?>(this, R.layout.spinner_item, locale)

        adapterLang1.setDropDownViewResource(R.layout.spinner_dropdown_item)
        adapterLang2.setDropDownViewResource(R.layout.spinner_dropdown_item)

        lang1Spinner.adapter = adapterLang1
        lang2Spinner.adapter = adapterLang2

        val publicSwitch: Switch = findViewById(R.id.publicSwitch)
        val et_name: TextInputEditText = findViewById(R.id.etnewname)

        val addButton: Button = findViewById(R.id.addButton)
        addButton.setOnClickListener{
            when{
                TextUtils.isEmpty(et_name.text.toString().trim {it <= ' '}) ->{
                    Toast.makeText(
                        this@AddNewActivity,
                        "Please enter dictionary name.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {

                    println("NAME: "+et_name.text.toString())
                    println(lang1Spinner.selectedItem.toString())
                    println(lang2Spinner.selectedItem.toString())
                    println("Public: "+publicSwitch.isChecked)

                    var new_dict = DictionaryEntity()

                    with(new_dict){
                        dictName = et_name.text.toString()
                        owner_uid = FirebaseAuth.getInstance().currentUser!!.uid
                        owner_nickname= FirebaseAuth.getInstance().currentUser!!.email!!.substringBefore("@")
                        lang1 = lang1Spinner.selectedItem.toString()
                        lang2 = lang2Spinner.selectedItem.toString()
                        timestampLastView = System.currentTimeMillis()
//                        number_of_copies = 1
                        public = publicSwitch.isChecked()


                        val db = Firebase.firestore

                        db.collection("dictionaries")
                            .add(new_dict)
                            .addOnSuccessListener { documentReference ->
                                Toast.makeText(
                                    this@AddNewActivity,
                                    "Dictionary "+ et_name.text.toString()+" added",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this@AddNewActivity,
                                    "Error adding document"+ e,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                    }

                    val intent = Intent(this@AddNewActivity, MainActivity::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }


        }

    }
}