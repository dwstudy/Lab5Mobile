package com.example.flightsearch.data.datasource

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

class DataStoreManager(private val context: Context) {

    companion object {
        val LAST_SEARCHED_AIRPORT = stringPreferencesKey("last_searched_airport")
    }

    suspend fun saveLastSearchedAirport(iataCode: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_SEARCHED_AIRPORT] = iataCode
        }
    }

    fun getLastSearchedAirport(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[LAST_SEARCHED_AIRPORT]
        }
    }

    suspend fun clearLastSearchedAirport() {
        context.dataStore.edit { preferences ->
            preferences.remove(LAST_SEARCHED_AIRPORT)
        }
    }
}