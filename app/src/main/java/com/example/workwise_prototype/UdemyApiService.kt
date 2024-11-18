// UdemyApiService.kt
package com.example.workwise_prototype

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

interface UdemyApiService {
    @Headers(
        "x-rapidapi-host: udemy-course-scrapper-api.p.rapidapi.com",
        "x-rapidapi-key: f199ce1593msh99bac33b11429b2p1aa224jsn9a5ae70b5581"
    )
    @GET("course-names/course-instructor/course-url")
    fun getCourses(): Call<Map<String, Map<String, String>>>
}
