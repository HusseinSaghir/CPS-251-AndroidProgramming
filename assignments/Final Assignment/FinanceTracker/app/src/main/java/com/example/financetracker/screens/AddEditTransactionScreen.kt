package com.example.financetracker.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import androidx.navigation.NavController
import com.example.financetracker.data.Transaction
import com.example.financetracker.data.TransactionType
import com.example.financetracker.viewmodel.FinanceViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionScreen(
    navController: NavController,
    financeViewModel: FinanceViewModel,
    transactionId: Long?
) {
    val scope = rememberCoroutineScope()

    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }

    var showDatePicker by remember { mutableStateOf(false) }
    var categoryError by remember { mutableStateOf(false) }

    val categories by financeViewModel.allCategories.collectAsState(initial = emptyList())
    val filteredCategories = categories.filter { it.type == selectedType }

    val isEditMode = transactionId != null

    LaunchedEffect(transactionId) {
        if (transactionId != null) {
            val transaction = financeViewModel.getTransactionById(transactionId)
            transaction?.let {
                selectedType = it.type
                selectedCategoryId = it.categoryId
                description = it.description
                amount = it.amount.toString()
                selectedDate.timeInMillis = it.date
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Transaction" else "Add Transaction") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (selectedCategoryId == null) {
                                categoryError = true
                                return@TextButton
                            }

                            val amountValue = amount.toDoubleOrNull() ?: 0.0
                            if (amountValue <= 0) return@TextButton

                            val transaction = Transaction(
                                id = transactionId ?: 0,
                                amount = amountValue,
                                date = selectedDate.timeInMillis,
                                description = description.ifBlank { "Untitled" },
                                categoryId = selectedCategoryId!!,
                                type = selectedType
                            )

                            scope.launch {
                                if (isEditMode) {
                                    financeViewModel.updateTransaction(transaction)
                                } else {
                                    financeViewModel.insertTransaction(transaction)
                                }
                                navController.popBackStack()
                            }
                        }
                    ) {
                        Text("Save")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Type Section
            Text(
                text = "Type",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedType == TransactionType.EXPENSE,
                    onClick = {
                        selectedType = TransactionType.EXPENSE
                        selectedCategoryId = null
                    },
                    label = { Text("Expense") },
                    modifier = Modifier.weight(1f),
                    border = if (selectedType == TransactionType.EXPENSE)
                        FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = true,
                            borderColor = MaterialTheme.colorScheme.primary,
                            selectedBorderColor = MaterialTheme.colorScheme.primary,
                            borderWidth = 2.dp,
                            selectedBorderWidth = 2.dp
                        )
                    else null
                )
                FilterChip(
                    selected = selectedType == TransactionType.INCOME,
                    onClick = {
                        selectedType = TransactionType.INCOME
                        selectedCategoryId = null
                    },
                    label = { Text("Income") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Category Section
            Text(
                text = "Category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (filteredCategories.isEmpty()) {
                Text(
                    text = "No categories available for ${if (selectedType == TransactionType.EXPENSE) "Expense" else "Income"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filteredCategories.take(4).forEach { category ->
                        FilterChip(
                            selected = selectedCategoryId == category.id,
                            onClick = {
                                selectedCategoryId = category.id
                                categoryError = false
                            },
                            label = { Text(category.name) },
                            border = if (selectedCategoryId == category.id)
                                FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = true,
                                    borderColor = MaterialTheme.colorScheme.primary,
                                    selectedBorderColor = MaterialTheme.colorScheme.primary,
                                    borderWidth = 2.dp,
                                    selectedBorderWidth = 2.dp
                                )
                            else null
                        )
                    }
                }
            }

            if (categoryError) {
                Text(
                    text = "Please select a category",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Description Field
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            // Amount Field
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth()
            )

            // Date Section
            Text(
                text = "Date",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showDatePicker = true },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(selectedDate.time),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Icon(Icons.Default.Edit, contentDescription = "Change date")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = {
                    if (selectedCategoryId == null) {
                        categoryError = true
                        return@Button
                    }

                    val amountValue = amount.toDoubleOrNull() ?: 0.0
                    if (amountValue <= 0) return@Button

                    val transaction = Transaction(
                        id = transactionId ?: 0,
                        amount = amountValue,
                        date = selectedDate.timeInMillis,
                        description = description.ifBlank { "Untitled" },
                        categoryId = selectedCategoryId!!,
                        type = selectedType
                    )

                    scope.launch {
                        if (isEditMode) {
                            financeViewModel.updateTransaction(transaction)
                        } else {
                            financeViewModel.insertTransaction(transaction)
                        }
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Transaction")
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.timeInMillis
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val calendar = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
                            calendar.timeInMillis = millis
                            selectedDate.set(
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            )
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}