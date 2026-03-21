package com.example.flightsearch.domain.usecase

import com.example.flightsearch.domain.model.Airport
import com.example.flightsearch.domain.repository.AirportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SearchAirportsUseCase(
    private val airportRepository: AirportRepository
) {
    operator fun invoke(query: String): Flow<List<Airport>> {
        return if (query.length >= 2) {
            try {
                airportRepository.searchAirports(query)
            } catch (e: Exception) {
                e.printStackTrace()
                flowOf(emptyList())
            }
        } else {
            flowOf(emptyList())
        }
    }
}