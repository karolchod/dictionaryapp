package com.dictionaryapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.dictionaryapp.objects.DictionaryEntity
import com.dictionaryapp.objects.Word
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.Comparator
import kotlin.collections.ArrayList

class BrowseActivity : AppCompatActivity() {

    var listItems = ArrayList<DictionaryEntity>()

    private var adapter = DictListCustomAdapter(this, listItems)
    lateinit var browseListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse)

//        println(FirebaseAuth.getInstance().currentUser!!.uid)
//        println(FirebaseAuth.getInstance().currentUser!!.email)

        browseListView = findViewById(R.id.browseListView)
        browseListView.adapter = adapter

        browseListView.setOnItemClickListener { parent, view, position, id ->
            val chosen = adapter.getItem(position)// The item that was clicked
            var item: DictionaryEntity
            val db = Firebase.firestore
            var num: Long
            num = 0

            val itemRef = db.collection("dictionaries/").document(chosen.toString())

            itemRef.get()
                .addOnSuccessListener { document ->

                    item = document.toObject(DictionaryEntity::class.java)!!
                    val alert = AlertDialog.Builder(this)
                    alert.setTitle("Do you want to make a copy of \"" + item.dictName + "\" dictionary?")
                    alert.setMessage(item.lang1 + ", " + item.lang2 + " by " + item.owner_nickname)
                    alert.setPositiveButton("Copy") { _, _ ->
                        with(item) {
                            num = item.number_of_copies

                            num += 1
                            itemRef.update("number_of_copies", num)

                            item.dictId_notOnline = "-1"
                            item.dictName =
                                "Copy of " + document.toObject(DictionaryEntity::class.java)!!.dictName
                            item.number_of_copies = 1
                            item.public = false
                            item.owner_uid = FirebaseAuth.getInstance().currentUser!!.uid
                            item.timestampLastView = System.currentTimeMillis()
                            val db = Firebase.firestore

                            db.collection("dictionaries")
                                .add(item)
                                .addOnSuccessListener { documentReference ->
                                    Toast.makeText(
                                        this@BrowseActivity,
                                        "Dictionary " + document.toObject(DictionaryEntity::class.java)!!.dictName + " copied",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        this@BrowseActivity,
                                        "Error adding document" + e,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                        }
                        onResume()

                    }
                    alert.setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    alert.setCancelable(false)
                    alert.show()


                }
                .addOnFailureListener { exception ->
                    println("Nothing found")
                    Toast.makeText(
                        this@BrowseActivity,
                        "error: Dictionary not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }


        }

    }

    override fun onResume() {
        super.onResume()

        val db = Firebase.firestore

        listItems.clear()
        db.collection("dictionaries")
            .whereEqualTo("public", true)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val data = document.toObject(DictionaryEntity::class.java)
                    data.dictId_notOnline = document.id
                    data.words = ArrayList() //bo nam tutaj nie potrzebne trzymac words w pamieci
                    listItems.add(data)

                }
                Collections.sort(listItems,
                    Comparator { o1, o2 -> o2.number_of_copies.compareTo(o1.number_of_copies) })
                adapter.updateListItems(listItems)
                adapter.notifyDataSetChanged()

            }
            .addOnFailureListener { exception ->
                println("Nothing found")
                Toast.makeText(
                    this@BrowseActivity,
                    "No dictionaries found",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private class DictListCustomAdapter(context: Context, listItems: ArrayList<DictionaryEntity>) :
        BaseAdapter() {

        private val mContext: Context
        private var listItemsToShow: ArrayList<DictionaryEntity>

        init {
            mContext = context
            listItemsToShow = listItems
        }

        fun updateListItems(listItems: ArrayList<DictionaryEntity>) {
            listItemsToShow = listItems
        }

        //how many rows in list
        override fun getCount(): Int {
            return listItemsToShow.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItem(position: Int): Any {
            return listItemsToShow[position].dictId_notOnline
        }

        //rendering each row
        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup): View {
//            val textView = TextView(mContext)
//            textView.text = "Row for listview"
//            return textView

            val layoutInflater = LayoutInflater.from(mContext)
            val rowMain = layoutInflater.inflate(R.layout.items_browse_1, viewGroup, false)

            val nameTV = rowMain.findViewById<TextView>(R.id.list_name)
            nameTV.text = listItemsToShow[position].dictName

            val descriptionTV = rowMain.findViewById<TextView>(R.id.list_description)
            descriptionTV.text =
                listItemsToShow[position].lang1 + ", " + listItemsToShow[position].lang2 + " by " + listItemsToShow[position].owner_nickname + " (copied " + listItemsToShow[position].number_of_copies + " times)"


            return rowMain

        }

    }
}