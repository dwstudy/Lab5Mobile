package com.example.flightsearch.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.flightsearch.data.local.entity.AirportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {
    @Query("SELECT * FROM airport WHERE iata_code LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%' LIMIT 20")
    fun searchAirports(query: String): Flow<List<AirportEntity>>

    @Query("SELECT * FROM airport WHERE iata_code = :iataCode")
    suspend fun getAirportByCode(iataCode: String): AirportEntity?

    @Query("SELECT * FROM airport")
    fun getAllAirports(): Flow<List<AirportEntity>>
}