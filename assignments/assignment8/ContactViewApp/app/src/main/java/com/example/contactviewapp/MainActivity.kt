package com.example.contactviewapp


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {

    private val viewModel: ContactViewModel by viewModels {
        ContactViewModel.provideFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    val contacts by viewModel.allContacts.collectAsState(initial = emptyList())
                    val searchQuery by viewModel.searchQuery.collectAsState()
                    val sortOrder by viewModel.sortOrder.collectAsState()
                    val name by viewModel.name.collectAsState()
                    val phoneNumber by viewModel.phoneNumber.collectAsState()

                    ContactScreen(
                        viewModel = viewModel,
                        contacts = contacts,
                        searchQuery = searchQuery,
                        name = name,
                        phoneNumber = phoneNumber
                    )
                }
            }
        }
    }
}

/*
4. The app we have set up currently is using and Observer pattern through the Kotlin Flows
implementations. The CollectAsState() observes the Flow from the ViewModel and converts it to Compose State.
This sets up what is called a "reactive UI" ensuring the UI will react to data changes automatically and then
our StateFlow will hold the latest values and notify all the collectors of an update.

 */