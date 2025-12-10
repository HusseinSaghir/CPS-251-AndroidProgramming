package com.example.contactviewapp

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow //<---- for #1
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine //<-- for #1
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.flatMapLatest //<-- for #1

class ContactViewModel(private val repository: ContactRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.ASC)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()



    @OptIn(ExperimentalCoroutinesApi::class)
    val allContacts: Flow<List<Contact>> = combine(
        _searchQuery,
        _sortOrder
    ) { query, sortOrder ->
        when (sortOrder) {
            SortOrder.ASC -> {
                if (query.isBlank()) {
                    repository.getContactsSortedByNameAsc()
                } else {
                    repository.findContacts("%${query}%")
                }
            }
            SortOrder.DESC -> {
                if (query.isBlank()) {
                    repository.getContactsSortedByNameDesc()
                } else {
                    repository.findContacts("%${query}%")
                }
            }
        }
    }.flatMapLatest  { it }

    /*
    1. After doing some research we can find out that the list updates as we type letters because
    we are implementing the use of Kotlin Flows with the combine operator. The SearchQuery StateFlow gives a new value
    each time a user inputs a new character. Our combine operator uses both searchQuery and sortOrder to trigger a
    new database query. The flatMapLatest operator switches to the latest Flow, automatically canceling previous queries.
    This creates a reactive pipeline where the
     UI changes (typing) → StateFlow emission → database query → UI update, all happening automatically
     */

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onSortOrderChange(sortOrder: SortOrder) {
        _sortOrder.value = sortOrder
    }

    fun insert(contact: Contact) = viewModelScope.launch {
        repository.insert(contact)
    }

    fun delete(contact: Contact) = viewModelScope.launch {
        repository.delete(contact)
    }

    fun onNameChange(newName: String) {
        _name.value = newName
    }

    fun onPhoneNumberChange(newPhoneNumber: String) {
        _phoneNumber.value = newPhoneNumber
    }

    fun clearInputFields() {
        _name.value = ""
        _phoneNumber.value = ""
    }

    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        return phoneNumber.matches("^[0-9]{3}\\.[0-9]{3}\\.[0-9]{4}$".toRegex())
    }

    companion object {
        fun provideFactory(
            application: Application,
        ): ViewModelProvider.Factory {
            return ContactViewModelFactory(ContactRepository(ContactDatabase.getDatabase(application).contactDao()))
        }
    }
}

class ContactViewModelFactory(private val repository: ContactRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

enum class SortOrder {
    ASC,
    DESC
}

/*
3. The primary responsibilities of our ContactViewModel is to manage our UI state changes such as the
searching queries or handling user actions such as creating, deleting and etc. It also allows our data to
remain after recompilation changes such as rotating our screen. Its effects on the Android Lifecycle is that
it solves a problem where activities and composable are destroyed and recreated during a config change while
also preventing data loss.
 */
