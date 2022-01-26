package com.example.notes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notes.adapters.NoteAdapter
import com.example.notes.databinding.ActivityMainBinding
import com.example.notes.models.Note
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var noteFinishAdapter: NoteAdapter
    private lateinit var db:DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)


        noteAdapter = NoteAdapter(mutableListOf(), this)
        binding.rvNotes.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = noteAdapter
        }

        noteFinishAdapter = NoteAdapter(mutableListOf(), this)
        binding.rvNotesFinished.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = noteFinishAdapter
        }

        binding.btnAdd.setOnClickListener {
            if (binding.tietDesc.text.toString().isNotBlank()) {
                val note = Note( desc = binding.tietDesc.text.toString().trim())
                note.id = db.insertNote(note)

                if(note.id != Constants.ID_ERROR) {
                    addNoteAuto(note)
                    binding.tietDesc.text?.clear()
                    showMessage(R.string.db_write_succes)
                } else {
                    showMessage(R.string.db_write_error)
                }

            } else {
                binding.tietDesc.error = getString(R.string.validation_field_required)
                Snackbar.make(binding.root, getString(R.string.validation_error), Snackbar.LENGTH_SHORT).show()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        getData()
    }

    private fun getData() {

        val data = db.getAllNotes()
        data.forEach { note ->
            addNoteAuto(note)
        }
    }

    private fun addNoteAuto(note: Note) {
        if (note.isFinished) {
            noteFinishAdapter.add(note)
        } else {
            noteAdapter.add(note)
        }

    }

    private fun deleteNoteAuto(note: Note) {
        if (note.isFinished) {
            noteAdapter.remove(note)
        } else {
            noteFinishAdapter.remove(note)
        }

    }

    override fun onLongClick(note: Note, currentAdapter: NoteAdapter) {

        val builder = AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_title))
            .setPositiveButton(getString(R.string.dialog_ok), { dialogInterface, i ->

                if (db.deleteNote(note)) {
                    currentAdapter.remove(note)
                    showMessage(R.string.db_delete_succes)
                } else {
                    showMessage(R.string.db_delete_error)
                }


            }).setNegativeButton(getString(R.string.dialog_cancel), null)
        builder.create().show()

    }

    override fun onCheck(note: Note) {
        if (db.updateNote(note)) {
            deleteNoteAuto(note)
            addNoteAuto(note)
            showMessage(R.string.db_update_succes)
        } else {
            showMessage(R.string.db_update_error)
        }

    }

    private fun showMessage(msg:Int) {
        Snackbar.make(binding.root, getString(msg), Snackbar.LENGTH_SHORT).show()
    }

}