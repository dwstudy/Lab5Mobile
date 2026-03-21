package com.example.flightsearch.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.flightsearch.R
import com.example.flightsearch.domain.model.Flight

class FlightAdapter(
    private val onFavoriteClick: (Flight) -> Unit
) : RecyclerView.Adapter<FlightAdapter.FlightViewHolder>() {

    private var flights = listOf<Flight>()

    fun submitList(newFlights: List<Flight>) {
        flights = newFlights
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_flight, parent, false)
        return FlightViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlightViewHolder, position: Int) {
        holder.bind(flights[position])
    }

    override fun getItemCount() = flights.size

    inner class FlightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDestinationCode: TextView = itemView.findViewById(R.id.tvDestinationCode)
        private val tvDestinationName: TextView = itemView.findViewById(R.id.tvDestinationName)
        private val tvFlightNumber: TextView = itemView.findViewById(R.id.tvFlightNumber)
        private val btnFavorite: ImageButton = itemView.findViewById(R.id.btnFavorite)

        fun bind(flight: Flight) {
            tvDestinationCode.text = flight.destinationCode
            tvDestinationName.text = flight.destinationName
            tvFlightNumber.text = "Flight: ${flight.departureCode} → ${flight.destinationCode}"

            btnFavorite.setImageResource(
                if (flight.isFavorite) R.drawable.ic_favorite_filled
                else R.drawable.ic_favorite_border
            )

            btnFavorite.setOnClickListener {
                onFavoriteClick(flight)
            }
        }
    }
}