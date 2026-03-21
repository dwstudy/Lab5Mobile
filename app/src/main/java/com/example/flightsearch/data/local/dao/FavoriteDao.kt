package com.example.flightsearch.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Query
import com.example.flightsearch.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT * FROM favorite WHERE departure_code = :departureCode")
    fun getFavoritesByDeparture(departureCode: String): Flow<List<FavoriteEntity>>

    @Insert
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorite WHERE departure_code = :departureCode AND destination_code = :destinationCode")
    suspend fun deleteFavoriteByCodes(departureCode: String, destinationCode: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite WHERE departure_code = :departureCode AND destination_code = :destinationCode)")
    suspend fun isFavorite(departureCode: String, destinationCode: String): Boolean
}