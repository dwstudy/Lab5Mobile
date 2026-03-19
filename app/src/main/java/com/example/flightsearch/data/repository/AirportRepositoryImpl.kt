package com.example.flightsearch.data.repository

import com.example.flightsearch.data.local.dao.AirportDao
import com.example.flightsearch.data.local.dao.FavoriteDao
import com.example.flightsearch.data.local.entity.AirportEntity
import com.example.flightsearch.domain.model.Airport
import com.example.flightsearch.domain.model.Flight
import com.example.flightsearch.domain.repository.AirportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class AirportRepositoryImpl(
    private val airportDao: AirportDao,
    private val favoriteDao: FavoriteDao
) : AirportRepository {

    override fun searchAirports(query: String): Flow<List<Airport>> {
        return airportDao.searchAirports(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getAirportByCode(iataCode: String): Airport? {
        return airportDao.getAirportByCode(iataCode)?.toDomain()
    }

    override fun getFlightsFromAirport(departureCode: String): Flow<List<Flight>> {
        return combine(
            airportDao.getAllAirports(),
            favoriteDao.getFavoritesByDeparture(departureCode)
        ) { airports, favorites ->
            val favoriteSet = favorites.map { it.destination_code }.toSet()
            airports.filter { it.iata_code != departureCode }
                .map { airport ->
                    Flight(
                        departureCode = departureCode,
                        destinationCode = airport.iata_code,
                        destinationName = airport.name,
                        isFavorite = favoriteSet.contains(airport.iata_code)
                    )
                }
        }
    }

    private fun AirportEntity.toDomain(): Airport {
        return Airport(
            id = id,
            iataCode = iata_code,
            name = name,
            passengers = passengers
        )
    }
}