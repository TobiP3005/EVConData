package com.example.evcondata.ui.car

import android.content.Context
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
    private val context: Context,
    private val listener: CarItemListener
) : RecyclerView.Adapter<CarsRecyclerViewAdapter.ViewHolder>() {

    var carList = emptyList<Car>()
        private set

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
        val item = carList[position]
        holder.bind(item, listener)

        if (!item.imageUri.isNullOrEmpty()) {
            Glide.with(context)
                .load(item.imageUri)
                .into(holder.imageView)
        }
    }

    override fun getItemCount(): Int = carList.size

    fun setCarList(carList: List<Car>) {
        this.carList = carList
    }

    inner class ViewHolder(private val binding: CarListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val imageView: ImageView = binding.itemImage

        fun bind(car: Car, listener: CarItemListener) {
            binding.car = car
            binding.root.parent
            binding.clickListener = listener
        }
    }
}