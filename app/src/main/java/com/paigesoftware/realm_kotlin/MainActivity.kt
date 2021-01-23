package com.paigesoftware.realm_kotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.paigesoftware.realm_kotlin.databinding.ActivityMainBinding
import io.realm.OrderedRealmCollectionChangeListener
import io.realm.Realm
import io.realm.RealmResults

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var realm: Realm
    private var noteList = ArrayList<Note>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        realm = Realm.getDefaultInstance()

        binding.fabAddNote.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
            finish()
        }

        binding.recyclerviewNotes.layoutManager =
            StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        getAllNotes()

    }

    private fun getAllNotes() {
        noteList.clear()

        val results: RealmResults<Note> = realm.where<Note>(Note::class.java).findAll()
        val adapter = NoteAdapter(results)
        binding.recyclerviewNotes.adapter = adapter

    }

    private fun realmCRUD() {

        //read with listener
        val callback: OrderedRealmCollectionChangeListener<RealmResults<Note>> =
            OrderedRealmCollectionChangeListener { results, changeSet ->
                if (changeSet == null) {
                    // The first time async returns with an null changeSet.
                } else {
                    // Called on every update.
                }
            }

        val readResult: RealmResults<Note> = realm.where<Note>(Note::class.java).findAll()
        readResult.addChangeListener { results, changeSet ->
            if (changeSet == null) {
                //do something...

            } else {
                //calls on every update
            }
        }
        readResult.addChangeListener(callback)

        if (isFinishing) {
            readResult.removeAllChangeListeners()
            //or
            readResult.removeChangeListener(callback)
        }

        // async
        val result = realm.where<Note>(Note::class.java).findAllAsync()
        result.load() // be careful, this will block the current thread until it returns

        //update
        realm.beginTransaction()
        val updateResult: Note? = realm.where<Note>(Note::class.java).lessThan("id", 3).findFirst()
        updateResult?.description = "kkk"
        realm.commitTransaction()

        //delete
        realm.beginTransaction()
        val deleteResult: Note? = realm.where(Note::class.java).equalTo("id", 2.toInt()).findFirst()
        deleteResult?.deleteFromRealm()
        realm.commitTransaction()

        //query
        val queryResult: RealmResults<Note> = realm.where<Note>(Note::class.java)
            .equalTo("id", 1.toInt())
            .or()
            .equalTo("id", 2.toInt())
            .findAll()


    }

}