package com.example.contactlist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ContactListApp()
                }
            }
        }
    }
}




@Composable
fun ContactListApp() {

    val contacts = listOf(
        Contact("Hugh Jarms", "huge.arms@example.com", "555-0101"),
        Contact("Anita Bath", "need.a.bath@example.com", "555-0102"),
        Contact("Mike Rotch", "itchy.mike@example.com", "555-0103"),
        Contact("Ben Dover", "bend.over@example.com", "555-0104"),
        Contact("Phil McCracken", "crack.inspector@example.com", "555-0105"),
        Contact("Al Kaholic", "beer.me@example.com", "555-0106"),
        Contact("Ivana Tinkle", "restroom.emergency@example.com", "555-0107"),
        Contact("Hugh Mungus", "huge.and.mighty@example.com", "555-0108"),
        Contact("Amanda Hugginkiss", "need.a.hug@example.com", "555-0109"),
        Contact("Heywood Jablome", "wood.you.mind@example.com", "555-0110"),
        Contact("Eileen Dover", "lean.over@example.com", "555-0111"),
        Contact("Dee Snuts", "peanut.gallery@example.com", "555-0112"),
        Contact("Moe Money", "moe.problems@example.com", "555-0113"),
        Contact("Seymour Butts", "butts.seen@example.com", "555-0114"),
        Contact("Oliver Clothesoff", "stripper.dancer@example.com", "555-0115"),
        Contact("Marty Graw", "party.time@example.com", "555-0116"),
        Contact("Carrie Oakey", "tree.hugger@example.com", "555-0117"),
        Contact("Sal Ami", "french.meat@example.com", "555-0118"),
        Contact("Chris P. Bacon", "pork.belly@example.com", "555-0119"),
        Contact("Anna Conda", "snake.charmer@example.com", "555-0120"),
        Contact("Barry D'Hatchet", "timber.manager@example.com", "555-0121"),
        Contact("Dinah Mite", "dynamic.personality@example.com", "555-0122"),
        Contact("Ella Vader", "dark.side@example.com", "555-0123"),
        Contact("Faye Slift", "quick.getaway@example.com", "555-0124"),
        Contact("Gene Poole", "swimming.hole@example.com", "555-0125")
    )

    ContactList(contacts = contacts)
}

@Composable
fun ContactList(contacts: List<Contact>) {

    /*
    2. So we want to use remember saveable instead of basic remember so we can remember
    what contacts were selected if we rotate our screen instead of having to re-enter all the information.

     */
    var selectedContact by rememberSaveable { mutableStateOf<Contact?>(null) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 50.dp)
    ) {

        Text(
            text = "Contact List",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

       Text(
           text = if(selectedContact != null) {
               "Selected: ${selectedContact}"
           } else {
               "No contact has been selected"
           },

           style = MaterialTheme.typography.bodyLarge,
           modifier = Modifier.padding(bottom = 24.dp)
       )

/*
1. We want to implement Lazy Column over a normal one for plenty of reasons such as to
make sure our screen can only have as many contacts as however big it is and a few extra contacts
ahead to make scrolling feel smoother and not have everything pop in. Ultimately tho the main
benefits come from the performance benefits. It's impractical to use a regular column
because it would load every single element for all contacts including the ones we don't see, which
is fine if we have 25 contacts but say we have something like 1000 with images and all that info
then we might potentially end up slowing down the app because it's taking up too much memory
trying to create all those elements and may even just crash the app.
 */
LazyColumn (
    verticalArrangement = Arrangement.spacedBy(8.dp),
    modifier = Modifier.weight(1f)
){
    items(contacts) {contact ->
        ContactItem(
        contact = contact,
            isSelected = contact == selectedContact,
            onClick = {
                selectedContact = contact
            }
        )
    }
}

        /*
        4. If we wanted to implement multi-selection and other bulk actions then we would want
        to add another button for something like a select and deselect all contacts button
        that we would hang in the upper right and then we could add more functions to our already
        existing button such as a delete button or maybe even and add contact button.

        EX.
        Button(
            onClick = { Delete selected contacts },
            modifier = Modifier.weight(1f)
        ) {
            Text("Delete (${selectedContacts.size})")
        }
         */


        if(selectedContact != null) {
            Button(
                onClick = { selectedContact = null },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Clear Selection")
            }
        }

    }
}

@Composable
fun ContactItem(
    contact: Contact,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        )
    ) {

        /*
        3. The order of sequence matters in how we decide we want the presentation to be.
        If we remove the padding then you can see how misaligned and weird everything looks
        with some parts even cutting off. And once we put padding on top of background you
        will see that now the padding surrounds the contact card instead of extending
        the whole box fully.

        (For example, remove padding, then place it on top of background)
         */
        Row(
            modifier = Modifier
                .fillMaxWidth()

                .background(
                    if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {


            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.secondary
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.name.split(" ").map { it.first() }.joinToString(""),
                    color = Color.White,
                    fontSize = 16.sp
                )
            }


            Spacer(modifier = Modifier.width(16.dp))

            Column (
                modifier = Modifier.weight(1f)
            ) {
               Text(
                   text = contact.name,
                   style = MaterialTheme.typography.titleMedium,
                   color = if (isSelected) {
                       MaterialTheme.colorScheme.onPrimaryContainer
                   } else {
                       MaterialTheme.colorScheme.onSurface
                   }
               )

                Text(
                    text = contact.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                Text(
                    text = contact.phone,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }


if (isSelected) {
    Icon(
        imageVector = Icons.Default.Check,
        contentDescription = "Selected",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(24.dp)
    )
}
        }
    }
}


data class Contact(
    val name: String,
    val email: String,
    val phone: String
)


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ContactListAppPreview() {

        ContactListApp()

}

