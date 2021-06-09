package com.dictionaryapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.dictionaryapp.objects.DictionaryEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    var listItems = ArrayList<DictionaryEntity>()

    private var adapter = DictListCustomAdapter(this, listItems)
    lateinit var dictListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val userId = intent.getStringExtra("user_id")
//        val emailId = intent.getStringExtra("email_id")

//        println("UID "+userId)
//        println("EMAIL "+emailId)
        println(FirebaseAuth.getInstance().currentUser!!.uid)
        println(FirebaseAuth.getInstance().currentUser!!.email)

        dictListView = findViewById(R.id.dictionariesListView)
        dictListView.adapter = adapter


        val settingsButton: Button = findViewById(R.id.settingsButton)
        settingsButton.setOnClickListener {
            println("settings")
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        val searchButton: Button = findViewById(R.id.searchButton)
        searchButton.setOnClickListener {
            println("search")
            val intent = Intent(this, BrowseActivity::class.java)
            startActivity(intent)
        }

        val AddButton: Button = findViewById(R.id.addButton)
        AddButton.setOnClickListener {
            println("add")
            val intent = Intent(this, AddNewActivity::class.java)
            startActivity(intent)
        }

        dictListView.setOnItemClickListener { parent, view, position, id ->
//            val chosen = adapter.getItem(position)// The item that was clicked

//            Toast.makeText(
//                this@MainActivity,
//                "clicked " + listItems[position].dictId_notOnline,
//                Toast.LENGTH_SHORT
//            ).show()

//            listItems[position].timestampLastView = System.currentTimeMillis()
//

            val intent = Intent (this@MainActivity, DictionaryViewActivity::class.java)
            intent.putExtra("dictionary_id", listItems[position].dictId_notOnline)
            startActivity(intent)

//            Collections.sort(listItems,
//                Comparator { o1, o2 -> o2.timestampLastView.compareTo(o1.timestampLastView) })
//            adapter.updateListItems(listItems)
//            adapter.notifyDataSetChanged()


        }

    }

    override fun onResume() {
        super.onResume()

        val db = Firebase.firestore

        listItems.clear()
        adapter.updateListItems(listItems)
        adapter.notifyDataSetChanged()
        db.collection("dictionaries")
            .whereEqualTo("owner_uid", FirebaseAuth.getInstance().currentUser!!.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val data = document.toObject(DictionaryEntity::class.java)
                    data.dictId_notOnline = document.id
                    data.words = ArrayList() //bo nam tutaj nie potrzebne trzymac words w pamieci
                    listItems.add(data)

                }
                Collections.sort(listItems,
                    Comparator { o1, o2 -> o2.timestampLastView.compareTo(o1.timestampLastView) })
                adapter.updateListItems(listItems)
                adapter.notifyDataSetChanged()

            }
            .addOnFailureListener { exception ->
                println("Nothing found")
                Toast.makeText(
                    this@MainActivity,
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
            val rowMain = layoutInflater.inflate(R.layout.items_list_1, viewGroup, false)

            val nameTV = rowMain.findViewById<TextView>(R.id.list_name)
            nameTV.text = listItemsToShow[position].dictName

            val descriptionTV = rowMain.findViewById<TextView>(R.id.list_description)
            descriptionTV.text =
                listItemsToShow[position].lang1 + ", " + listItemsToShow[position].lang2

            return rowMain

        }

    }

}