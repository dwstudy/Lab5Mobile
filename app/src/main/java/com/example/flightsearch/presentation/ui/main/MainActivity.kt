package com.example.flightsearch.presentation.ui.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flightsearch.R
import com.example.flightsearch.presentation.adapter.AirportAdapter
import com.example.flightsearch.presentation.adapter.FlightAdapter
import com.example.flightsearch.presentation.viewmodel.MainViewModel
import com.example.flightsearch.presentation.viewmodel.MainViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import android.widget.Toast

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var rvAirportSuggestions: RecyclerView
    private lateinit var rvFlights: RecyclerView
    private lateinit var tvEmptyState: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tilSearch: com.google.android.material.textfield.TextInputLayout

    private lateinit var airportAdapter: AirportAdapter
    private lateinit var flightAdapter: FlightAdapter
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate started")
        setContentView(R.layout.activity_main)

        try {
            initViews()
            Log.d(TAG, "Views initialized")

            setupViewModel()
            Log.d(TAG, "ViewModel setup completed")

            setupRecyclerViews()
            Log.d(TAG, "RecyclerViews setup completed")

            setupSearchInput()
            Log.d(TAG, "Search input setup completed")

            setupObservers()
            Log.d(TAG, "Observers setup completed")

        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate: ${e.message}", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun initViews() {
        etSearch = findViewById(R.id.etSearch)
        rvAirportSuggestions = findViewById(R.id.rvAirportSuggestions)
        rvFlights = findViewById(R.id.rvFlights)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        progressBar = findViewById(R.id.progressBar)
        tilSearch = findViewById(R.id.tilSearch)
    }

    private fun setupViewModel() {
        try {
            Log.d(TAG, "Initializing ViewModelFactory...")
            MainViewModelFactory.initialize(applicationContext)

            Log.d(TAG, "Creating ViewModel...")
            viewModel = ViewModelProvider(this, MainViewModelFactory(applicationContext))
                .get(MainViewModel::class.java)

            Log.d(TAG, "ViewModel created: $viewModel")
        } catch (e: Exception) {
            Log.e(TAG, "Error in setupViewModel: ${e.message}", e)
            throw e
        }
    }

    private fun setupRecyclerViews() {
        airportAdapter = AirportAdapter { airport ->
            try {
                Log.d(TAG, "Airport clicked: ${airport.iataCode}")
                viewModel.selectAirport(airport)
                rvAirportSuggestions.visibility = View.GONE
            } catch (e: Exception) {
                Log.e(TAG, "Error in airport click: ${e.message}", e)
            }
        }

        flightAdapter = FlightAdapter { flight ->
            try {
                Log.d(TAG, "Flight favorite toggled: ${flight.destinationCode}")
                viewModel.toggleFavorite(flight)
            } catch (e: Exception) {
                Log.e(TAG, "Error in flight click: ${e.message}", e)
            }
        }

        rvAirportSuggestions.layoutManager = LinearLayoutManager(this)
        rvAirportSuggestions.adapter = airportAdapter

        rvFlights.layoutManager = LinearLayoutManager(this)
        rvFlights.adapter = flightAdapter
    }

    private fun setupSearchInput() {
        if (!::viewModel.isInitialized) {
            Log.e(TAG, "ERROR: viewModel not initialized in setupSearchInput")
            return
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d(TAG, "beforeTextChanged: $s")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d(TAG, "onTextChanged: $s")
                try {
                    if (::viewModel.isInitialized) {
                        viewModel.updateSearchQuery(s?.toString() ?: "")

                        if (s?.length ?: 0 >= 2) {
                            rvAirportSuggestions.visibility = View.VISIBLE
                        } else {
                            rvAirportSuggestions.visibility = View.GONE
                        }
                    } else {
                        Log.e(TAG, "viewModel not initialized in onTextChanged")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error in onTextChanged: ${e.message}", e)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                Log.d(TAG, "afterTextChanged: $s")
            }
        })

        tilSearch.setEndIconOnClickListener {
            try {
                if (::viewModel.isInitialized) {
                    viewModel.clearSearch()
                    etSearch.text?.clear()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in clear click: ${e.message}", e)
            }
        }
    }

    private fun setupObservers() {
        if (!::viewModel.isInitialized) {
            Log.e(TAG, "ERROR: viewModel not initialized in setupObservers")
            return
        }

        lifecycleScope.launch {
            try {
                viewModel.error.collectLatest { errorMessage ->
                    Log.d(TAG, "Error message received: $errorMessage")
                    if (errorMessage != null) {
                        Toast.makeText(this@MainActivity, "Error: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in error observer: ${e.message}", e)
            }
        }

        lifecycleScope.launch {
            try {
                viewModel.searchResults.collectLatest { airports ->
                    Log.d(TAG, "Search results received: ${airports.size} airports")
                    airportAdapter.submitList(airports)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in searchResults observer: ${e.message}", e)
            }
        }

        lifecycleScope.launch {
            try {
                viewModel.flights.collectLatest { flights ->
                    Log.d(TAG, "Flights received: ${flights.size} flights")
                    flightAdapter.submitList(flights)

                    if (flights.isEmpty()) {
                        tvEmptyState.visibility = View.VISIBLE
                        rvFlights.visibility = View.GONE
                    } else {
                        tvEmptyState.visibility = View.GONE
                        rvFlights.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in flights observer: ${e.message}", e)
            }
        }

        lifecycleScope.launch {
            try {
                viewModel.isLoading.collectLatest { isLoading ->
                    Log.d(TAG, "Loading state: $isLoading")
                    progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in loading observer: ${e.message}", e)
            }
        }
    }
}