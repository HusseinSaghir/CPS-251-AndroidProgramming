package com.example.financetracker.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.financetracker.screens.AddEditTransactionScreen
import com.example.financetracker.screens.CategoryManagementScreen
import com.example.financetracker.screens.HomeScreen
import com.example.financetracker.screens.TransactionListScreen
import com.example.financetracker.viewmodel.FinanceViewModel
import com.example.financetracker.viewmodel.FinanceViewModelFactory

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object TransactionList : Screen("transactions")
    object AddTransaction : Screen("add_transaction")
    object EditTransaction : Screen("edit_transaction/{transactionId}") {
        fun createRoute(transactionId: Long) = "edit_transaction/$transactionId"
    }
    object CategoryManagement : Screen("categories")
}

@Composable
fun FinanceTrackerNavigation(factory: FinanceViewModelFactory) {
    val navController = rememberNavController()
    val financeViewModel: FinanceViewModel = viewModel(factory = factory)

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
               financeViewModel = financeViewModel
            )
        }

        composable(Screen.TransactionList.route) {
            TransactionListScreen(
                navController = navController,
                financeViewModel = financeViewModel
            )
        }

        composable(Screen.AddTransaction.route) {
            AddEditTransactionScreen(
                navController = navController,
                financeViewModel = financeViewModel,
                transactionId = null
            )
        }

        composable(
            route = Screen.EditTransaction.route,
            arguments = listOf(navArgument("transactionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getLong("transactionId")
            AddEditTransactionScreen(
                navController = navController,
                financeViewModel = financeViewModel,
                transactionId = transactionId
            )
        }

        composable(Screen.CategoryManagement.route) {
            CategoryManagementScreen(
                navController = navController,
                transactionViewModel = financeViewModel,
                categoryViewModel = financeViewModel
            )
        }
    }
}