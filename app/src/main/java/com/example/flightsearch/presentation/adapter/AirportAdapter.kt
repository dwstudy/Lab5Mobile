package com.example.flightsearch.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flightsearch.R
import com.example.flightsearch.domain.model.Airport

class AirportAdapter(
    private val onAirportClick: (Airport) -> Unit
) : RecyclerView.Adapter<AirportAdapter.AirportViewHolder>() {

    private var airports = listOf<Airport>()

    fun submitList(newAirports: List<Airport>) {
        airports = newAirports
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AirportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_airport, parent, false)
        return AirportViewHolder(view)
    }

    override fun onBindViewHolder(holder: AirportViewHolder, position: Int) {
        holder.bind(airports[position])
    }

    override fun getItemCount() = airports.size

    inner class AirportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvAirportCode: TextView = itemView.findViewById(R.id.tvAirportCode)
        private val tvAirportName: TextView = itemView.findViewById(R.id.tvAirportName)

        fun bind(airport: Airport) {
            tvAirportCode.text = airport.iataCode
            tvAirportName.text = airport.name
            itemView.setOnClickListener {
                onAirportClick(airport)
            }
        }
    }
}