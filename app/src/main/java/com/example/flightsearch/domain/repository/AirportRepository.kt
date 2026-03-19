package com.example.flightsearch.domain.repository

import com.example.flightsearch.domain.model.Airport
import com.example.flightsearch.domain.model.Flight
import kotlinx.coroutines.flow.Flow

interface AirportRepository {
    fun searchAirports(query: String): Flow<List<Airport>>
    suspend fun getAirportByCode(iataCode: String): Airport?
    fun getFlightsFromAirport(departureCode: String): Flow<List<Flight>>
}