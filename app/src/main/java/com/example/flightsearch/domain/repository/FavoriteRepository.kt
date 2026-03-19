package com.example.flightsearch.domain.repository

import com.example.flightsearch.domain.model.Flight
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getAllFavoriteFlights(): Flow<List<Flight>>
    suspend fun addFavorite(departureCode: String, destinationCode: String)
    suspend fun removeFavorite(departureCode: String, destinationCode: String)
    suspend fun isFavorite(departureCode: String, destinationCode: String): Boolean
}