package com.example.notes

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.notes.models.Note

class DatabaseHelper(context:Context): SQLiteOpenHelper(context, Constants.DATABASE_NAME,
    null, Constants.DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE ${Constants.ENTITY_NAME} (" +
                "${Constants.PROPERTY_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${Constants.PROPERTY_DESC} VARCHAR(60), " +
                "${Constants.PROPERTY_FINISHED} BOOLEAN)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVer: Int, newVer: Int) {
        TODO("Not yet implemented")
    }

    @SuppressLint("Range")
    public fun getAllNotes(): MutableList<Note> {
        val notes: MutableList<Note> = mutableListOf()
        val db = this.readableDatabase
        val query = "SELECT * FROM ${Constants.ENTITY_NAME}"
        val result = db.rawQuery(query, null)
        if (result.moveToFirst()) {
            do {
                val newNote = Note()
                newNote.id = result.getLong(result.getColumnIndex(Constants.PROPERTY_ID))
                newNote.desc = result.getString(result.getColumnIndex(Constants.PROPERTY_DESC))
                newNote.isFinished = result.getInt(result.getColumnIndex(Constants.PROPERTY_FINISHED)) == Constants.TRUE

                notes.add(newNote)
            } while (result.moveToNext())
        }

        return notes
    }

    public fun insertNote(note:Note): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(Constants.PROPERTY_DESC, note.desc)
            put(Constants.PROPERTY_FINISHED, note.isFinished)
        }
        val resultId = db.insert(Constants.ENTITY_NAME, null, contentValues)
        return resultId
    }

    public fun updateNote(note:Note): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(Constants.PROPERTY_DESC, note.desc)
            put(Constants.PROPERTY_FINISHED, note.isFinished)
        }

        val result = db.update(Constants.ENTITY_NAME, contentValues,
            "${Constants.PROPERTY_ID} = ${note.id}", null)
        return result == Constants.TRUE

    }

    public fun deleteNote(note:Note):Boolean {
        val db = this.writableDatabase
        val result = db.delete(Constants.ENTITY_NAME,
            "${Constants.PROPERTY_ID} = ${note.id}", null)
        return result == Constants.TRUE
    }

}