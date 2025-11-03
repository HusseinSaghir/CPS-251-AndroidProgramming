package com.example.contactviewapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(
    viewModel: ContactViewModel,
    contacts: List<Contact>,
    searchQuery: String,
    name: String,
    phoneNumber: String
) {
    var nameError by remember { mutableStateOf(false) }
    var phoneError by remember {mutableStateOf(false)}
    var showDeleteDialog by remember { mutableStateOf(false) }
    var contactToDelete by remember { mutableStateOf<Contact?>(null) }


    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        OutlinedTextField(
            value = name,
            onValueChange = {
                viewModel.onNameChange(it)
                nameError = false
            },
            label = { Text("Name")},
            isError = nameError,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorLabelColor = MaterialTheme.colorScheme.error
            )
        )

      if (nameError) {
          Text (
              text = "Name cannot be empty",
              color = MaterialTheme.colorScheme.error,
              fontSize = 12.sp,
              modifier = Modifier.padding(start = 16.dp, top = 4.dp)
          )
       }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                viewModel.onPhoneNumberChange(it)
                phoneError = false
            },
            label = { Text("Phone Number (10 digits)") },
            isError = phoneError,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorLabelColor = MaterialTheme.colorScheme.error
            )
        )

        if(phoneError) {
            Text(
                text = "Invalid phone number format should be like\n999.999.9999",
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier.padding(start =16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(54.dp, Alignment.CenterHorizontally)

        ) {
            Button(
                onClick = {
                    nameError = name.isBlank()
                    phoneError = !viewModel.isValidPhoneNumber(phoneNumber)

                    if(!nameError && !phoneError) {
                        viewModel.insert(Contact(name = name, phoneNumber = phoneNumber))
                        viewModel.clearInputFields()

                    }
                },

            ) {
                Text("Add")
            }

            Button(
                onClick = {
                    viewModel.onSortOrderChange(SortOrder.DESC)
                },

            ) {
                Text("Sort Desc")
            }

            Button(
                onClick = {
                    viewModel.onSortOrderChange(SortOrder.ASC)
                },

            ) {
                Text("Sort Asc")
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            label = { Text("Search Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Contacts:",
            fontSize = 40.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn (
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(contacts) { contact ->
                ContactItem(
                    contact = contact,
                    onDelete = {
                        contactToDelete = contact
                        showDeleteDialog = true
                    }
                )
            }
        }
    }



    if(showDeleteDialog && contactToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Contact") },
            text = { Text("Are you sure you want to delete this\ncontact?")},
            confirmButton = {
                TextButton(
                    onClick = {
                        contactToDelete?.let { viewModel.delete(it) }
                        showDeleteDialog = false
                        contactToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        contactToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ContactItem(
    contact: Contact,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = contact.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = contact.phoneNumber,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Contact"
            )
        }
    }
}

