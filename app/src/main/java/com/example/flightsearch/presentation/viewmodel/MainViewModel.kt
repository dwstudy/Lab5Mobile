package com.example.flightsearch.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flightsearch.data.datasource.DataStoreManager
import com.example.flightsearch.domain.model.Airport
import com.example.flightsearch.domain.model.Flight
import com.example.flightsearch.domain.usecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import android.util.Log

class MainViewModel(
    private val searchAirportsUseCase: SearchAirportsUseCase,
    private val getFlightsFromAirportUseCase: GetFlightsFromAirportUseCase,
    private val getFavoriteFlightsUseCase: GetFavoriteFlightsUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedAirport = MutableStateFlow<Airport?>(null)
    val selectedAirport: StateFlow<Airport?> = _selectedAirport.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Airport>>(emptyList())
    val searchResults: StateFlow<List<Airport>> = _searchResults.asStateFlow()

    private val _flights = MutableStateFlow<List<Flight>>(emptyList())
    val flights: StateFlow<List<Flight>> = _flights.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isShowingFavorites = MutableStateFlow(false)
    val isShowingFavorites: StateFlow<Boolean> = _isShowingFavorites.asStateFlow()

    init {
        loadLastSearchedAirport()

        viewModelScope.launch {
            searchQuery
                .debounce(300)
                .filter { it.length >= 2 }
                .collect { query ->
                    try {
                        Log.d("Search", "New query: '$query'")
                        val airports = searchAirportsUseCase(query).first()
                        Log.d("Search", "Found ${airports.size} airports")
                        _searchResults.value = airports
                    } catch (e: Exception) {
                        Log.e("Search", "Error: ${e.message}", e)
                        _error.value = "Search error: ${e.message}"
                    }
                }
        }

        viewModelScope.launch {
            selectedAirport.collect { airport ->
                if (airport != null) {
                    dataStoreManager.saveLastSearchedAirport(airport.iataCode)
                    val flightsList = getFlightsFromAirportUseCase(airport.iataCode).first()
                    _flights.value = flightsList
                    _isShowingFavorites.value = false
                }
            }
        }

        viewModelScope.launch {
            getFavoriteFlightsUseCase().collect { favorites ->
                if (_isShowingFavorites.value) {
                    _flights.value = favorites
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isEmpty()) {
            _searchResults.value = emptyList()
            showFavorites()
        }
    }

    fun selectAirport(airport: Airport) {
        _selectedAirport.value = airport
        _searchQuery.value = airport.iataCode
        _searchResults.value = emptyList()
    }

    fun toggleFavorite(flight: Flight) {
        viewModelScope.launch {
            if (flight.isFavorite) {
                removeFavoriteUseCase(flight.departureCode, flight.destinationCode)
            } else {
                addFavoriteUseCase(flight.departureCode, flight.destinationCode)
            }
        }
    }

    fun showFavorites() {
        _isShowingFavorites.value = true
        _selectedAirport.value = null
        viewModelScope.launch {
            val favorites = getFavoriteFlightsUseCase().first()
            _flights.value = favorites
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _selectedAirport.value = null
        _searchResults.value = emptyList()
        showFavorites()
        viewModelScope.launch {
            dataStoreManager.clearLastSearchedAirport()
        }
    }

    private fun loadLastSearchedAirport() {
        viewModelScope.launch {
            val iataCode = dataStoreManager.getLastSearchedAirport().first()
            if (!iataCode.isNullOrEmpty()) {
                _searchQuery.value = iataCode
            } else {
                showFavorites()
            }
        }
    }
}