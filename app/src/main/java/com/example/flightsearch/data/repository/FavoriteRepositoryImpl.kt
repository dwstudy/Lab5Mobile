package com.example.flightsearch.data.repository

import com.example.flightsearch.data.local.dao.AirportDao
import com.example.flightsearch.data.local.dao.FavoriteDao
import com.example.flightsearch.data.local.entity.FavoriteEntity
import com.example.flightsearch.domain.model.Flight
import com.example.flightsearch.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class FavoriteRepositoryImpl(
    private val favoriteDao: FavoriteDao,
    private val airportDao: AirportDao
) : FavoriteRepository {

    override fun getAllFavoriteFlights(): Flow<List<Flight>> {
        return combine(
            favoriteDao.getAllFavorites(),
            airportDao.getAllAirports()
        ) { favorites, airports ->
            val airportMap = airports.associateBy { it.iata_code }
            favorites.map { favorite ->
                val destinationAirport = airportMap[favorite.destination_code]
                Flight(
                    departureCode = favorite.departure_code,
                    destinationCode = favorite.destination_code,
                    destinationName = destinationAirport?.name ?: "Unknown",
                    isFavorite = true
                )
            }
        }
    }

    override suspend fun addFavorite(departureCode: String, destinationCode: String) {
        favoriteDao.insertFavorite(
            FavoriteEntity(
                departure_code = departureCode,
                destination_code = destinationCode
            )
        )
    }

    override suspend fun removeFavorite(departureCode: String, destinationCode: String) {
        favoriteDao.deleteFavoriteByCodes(departureCode, destinationCode)
    }

    override suspend fun isFavorite(departureCode: String, destinationCode: String): Boolean {
        return favoriteDao.isFavorite(departureCode, destinationCode)
    }
}