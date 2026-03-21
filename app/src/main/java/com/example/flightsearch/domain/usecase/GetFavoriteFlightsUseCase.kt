package com.example.flightsearch.domain.usecase

import com.example.flightsearch.domain.model.Flight
import com.example.flightsearch.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow

class GetFavoriteFlightsUseCase(
    private val favoriteRepository: FavoriteRepository
) {
    operator fun invoke(): Flow<List<Flight>> {
        return favoriteRepository.getAllFavoriteFlights()
    }
}