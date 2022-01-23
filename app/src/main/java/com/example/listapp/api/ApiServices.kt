package com.example.listapp.api

import com.example.listapp.models.DataResponseItem
import retrofit2.Response
import retrofit2.http.GET

interface ApiServices {
    @GET("getListOfFilesResponse.json")
    suspend fun getData(): Response<List<DataResponseItem>>
}