package com.example.evcondata

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.evcondata.databinding.FragmentCarDetailBinding
import com.example.evcondata.model.Car
import com.squareup.picasso.Picasso

class CarDetailFragment : Fragment() {

    private lateinit var binding: FragmentCarDetailBinding
    private lateinit var car: Car

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_car_detail, container, false)

        val args = CarDetailFragmentArgs.fromBundle(requireArguments())
        car = args.car
        (activity as AppCompatActivity?)?.supportActionBar?.title = car.name

        binding.itemPower.text = String.format("%s kw", if (car.power == null) "?" else car.power.toString())
        binding.itemSpeed.text = String.format("%s kmh", if (car.maxSpeed == null) "?" else car.maxSpeed.toString())
        binding.itemWltpRange.text = String.format("%s km", if (car.wltpRange == null) "?" else car.wltpRange.toString())
        binding.itemWltpConsumption.text = String.format("%s kwh", if (car.wltpConsumption == null) "?" else car.wltpConsumption.toString())
        binding.itemBattery.text = String.format("%s kwh", if (car.battery == null) "?" else car.battery.toString())
        binding.itemChargeSpeed.text = String.format("%s kw", if (car.chargeSpeed == null) "?" else car.chargeSpeed.toString())

        loadCarImage()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun loadCarImage() {
        if (car.imageUri?.isNotEmpty() == true) {
            Picasso.get().load(car.imageUri).into(binding.itemCarImage)
        }
    }
}