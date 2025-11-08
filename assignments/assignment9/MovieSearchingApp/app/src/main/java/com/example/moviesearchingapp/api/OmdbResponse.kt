package com.example.moviesearchingapp.api

import com.google.gson.annotations.SerializedName

/*
2. This is our data class and it's really cool because it serves as our model that represents
the structure of our JSON responses from the OMDB API we set up. This is allowing us to use annotations
like the @SerializedName to map JSON field names to kotlin properties. An example would be
our "Title" in our "OmdbMovieDetailResponse", we map that to a kotlin property like
"movieDetail.title" and this receive a String from our API for specific information set.

So to run through the function

1. We send a Query to the API
2. We then get a result from our API
3. What ever items we have in place will handle that response
 */


//This will be the response for searching a movie. Below it will give us the results of that response.

// Response for search endpoint
data class OmdbResponse(
    @SerializedName("Search") val search: List<MovieSearchItem>?,
    @SerializedName("totalResults") val totalResults: String?,
    @SerializedName("Response") val response: String?,
    @SerializedName("Error") val error: String?
)

// Individual movie item in search results
data class MovieSearchItem(
    @SerializedName("Title") val title: String?,
    @SerializedName("Year") val year: String?,
    @SerializedName("imdbID") val imdbID: String?,
    @SerializedName("Type") val type: String?,
    @SerializedName("Poster") val poster: String?
)

// Response for detail endpoint
data class OmdbMovieDetailResponse(
    @SerializedName("Title") val title: String?,
    @SerializedName("Year") val year: String?,
    @SerializedName("Rated") val rated: String?,
    @SerializedName("Director") val director: String?,
    @SerializedName("Actors") val actors: String?,
    @SerializedName("Plot") val plot: String?,
    @SerializedName("Poster") val poster: String?,
    @SerializedName("Ratings") val ratings: List<Rating>?,
    @SerializedName("imdbRating") val imdbRating: String?,
    @SerializedName("BoxOffice") val boxOffice: String?,
    @SerializedName("Response") val response: String?,
    @SerializedName("Error") val error: String?
)

data class Rating(
    @SerializedName("Source") val source: String?,
    @SerializedName("Value") val value: String?
)


/*

Different than JSON vvv

{
  "name": "John Smith",
  "age": 30,
  "isStudent": false,
  "hobbies": ["reading", "gaming", "cooking"],
  "address": {
    "street": "123 Main St",
    "city": "New York"
  }
}
 */