package com.example.workwise_prototype

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://udemy-course-scrapper-api.p.rapidapi.com"
    private const val API_KEY = "f199ce1593msh99bac33b11429b2p1aa224jsn9a5ae70b5581"

    // Create OkHttpClient with logging interceptor and API key
    private val client = OkHttpClient.Builder().apply {
        // Add logging for debugging
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        addInterceptor(logging)

        // Add interceptor to pass the API key in the headers
        addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("x-rapidapi-host", "udemy-course-scrapper-api.p.rapidapi.com")
                .addHeader("x-rapidapi-key", API_KEY)
                .build()
            chain.proceed(request)
        }
    }.build()

    // Initialize Retrofit instance
    val instance: UdemyApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(UdemyApiService::class.java)
    }
}
