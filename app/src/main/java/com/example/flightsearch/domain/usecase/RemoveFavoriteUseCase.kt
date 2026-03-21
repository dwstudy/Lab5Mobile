package com.example.flightsearch.domain.usecase

import com.example.flightsearch.domain.repository.FavoriteRepository

class RemoveFavoriteUseCase(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(departureCode: String, destinationCode: String) {
        favoriteRepository.removeFavorite(departureCode, destinationCode)
    }
}