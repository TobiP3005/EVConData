package com.example.evcondata

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

import com.example.evcondata.databinding.FragmentMyConsumptionBinding
import com.example.evcondata.model.Consumption

class MyConsumptionRecyclerViewAdapter(
    private val values: List<Consumption>
) : RecyclerView.Adapter<MyConsumptionRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentMyConsumptionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.name.text = item.name
        holder.distance.text = String.format("%d km", item.distance)
        holder.consumption.text = String.format("%.2f kwh", item.consumption)
        holder.altitudeUp.text = String.format("%d m", item.altitudeUp)
        holder.altitudeDown.text = String.format("%d m", item.altitudeDown)
        holder.temperature.text = String.format("%d°C", item.temperature)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentMyConsumptionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val name: TextView = binding.itemName
        val distance: TextView = binding.itemDistance
        val consumption: TextView = binding.itemConsumption
        val altitudeUp: TextView = binding.itemAltitudeUp
        val altitudeDown: TextView = binding.itemAltitudeDown
        val temperature: TextView = binding.itemTemperature
    }

}