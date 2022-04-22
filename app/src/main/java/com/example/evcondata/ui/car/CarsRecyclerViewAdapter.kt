package com.example.evcondata.ui.car

import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.evcondata.databinding.CarListItemBinding
import com.example.evcondata.model.Car

/**
 * [RecyclerView.Adapter] that can display a [Car].
 */
class CarsRecyclerViewAdapter(
    private val context: Activity,
    private val values: List<Car>
) : RecyclerView.Adapter<CarsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            CarListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.bind(item)

        if (!item.imageUri.isNullOrEmpty()) {
            Glide.with(context)
                .load(item.imageUri)
                .into(holder.imageView)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(private val binding: CarListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var recipe: Car? = binding.car
        val imageView: ImageView = binding.itemImage

        fun bind(car: Car) {
            binding.car = car
            binding.root.parent
            //binding.clickListener = listener
        }
    }
}