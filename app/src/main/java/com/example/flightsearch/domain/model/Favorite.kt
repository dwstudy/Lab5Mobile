package com.example.flightsearch.domain.model

data class Favorite(
    val id: Int,
    val departureCode: String,
    val destinationCode: String
)