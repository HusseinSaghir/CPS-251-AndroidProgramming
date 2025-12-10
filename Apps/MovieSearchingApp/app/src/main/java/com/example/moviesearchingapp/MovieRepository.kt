package com.example.moviesearchingapp

import com.example.moviesearchingapp.api.OmdbMovieDetailResponse
import com.example.moviesearchingapp.api.OmdbResponse
import com.example.moviesearchingapp.api.RetrofitInstance

/*
3. A Movie Repository is required within our structure because it abstracts our data layer
for the rest of our application. What this does for us is handling all the data fetching logic
we actually use for our project such as the API calls. It also handles error wrappings and we could
actually add multiple API data sources here and manage them all, but that is for larger apps usually.

The obvious benefits of this is our favorite word which is scalability. This helps with separation,
testing, and error handling which is all incredibly useful.
 */

class MovieRepository(private val apiKey: String) {

    suspend fun searchMovies(query: String): Result<OmdbResponse> {
        return try {

            val response = RetrofitInstance.api.searchMovies(query, apiKey)
            Result.success(response)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMovieDetails(imdbId: String): Result<OmdbMovieDetailResponse> {
        return try {
            val response = RetrofitInstance.api.getMovieDetails(imdbId, apiKey)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}