package com.paigesoftware.realm_kotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.paigesoftware.realm_kotlin.databinding.ActivityAddNoteBinding
import io.realm.Realm
import java.lang.Exception

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        realm = Realm.getDefaultInstance()

        binding.buttonSave.setOnClickListener {
            addNoteToDB()
        }

    }

    private fun addNoteToDB() {

        try {

            // Auto Increment id

            realm.beginTransaction()

            val currentNumber: Number? = realm.where(Note::class.java).max("id")
            val nextId: Int = if(currentNumber == null) {
                1
            } else {
                currentNumber.toInt() + 1
            }

            val note = Note()
            note.title = binding.edittextTitle.text.toString()
            note.description = binding.edittextDescription.text.toString()
            note.id = nextId

            // copy this transiction & commit
            realm.copyToRealmOrUpdate(note)
            realm.commitTransaction()

            Toast.makeText(this, "Notes Added Successfully", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()


        } catch (e: Exception) {

            e.printStackTrace()

        }

    }

}