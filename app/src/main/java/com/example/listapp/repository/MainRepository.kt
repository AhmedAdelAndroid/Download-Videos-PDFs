package com.example.listapp.repository

import com.example.listapp.api.ApiServices
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val apiService: ApiServices
){
    suspend fun getResponseData() = apiService.getData()
}