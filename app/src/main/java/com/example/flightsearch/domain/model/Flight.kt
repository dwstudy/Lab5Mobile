package com.example.flightsearch.domain.model

data class Flight(
    val departureCode: String,
    val destinationCode: String,
    val destinationName: String,
    val isFavorite: Boolean = false
)