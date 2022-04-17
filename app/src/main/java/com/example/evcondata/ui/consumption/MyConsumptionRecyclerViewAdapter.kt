package com.example.evcondata.ui.consumption

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.evcondata.databinding.FragmentMyConsumptionBinding
import com.example.evcondata.model.Consumption

class MyConsumptionRecyclerViewAdapter() : RecyclerView.Adapter<MyConsumptionRecyclerViewAdapter.ViewHolder>() {

    private var mConsumptionList: List<Consumption> = ArrayList()

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
        val item = mConsumptionList[position]
        holder.name.text = item.name
        holder.distance.text = String.format("%.2f km", item.distance)
        holder.consumption.text = String.format("%.2f kwh", item.consumption)
        holder.altitudeUp.text = String.format("%d m", item.altitudeUp)
        holder.altitudeDown.text = String.format("%d m", item.altitudeDown)
        holder.temperature.text = String.format("%d°C", item.temperature)
    }

    override fun getItemCount(): Int = mConsumptionList.size

    fun setConsumptionList(consumptionList: List<Consumption>) {
        mConsumptionList = consumptionList
    }

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