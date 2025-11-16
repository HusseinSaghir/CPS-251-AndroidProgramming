package com.example.notesapp

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

private val Teal = Color(0xFF03DAC6) // Can be used here as well!

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(viewModel: NoteViewModel) {

    val notes by viewModel.notes.collectAsState()


    var titleInput by remember { mutableStateOf("") }

    var contentInput by remember { mutableStateOf("") }

    var titleTouched by remember { mutableStateOf(false) }

    var contentTouched by remember { mutableStateOf(false) }

    var editingNote by remember { mutableStateOf<Note?>(null) }

    var showDeleteDialog by remember { mutableStateOf<Note?>(null) }

    var importantNotes by remember { mutableStateOf(setOf<Int>()) }

    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    val snackbarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()



    // Scaffold provides a basic screen layout with TopAppBar, FAB, and SnackBarHost
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Material Notes") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Reset form to add new note
                    editingNote = null
                    titleInput = ""
                    contentInput = ""
                    titleTouched = false
                    contentTouched = false
                },
                //containerColor = Teal, //We can insert our color here
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Note")
            }
        },

        /*
        3. Focus management is important for this assignment and ones moving forward because
        it should simplify and make the user experience more enjoyable. In this example
        we are using the FAB to automatically open the keyboard and position the cursor within
        the text field saving our user a tiny bit of time not having to manually do it.
        It may seem insignificant but if we have multiple functions like this in an app
        the time saves will stack up.
         */


        floatingActionButtonPosition = FabPosition.Center,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = padding.calculateTopPadding()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Form Card for Create/Edit Note
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Title for the form section
                    Text(
                        text = if (editingNote == null) "Create New Note" else "Edit Note",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title input field
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = {
                            titleInput = it
                            titleTouched = true
                        },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = titleTouched && titleInput.isBlank(),
                        supportingText = {
                            if (titleTouched && titleInput.isBlank()) {
                                Text(
                                    text = "Title cannot be empty",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Content input field
                    OutlinedTextField(
                        value = contentInput,
                        onValueChange = {
                            contentInput = it
                            contentTouched = true
                        },
                        label = { Text("Content") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        isError = contentTouched && contentInput.isBlank(),
                        supportingText = {
                            if (contentTouched && contentInput.isBlank()) {
                                Text(
                                    text = "Content cannot be empty",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Button row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                if (titleInput.isNotBlank() && contentInput.isNotBlank()) {
                                    if (editingNote == null) {
                                        // Add new note
                                        viewModel.addNote(titleInput, contentInput, dateFormat.format(Date()))
                                        scope.launch { snackbarHostState.showSnackbar("Note added!") }
                                    } else {
                                        // Update existing note
                                        viewModel.deleteNote(editingNote!!)
                                        viewModel.addNote(titleInput, contentInput, dateFormat.format(Date()))
                                        scope.launch { snackbarHostState.showSnackbar("Note updated!") }
                                        editingNote = null
                                    }
                                    // Clear input fields
                                    titleInput = ""
                                    contentInput = ""
                                    titleTouched = false
                                    contentTouched = false
                                } else {
                                    titleTouched = true
                                    contentTouched = true
                                }
                            },
                            enabled = titleInput.isNotBlank() && contentInput.isNotBlank()
                        ) {
                            Text(if (editingNote == null) "Add Note" else "Update Note")
                        }

                        // Show Cancel Edit button when editing
                        if (editingNote != null) {
                            OutlinedButton(
                                onClick = {
                                    editingNote = null
                                    titleInput = ""
                                    contentInput = ""
                                    titleTouched = false
                                    contentTouched = false
                                }
                            ) {
                                Text("Cancel Edit")
                            }
                        }
                    }
                }
            }

            // Your Notes section
            Text(
                text = "Your Notes",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // LazyColumn to display notes
            LazyColumn(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(notes, key = { it.id }) { note ->
                    val isImportant = importantNotes.contains(note.id)

                    // Animated card elevation
                    val elevation by animateDpAsState(
                        targetValue = if (isImportant) 8.dp else 2.dp,
                        animationSpec = tween(300),
                        label = "elevation"
                    )

                    /*
                    2. So what animation spec is doing for us is creating a smooth and quick
                    seamless transition for when we mark our note as favorite to highlight the
                    note. We set a 300 millisecond duration so the animation doesn't happen in a
                    single frame making it look more professional and better looking
                     */

                    // Animated card color
                    val cardColor by animateColorAsState(
                        targetValue = if (isImportant)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surface,
                        animationSpec = tween(300),
                        label = "cardColor"
                    )

                    /*
                    5. So for this assignment I actually didn't use animation visability for this section.
                    I instead used a simple clickable if statement to do most of the work but we could
                    add to this if we wanted to.

                    A.Step one would be to create a variable such as
                    var expandedNoteId by remember { mutableStateOf<Int?>(null) }

                    B.Next we would add a clickable to expand and close the card itself
                    .clickable {
                     expandedNoteId = if (expandedNoteId == note.id) null else note.id
                     }

                    C. Lastly we add our animation visablity functions and columns to finish off.
                    AnimatedVisibility(
    visible = expandedNoteId == note.id,
    enter = expandVertically(animationSpec = tween(300)),
    exit = shrinkVertically(animationSpec = tween(300))
) {
    Column {
        Spacer(modifier = Modifier.height(4.dp))

        // Note content
        Text(
            text = note.content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

                    What this would do for us is turn our note into a card once clicked on
                    which would expand to show more information within our note that was hidden
                    away. The only issue though is we implement it this way it would override our
                    click to edit meaning we would have to find a workaround such as adding
                    a edit button or maybe doing a double tap to edit feature.
                     */
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Click to edit
                                editingNote = note
                                titleInput = note.title
                                contentInput = note.content
                                titleTouched = false
                                contentTouched = false
                            },
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = cardColor
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Note title
                            Text(
                                text = note.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Note content
                            Text(
                                text = note.content,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Date and action buttons row

                                // Date label
                                Text(
                                    text = "Last updated: ${note.date}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Icon buttons row
                                Row (
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    // Star/Important button
                                    IconButton(
                                        onClick = {
                                            if (isImportant) {
                                                importantNotes = importantNotes - note.id
                                            } else {
                                                importantNotes = importantNotes + note.id
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isImportant) Icons.Filled.Star else Icons.Filled.StarBorder,
                                            contentDescription = if (isImportant) "Mark as not important" else "Mark as important",
                                            tint = if (isImportant)
                                                MaterialTheme.colorScheme.secondary
                                            else
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Delete button
                                    IconButton(
                                        onClick = {
                                            showDeleteDialog = note
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = "Delete note",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


    // Delete confirmation AlertDialog
    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Note") },
            text = {
                Text("Are you sure you want to delete this note: \"${showDeleteDialog!!.title}\"?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteNote(showDeleteDialog!!)
                        scope.launch { snackbarHostState.showSnackbar("Note deleted!") }
                        // Clear editing state if deleted note was being edited
                        if (editingNote?.id == showDeleteDialog!!.id) {
                            editingNote = null
                            titleInput = ""
                            contentInput = ""
                            titleTouched = false
                            contentTouched = false
                        }
                        // Remove from important notes if it was marked
                        importantNotes = importantNotes - showDeleteDialog!!.id
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}