package com.example.notesappfirebase

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesappfirebase.Model.Note
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var TAG: String = "iAmMainActivity"

    private lateinit var notes :List<String>
    private lateinit var adapter : RVAdapter
    lateinit var mainViewModel : MainActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        notes = arrayListOf()
        adapter = RVAdapter(this)
        rvMain.adapter = adapter
        rvMain.layoutManager = LinearLayoutManager(this)

        mainViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        mainViewModel.getNotes().observe(this,{
            note -> adapter.update(note)
        })

        btSave.setOnClickListener {
            if (etNote.text.isNotEmpty()) {
                mainViewModel.addNote(Note("",etNote.text.toString()))
                etNote.text.clear()
                etNote.clearFocus()
            }
        }

        mainViewModel.getData()
    }


    fun raiseDialog(id : String){
        val dialogBuilder = AlertDialog.Builder(this)
        val updateNote = EditText(this)
        updateNote.hint = "Enter new text"

        dialogBuilder
            .setCancelable(false)
            .setPositiveButton("Save", DialogInterface.OnClickListener{
                _,_ -> mainViewModel.updateNote(id,updateNote.text.toString())
            })
            .setNegativeButton("Cancel",DialogInterface.OnClickListener{
                dialog, _ -> dialog.cancel()
            })
        val alert = dialogBuilder.create()
        alert.setTitle("Update Note")
        alert.setView(updateNote)
        alert.show()
    }
}