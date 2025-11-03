package com.example.contactviewapp

import kotlinx.coroutines.flow.Flow

class ContactRepository (private val contactDao: ContactDao) {

    fun findContacts(searchQuery: String): Flow<List<Contact>> {
        return contactDao.findContacts(searchQuery)
    }

    fun getContactsSortedByNameDesc(): Flow<List<Contact>> {
        return contactDao.getContactsSortedByNameDesc()
    }

    fun getContactsSortedByNameAsc(): Flow<List<Contact>> {
        return contactDao.getContactsSortedByNameAsc()
    }

    suspend fun insert(contact: Contact) {
        contactDao.insert(contact)
    }

    suspend fun delete(contact: Contact) {
        contactDao.delete(contact)
    }
}

/*
5. So the overall architecture of our applications looks something like this.

We have our UI layer
which has our composable functions that will display data and capture any user inputs. It is
stateless and only sees the ViewModel file.

Then we have our ViewModel layer
which actually manages the UI state and data streams such as user events. This file acts as a
communication or link between all the other files. It does this because it's sitting between the UI and
data layers meaning everything will flow through it.

We have our ContactRepository layer
which is keeping track of what data is getting passed through to ensure we
get the information we need. Sort of acts like a middleman that makes isolating data a lot more simpler.
It is read by the ViewModel so that means we would change this layer to alter the data received.

Lastly we have our Database Layer
which is our ContactDAO and ContactDatabase files, they as databases that handles data persistence
with SQLite. The DAO is defining our operations, providing instances and is the entity that defines the schema.

The main benefits we gain by setting up the structure like this is it assists with things like testability,
maintainability, scalability, and many other reasons. It's always good to be organized and efficient!
 */