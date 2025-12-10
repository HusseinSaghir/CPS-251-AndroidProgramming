package com.example.moviesearchingapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.moviesearchingapp.api.OmdbMovieDetailResponse


/*
5. The navigation between the movie search screen and the details screen are handled through two files
using the jetpack compose navigation such as the navController in the mainActivity file. Our movieSearch.kt
initiates our navigation and when a movie card is clicked the navController is triggered and takes us
to the detail screen. MovieDetails.kt is using the imbdId as a parameter and uses it to fetch movie details
using the ViewModel.
 */



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    navController: NavController,
    imdbId: String,
    movieViewModel: MovieViewModel = viewModel()
) {
    var movieDetails by remember { mutableStateOf<OmdbMovieDetailResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(imdbId) {
        try {
            val details = movieViewModel.getMovieDetailsById(imdbId)
            if (details != null) {
                movieDetails = details
            } else {
                error = "Movie details not found"
            }
        } catch (e: Exception) {
            error = e.message ?: "An error occurred"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movie Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = error ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            movieDetails != null -> {
                MovieDetailsContent(
                    movieDetails = movieDetails!!,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}



@Composable
fun MovieDetailsContent(movieDetails: OmdbMovieDetailResponse, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Movie Poster
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (!movieDetails.poster.isNullOrEmpty() && movieDetails.poster != "N/A") {
                    AsyncImage(
                        model = movieDetails.poster,
                        contentDescription = "Movie Poster",
                        modifier = Modifier
                            .height(400.dp)
                            .width(270.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Movie Title
                Text(
                    text = movieDetails.title ?: "Unknown Title",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Movie Details
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Year
                    DetailRow("Year", movieDetails.year ?: "N/A")

                    // Rated
                    DetailRow("Rated", movieDetails.rated ?: "N/A")

                    // Director
                    DetailRow("Director", movieDetails.director ?: "N/A")

                    // Actors
                    DetailRow("Actors", movieDetails.actors ?: "N/A")

                    // Rotten Tomatoes Rating
                    val rottenTomatoesRating = movieDetails.ratings?.find {
                        it.source?.contains("Rotten Tomatoes", ignoreCase = true) == true
                    }?.value
                    DetailRow("Rotten Tomatoes", rottenTomatoesRating ?: "N/A")

                    // IMDb Rating
                    DetailRow("IMDb Rating", movieDetails.imdbRating ?: "N/A")

                    // Box Office
                    DetailRow("Box Office", movieDetails.boxOffice ?: "N/A")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Plot
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Plot:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = movieDetails.plot ?: "No plot available",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(140.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}