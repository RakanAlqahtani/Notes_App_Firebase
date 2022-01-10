package com.example.notesappfirebase

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notesappfirebase.Model.Note
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.Continuation

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val notes: MutableLiveData<List<Note>> = MutableLiveData()


    private var db: FirebaseFirestore = Firebase.firestore


    fun getNotes(): LiveData<List<Note>> {
        return notes
    }

    fun addNote(note: Note) {
        CoroutineScope(Dispatchers.IO).launch {

// Create a new user with a first and last name
            val note = hashMapOf(
                "note" to "${note.noteText}",
            )

// Add a new document with a generated ID
            db.collection("notes")
                .add(note)
            getData()
        }
    }

     fun getData() {
         CoroutineScope(Dispatchers.IO).launch {

             db.collection("notes")
                 .get().addOnSuccessListener { result ->
                     val tempNotes = arrayListOf<Note>()
                     for (document in result) {
                         document.data.map { (kay, value) ->

                             tempNotes.add(Note(document.id, value.toString()))
                         }
                     }
                     notes.postValue(tempNotes)
                 }.addOnFailureListener {
//                Toast.makeText(this, "cant retrive data", Toast.LENGTH_LONG).show()
                 }

         }
     }


    fun updateNote(noteID: String, noteText : String){
        CoroutineScope(Dispatchers.IO).launch {
            db.collection("notes")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result){
                        if (document.id == noteID){
                            db.collection("notes")
                                .document(noteID).update("note",noteText)
                        }
                    }
                    getData()
                }.addOnFailureListener { exception ->
                    Log.w("MainActivity", "Error getting documents.", exception)
                }
        }

    }

    fun deleteNote(noteID : String){
        CoroutineScope(Dispatchers.IO).launch {
            db.collection("notes")
                .get().addOnSuccessListener { result ->
                    for (document in result){

                        if (document.id == noteID){
                            db.collection("notes").document(noteID).delete()
                        }
                    }
                    getData()
                }.addOnFailureListener { exception ->
                    Log.w("MainActivity", "Error getting documents.", exception)
                }
        }
    }

}

