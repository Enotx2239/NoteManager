package com.example.notemanagerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.*
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Room Database Setup
@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val tags: String
)

@Dao
interface NoteDao {
    @Query("SELECT * FROM Note")
    suspend fun getAllNotes(): List<Note>

    @Insert
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)
}

@Database(entities = [Note::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}

// ViewModel
class NoteViewModel(private val noteDao: NoteDao) : ViewModel() {
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            _notes.value = withContext(Dispatchers.IO) {
                noteDao.getAllNotes()
            }
        }
    }

    fun createNote(title: String, content: String, tags: String) {
        viewModelScope.launch {
            val note = Note(0, title, content, tags)
            withContext(Dispatchers.IO) {
                noteDao.insert(note)
            }
            loadNotes()
        }
    }
}

// Splash Screen
@Composable
fun SplashScreen(onFinish: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = rememberAsyncImagePainter("https://example.com/logo.png"),
                contentDescription = null
            )
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }
    }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)
        onFinish()
    }
}

// Main Screen
@Composable
fun MainScreen(viewModel: NoteViewModel) {
    val notes by viewModel.notes.collectAsState() // Обновляемый список заметок
    var currentScreen by remember { mutableStateOf("list") } // Управление экранами

    when (currentScreen) {
        "list" -> {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Button(onClick = { currentScreen = "create" }) {
                    Text("Create Note")
                }
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(notes) { note ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { /* Логика для просмотра заметки */ },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = note.title, style = MaterialTheme.typography.h6)
                                Text(text = note.content, style = MaterialTheme.typography.body1)
                                Text(text = note.tags, style = MaterialTheme.typography.caption)
                            }
                        }
                    }
                }
            }
        }
        "create" -> {
            CreateEditNoteScreen { title, content, tags ->
                viewModel.createNote(title, content, tags) // Сохранение заметки
                currentScreen = "list" // Возвращение на список
            }
        }
    }
}

// Create/Edit Note Screen
@Composable
fun CreateEditNoteScreen(onSave: (String, String, String) -> Unit) {
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var content by remember { mutableStateOf(TextFieldValue("")) }
    var tags by remember { mutableStateOf(TextFieldValue("")) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        BasicTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        BasicTextField(
            value = content,
            onValueChange = { content = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        BasicTextField(
            value = tags,
            onValueChange = { tags = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onSave(title.text, content.text, tags.text) }) {
            Text("Save")
        }
    }
}

// Main Activity
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "note-database"
        ).build()

        val viewModel = NoteViewModel(db.noteDao())

        setContent {
            var showSplash by remember { mutableStateOf(true) }
            if (showSplash) {
                SplashScreen { showSplash = false }
            } else {
                MainScreen(viewModel)
            }
        }
    }
}
