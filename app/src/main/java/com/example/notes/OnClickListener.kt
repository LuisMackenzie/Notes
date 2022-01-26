package com.example.notes

import com.example.notes.adapters.NoteAdapter
import com.example.notes.models.Note

interface OnClickListener {
    public abstract fun onLongClick(note:Note, currentAdapter: NoteAdapter)
    public abstract fun onCheck(note:Note)
}