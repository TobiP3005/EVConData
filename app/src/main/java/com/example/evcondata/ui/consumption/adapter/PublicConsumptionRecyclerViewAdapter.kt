package com.example.evcondata.ui.consumption.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.evcondata.databinding.FragmentPublicConsumptionItemBinding
import com.example.evcondata.model.ConsumptionModelDTO

class PublicConsumptionRecyclerViewAdapter() : RecyclerView.Adapter<PublicConsumptionRecyclerViewAdapter.ViewHolder>() {

    var consumptionList = emptyList<ConsumptionModelDTO>()
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentPublicConsumptionItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = consumptionList[position].item
        holder.username.text = item.username
        holder.name.text = item.name
        holder.distance.text = String.format("%s km", if (item.distance == null) "?" else item.distance.toString())
        holder.consumption.text = String.format("%s kwh", if (item.consumption == null) "?" else item.consumption.toString())
        holder.altitudeUp.text = String.format("%s m", if (item.altitudeUp == null) "?" else item.altitudeUp.toString())
        holder.altitudeDown.text = String.format("%s m", if (item.altitudeDown == null) "?" else item.altitudeDown.toString())
        holder.temperature.text = String.format("%s°C", if (item.temperature == null) "?" else item.temperature.toString())
    }

    override fun getItemCount(): Int = consumptionList.size

    fun setConsumptionList(consumptionList: List<ConsumptionModelDTO>) {
        this.consumptionList = consumptionList
    }

    inner class ViewHolder(binding: FragmentPublicConsumptionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val username: TextView = binding.itemUsername
        val name: TextView = binding.itemName
        val distance: TextView = binding.itemDistance
        val consumption: TextView = binding.itemConsumption
        val altitudeUp: TextView = binding.itemAltitudeUp
        val altitudeDown: TextView = binding.itemAltitudeDown
        val temperature: TextView = binding.itemTemperature
    }

}