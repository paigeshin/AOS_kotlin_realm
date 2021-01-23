package com.paigesoftware.realm_kotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paigesoftware.realm_kotlin.databinding.ItemNoteBinding
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_add_note.view.*
import kotlinx.android.synthetic.main.item_note.view.*

class NoteAdapter(
    private val noteList: RealmResults<Note>
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private lateinit var binding: ItemNoteBinding

    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        binding = ItemNoteBinding.bind(view)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {

        val note = noteList[position]

        binding.textviewTitle.text = note!!.title
        binding.textviewDescription.text = note.description
        binding.textViewId.text = note.id.toString()

    }

    override fun getItemCount(): Int {
        return noteList.size
    }

}