package com.example.financetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financetracker.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FinanceViewModel(
    private val repository: FinanceRepository
) : ViewModel() {

    // Transaction-related stuff
    val allTransactions: Flow<List<Transaction>>
    private val _filteredTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val filteredTransactions: StateFlow<List<Transaction>> = _filteredTransactions.asStateFlow()

    private val _currentBalance = MutableStateFlow(0.0)
    val currentBalance: StateFlow<Double> = _currentBalance.asStateFlow()

    private val _totalIncome = MutableStateFlow(0.0)
    val totalIncome: StateFlow<Double> = _totalIncome.asStateFlow()

    private val _totalExpense = MutableStateFlow(0.0)
    val totalExpense: StateFlow<Double> = _totalExpense.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<Long?>(null)
    val selectedCategoryFilter: StateFlow<Long?> = _selectedCategoryFilter.asStateFlow()

    // Category-related stuff
    val allCategories: Flow<List<Category>>

    init {
        allTransactions = repository.getAllTransactions()
        allCategories = repository.getAllCategories()

        viewModelScope.launch {
            allTransactions.collect { transactions ->
                updateFinancialSummary(transactions)
                if (_selectedCategoryFilter.value == null) {
                    _filteredTransactions.value = transactions
                }
            }
        }
    }

    private fun updateFinancialSummary(transactions: List<Transaction>) {
        val income = transactions.filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }
        val expense = transactions.filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

        _totalIncome.value = income
        _totalExpense.value = expense
        _currentBalance.value = income - expense
    }

    // Transaction methods
    fun filterByCategory(categoryId: Long?) {
        _selectedCategoryFilter.value = categoryId
        viewModelScope.launch {
            if (categoryId == null) {
                allTransactions.collect { _filteredTransactions.value = it }
            } else {
                repository.getTransactionsByCategory(categoryId).collect {
                    _filteredTransactions.value = it
                }
            }
        }
    }

    suspend fun getTransactionById(id: Long): Transaction? {
        return repository.getTransactionById(id)
    }

    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.insertTransaction(transaction)
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun getRecentTransactions(count: Int): Flow<List<Transaction>> {
        return allTransactions.map { it.take(count) }
    }

    fun insertCategory(category: Category) {
        viewModelScope.launch {
            repository.insertCategory(category)
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            repository.updateCategory(category)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }
}