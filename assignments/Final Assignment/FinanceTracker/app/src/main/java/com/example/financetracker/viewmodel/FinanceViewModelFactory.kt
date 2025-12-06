package com.example.financetracker.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.financetracker.data.FinanceDatabase
import com.example.financetracker.data.FinanceRepository

class FinanceViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinanceViewModel::class.java)) {

            // Get the database instance using your FinanceDatabase class
            val database = FinanceDatabase.getDatabase(application)

            // Get the DAOs from the database
            val transactionDao = database.transactionDao()
            val categoryDao = database.categoryDao()

            // Create repository with both DAOs
            val repository = FinanceRepository(transactionDao, categoryDao)

            @Suppress("UNCHECKED_CAST")
            return FinanceViewModel(repository = repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}