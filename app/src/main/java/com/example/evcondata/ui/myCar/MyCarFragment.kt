package com.example.evcondata.ui.myCar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import com.example.evcondata.databinding.FragmentMyCarBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.math.RoundingMode


@AndroidEntryPoint
class MyCarFragment : Fragment() {

    private val myCarViewModel: MyCarViewModel by viewModels()
    private lateinit var binding: FragmentMyCarBinding
    private var myCar: String? = null
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyCarBinding.inflate(layoutInflater)
        binding = FragmentMyCarBinding.inflate(inflater, container, false)

        adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item, mutableListOf<String>()
        )
        adapter.insert("", 0)
        binding.spinnerCars.adapter = adapter

        var check = 0
        binding.spinnerCars.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (++check > 1) {
                    val car = p0!!.getItemAtPosition(p2).toString()
                    myCarViewModel.setMyCar(car)
                    loadData(car)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        keepCarListUpToDate()
        keepMyCarUpToDate()

        return binding.root
    }

    private fun keepMyCarUpToDate() {
        lifecycle.coroutineScope.launch {
            myCarViewModel.myCarFlow
                .collect { carName ->
                    myCar = carName
                    val spinnerPosition = adapter.getPosition(carName)
                    binding.spinnerCars.setSelection(spinnerPosition)
                    loadData(carName)
                }
        }
    }

    private fun keepCarListUpToDate() {
        lifecycle.coroutineScope.launch {
            myCarViewModel.carNames.collect { carNameList ->
                adapter.clear()
                if (myCar.isNullOrBlank()) {
                    adapter.insert("", 0)
                }
                adapter.addAll(carNameList)
                adapter.notifyDataSetChanged()
                if (myCar != null) {
                    val spinnerPosition = adapter.getPosition(myCar)
                    binding.spinnerCars.setSelection(spinnerPosition)
                }
            }
        }
    }

    private fun loadData(carName: String) {
        val car = myCarViewModel.getCar(carName)

        if (car != null) {
            binding.itemPower.text =
                String.format("%s kw", if (car.power == null) "?" else car.power.toString())
            binding.itemSpeed.text =
                String.format("%s kmh", if (car.maxSpeed == null) "?" else car.maxSpeed.toString())
            binding.itemWltpRange.text =
                String.format("%s km", if (car.wltpRange == null) "?" else car.wltpRange.toString())
            binding.itemWltpConsumption.text = String.format(
                "%s kwh",
                if (car.wltpConsumption == null) "?" else car.wltpConsumption.toString()
            )
            binding.itemBattery.text =
                String.format("%s kwh", if (car.battery == null) "?" else car.battery.toString())
            binding.itemChargeSpeed.text = String.format(
                "%s kw",
                if (car.chargeSpeed == null) "?" else car.chargeSpeed.toString()
            )
            binding.itemAcceleration.text = String.format(
                "%s sec",
                if (car.acceleration == null) "?" else car.acceleration.toString()
            )

            Picasso.get().load(car.imageUri).into(binding.itemCarImage)
        }

        lifecycle.coroutineScope.launch {
            myCarViewModel.getAvgConsumption(carName)
                .collect { avg ->
                    binding.itemCommunityConsumption.text = String.format(
                        "%s kwh",
                        avg?.toBigDecimal()?.setScale(1, RoundingMode.HALF_EVEN) ?: "?"
                    )
                }
        }

        lifecycle.coroutineScope.launch {
            myCarViewModel.getMyConsumption(carName)
                .collect { avg ->
                    binding.itemMyConsumption.text = String.format(
                        "%s kwh",
                        avg?.toBigDecimal()?.setScale(1, RoundingMode.HALF_EVEN) ?: "?"
                    )
                }
        }
    }
}