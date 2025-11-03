package com.example.contactviewapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


/*
2. The purpose of Data Access Object (DAO) is to give us an interface that defines how we interact with the database
without having to manually write queries ourselves. We went over in class that this handles CRUD operations such as
Creating, Reading, Updating and Deleting information. In this instance we are using it to Create contacts, Delete them
and Query for them by searching names.
 */

@Dao
interface ContactDao {
    @Insert
    suspend fun insert(contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)

    @Query("SELECT * FROM contact_table ORDER BY name ASC")
    fun getAllContacts(): Flow<List<Contact>>

    @Query("SELECT * FROM contact_table WHERE name LIKE :searchQuery")
    fun findContacts(searchQuery: String): Flow<List<Contact>>

    @Query("SELECT * FROM contact_table ORDER BY name DESC")
    fun getContactsSortedByNameDesc(): Flow<List<Contact>>

    @Query("SELECT * FROM contact_table ORDER BY name ASC")
    fun getContactsSortedByNameAsc(): Flow<List<Contact>>
}
