package com.example.flightsearch.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flightsearch.data.datasource.DataStoreManager
import com.example.flightsearch.data.local.database.FlightDatabase
import com.example.flightsearch.data.repository.AirportRepositoryImpl
import com.example.flightsearch.data.repository.FavoriteRepositoryImpl
import com.example.flightsearch.domain.usecase.*

class MainViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(
                searchAirportsUseCase = SearchAirportsUseCase(airportRepository),
                getFlightsFromAirportUseCase = GetFlightsFromAirportUseCase(airportRepository),
                getFavoriteFlightsUseCase = GetFavoriteFlightsUseCase(favoriteRepository),
                addFavoriteUseCase = AddFavoriteUseCase(favoriteRepository),
                removeFavoriteUseCase = RemoveFavoriteUseCase(favoriteRepository),
                dataStoreManager = DataStoreManager(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        private lateinit var database: FlightDatabase
        private lateinit var airportRepository: AirportRepositoryImpl
        private lateinit var favoriteRepository: FavoriteRepositoryImpl

        fun initialize(context: Context) {
            try {
                database = FlightDatabase.getDatabase(context)
                airportRepository = AirportRepositoryImpl(database.airportDao(), database.favoriteDao())
                favoriteRepository = FavoriteRepositoryImpl(database.favoriteDao(), database.airportDao())
                println("MainViewModelFactory initialized successfully")
            } catch (e: Exception) {
                println("Error initializing MainViewModelFactory: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}