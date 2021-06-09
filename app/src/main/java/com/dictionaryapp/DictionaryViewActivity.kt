package com.dictionaryapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.dictionaryapp.objects.DictionaryEntity
import com.dictionaryapp.objects.Word
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class DictionaryViewActivity : AppCompatActivity() {

    var item = DictionaryEntity()
    lateinit var dictionary_id: String

    private var adapter = DictionaryViewActivity.ViewDictListCustomAdapter(this, item.words)
    lateinit var dictViewListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary_view)

        dictionary_id = intent.getStringExtra("dictionary_id").toString()

        dictViewListView = findViewById(R.id.dictViewListView)
        dictViewListView.adapter = adapter

        val dictTopTV: TextView = findViewById(R.id.dictTopTextView)
        dictTopTV.text = ""

        val editDictButton: Button = findViewById(R.id.editDictButton)
        editDictButton.setOnClickListener {
            println("edit")
            val intent = Intent(this, DictionaryEditActivity::class.java)
            intent.putExtra("dictionary_id", dictionary_id)
            startActivity(intent)


        }


        val addWordDictButton: Button = findViewById(R.id.addWordDictButton)
        addWordDictButton.setOnClickListener {
            println("add")
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Add new words")
//            alert.setMessage("message")
            val layout = LinearLayout(this)
            layout.orientation = LinearLayout.VERTICAL
            val word1ET = EditText(this)
            word1ET.setSingleLine()
            word1ET.hint = item.lang1
            layout.addView(word1ET)
            val word2ET = EditText(this)
            word2ET.setSingleLine()
            word2ET.hint = item.lang2
            layout.addView(word2ET)
            layout.setPadding(50, 40, 50, 10)
            alert.setView(layout)
            alert.setPositiveButton("Add") { _, _ ->
                val word1 = word1ET.text.toString()
                val word2 = word2ET.text.toString()

                if (word1.isEmpty() || word2.isEmpty()) {
                    Toast.makeText(
                        this@DictionaryViewActivity,
                        "Please enter words",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    item.words.add(Word(word1, word2))

                    val db = Firebase.firestore
                    val itemRef = db.collection("dictionaries/").document(dictionary_id)
                    itemRef.update("words", item.words)
                    onResume()
                }
            }
            alert.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            alert.setCancelable(false)
            alert.show()
        }

        dictViewListView.setOnItemLongClickListener(OnItemLongClickListener { arg0, arg1, pos, id ->
//            Toast.makeText(applicationContext, "long clicked", Toast.LENGTH_SHORT).show()
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Do you want to delete words?")
            alert.setMessage(item.words[pos].word1+"\n"+item.words[pos].word2)

            alert.setPositiveButton("Delete") { _, _ ->
                    item.words.removeAt(pos)
                    val db = Firebase.firestore
                    val itemRef = db.collection("dictionaries/").document(dictionary_id)
                    itemRef.update("words", item.words)
                onResume()
            }
            alert.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            alert.setCancelable(false)
            alert.show()
            true
        })


    }

    override fun onResume() {
        super.onResume()

        val db = Firebase.firestore

        val itemRef = db.collection("dictionaries/").document(dictionary_id)
//            .whereEqualTo("owner_uid", FirebaseAuth.getInstance().currentUser!!.uid)
        itemRef.get()
            .addOnSuccessListener { document ->
//                for (document in documents) {
                val data = document.toObject(DictionaryEntity::class.java)
                data!!.dictId_notOnline = document.id
                item = data


                val dictTopTV: TextView = findViewById(R.id.dictTopTextView)
                dictTopTV.text = item.dictName

                val lang1TV: TextView = findViewById(R.id.lang1textView)
                lang1TV.text = item.lang1

                val lang2TV: TextView = findViewById(R.id.lang2textView)
                lang2TV.text = item.lang2

                adapter.updateListItems(item.words)
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                println("Nothing found")
                Toast.makeText(
                    this@DictionaryViewActivity,
                    "error: Dictionary not found",
                    Toast.LENGTH_SHORT
                ).show()
            }

        itemRef.update("timestampLastView", System.currentTimeMillis())

    }

    private class ViewDictListCustomAdapter(context: Context, listItems: ArrayList<Word>) :
        BaseAdapter() {

        private val mContext: Context
        private var listItemsToShow: ArrayList<Word>

        init {
            mContext = context
            listItemsToShow = listItems
        }

        fun updateListItems(listItems: ArrayList<Word>) {
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
            return "getItem not needed"
        }

        //rendering each row
        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup): View {
//            val textView = TextView(mContext)
//            textView.text = "Row for listview"
//            return textView

            val layoutInflater = LayoutInflater.from(mContext)
            val rowMain = layoutInflater.inflate(R.layout.items_view_1, viewGroup, false)

            val word1TV = rowMain.findViewById<TextView>(R.id.word1)
            word1TV.text = listItemsToShow[position].word1

            val word2TV = rowMain.findViewById<TextView>(R.id.word2)
            word2TV.text = listItemsToShow[position].word2

            return rowMain

        }


    }
}