package com.example.evcondata

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import com.example.evcondata.databinding.FragmentCarDetailBinding
import com.example.evcondata.model.Car
import com.example.evcondata.ui.myCar.MyCarViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.math.RoundingMode

@AndroidEntryPoint
class CarDetailFragment : Fragment() {

    private val myCarViewModel: MyCarViewModel by viewModels()
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
        binding.itemAcceleration.text = String.format("%s sec", if (car.acceleration == null) "?" else car.acceleration.toString())

        loadCarImage()

        lifecycle.coroutineScope.launch {
            car.name?.let {
                myCarViewModel.getAvgConsumption(it)
                    .collect { avg ->
                        binding.itemCommunityConsumption.text = String.format(
                            "%s kwh",
                            avg?.toBigDecimal()?.setScale(1, RoundingMode.HALF_EVEN) ?: "?"
                        )
                    }
            }
        }
        return binding.root
    }

    private fun loadCarImage() {
        if (car.imageUri?.isNotEmpty() == true) {
            Picasso.get().load(car.imageUri).into(binding.itemCarImage)
        }
    }
}