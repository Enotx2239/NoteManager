package com.example.notemanagerapp.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notemanagerapp.data.Note
import com.example.notemanagerapp.data.NoteDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NoteViewModel(private val noteDao: NoteDao) : ViewModel() {
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            _notes.value = noteDao.getAllNotes()
        }
    }

    fun addNote(note: Note) {
        viewModelScope.launch {
            noteDao.insert(note)
            loadNotes()
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteDao.delete(note)
            loadNotes()
        }
    }
}
