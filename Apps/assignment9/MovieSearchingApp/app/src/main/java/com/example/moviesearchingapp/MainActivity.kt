package com.example.moviesearchingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moviesearchingapp.ui.theme.MovieSearchingAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieSearchingAppTheme {
                MaterialTheme(
                    colorScheme = lightColorScheme(

                        //Color for general UI
                        primary = Color(0xFF5B6B8C),

                        //Font Color
                        onPrimaryContainer = Color.Black,

                        //For cards
                        background = Color(0xFFF5F5F5),
                        surfaceContainerHighest = Color(0xFFE0E0E0),


                    )
                ) {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MovieSearchApp()
                }
            }
        }
    }
}


@Composable
fun MovieSearchApp() {

    val navController = rememberNavController()

    //We enter our API key here as an entry point to receive the data needed

    val apiKey = "7ea75295"
    val repository = MovieRepository(apiKey)
    val movieViewModel: MovieViewModel = viewModel(
        factory = MovieViewModel.provideFactory(repository)
    )

    NavHost(
        navController = navController,
        startDestination = "movie_search"
    ) {
        composable("movie_search") {
            MovieSearchScreen(
                navController = navController,
                movieViewModel = movieViewModel
            )
        }

        composable(
            route = "movie_details/{imdbId}",
            arguments = listOf(navArgument("imdbId") {type = NavType.StringType})
        ) { backStackEntry ->
            val imdbId = backStackEntry.arguments?.getString("imdbId") ?: ""

            MovieDetailsScreen(
                navController = navController,
                imdbId = imdbId,
                movieViewModel = movieViewModel
            )
        }
    }
}
}