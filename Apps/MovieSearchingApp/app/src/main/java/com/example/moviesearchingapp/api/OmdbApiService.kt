package com.example.moviesearchingapp.api

import retrofit2.http.GET
import retrofit2.http.Query

interface OmdbApiService {

    @GET("/")
    suspend fun searchMovies(
        @Query("s") searchQuery: String,
        @Query("apikey") apikey: String
    ): OmdbResponse

    @GET("/")
    suspend fun getMovieDetails(
        @Query("i") imdbId: String,
        @Query("apikey") apikey: String
    ): OmdbMovieDetailResponse
}