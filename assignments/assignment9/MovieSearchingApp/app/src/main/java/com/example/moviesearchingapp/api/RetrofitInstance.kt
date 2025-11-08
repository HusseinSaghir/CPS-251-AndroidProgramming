package com.example.moviesearchingapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*
1. We use Retrofit within our project since it is a type safe http client library for Android that
will help us create a structure of API request and responses while simplifying them for us. It simplifies
things by by providing us things such as automatic JSON parsing using things like GsonConverterFactory
which will automatically convert JSON to our data classes. The other methods using HTTP raw requests
have us entering far more code manually making the whole process take longer.
 */

object RetrofitInstance {

    private const val BASE_URL = "https://www.omdbapi.com/"

    val api: OmdbApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OmdbApiService::class.java)
    }
}