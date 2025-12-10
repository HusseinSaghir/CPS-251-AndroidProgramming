package com.example.contactviewapp

import androidx.room.Entity
import androidx.room.PrimaryKey

    @Entity(tableName = "contact_table")
    data class Contact(
        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,
        val name: String,
        val phoneNumber: String
    )
/*
Here we are creating the room entity that will be used to store information in
our local SQLite database.
 */