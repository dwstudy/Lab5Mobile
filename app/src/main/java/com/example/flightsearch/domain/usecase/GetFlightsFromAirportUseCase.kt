package com.example.flightsearch.domain.usecase

import com.example.flightsearch.domain.model.Flight
import com.example.flightsearch.domain.repository.AirportRepository
import kotlinx.coroutines.flow.Flow

class GetFlightsFromAirportUseCase(
    private val airportRepository: AirportRepository
) {
    operator fun invoke(departureCode: String): Flow<List<Flight>> {
        return airportRepository.getFlightsFromAirport(departureCode)
    }
}