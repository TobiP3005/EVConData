package com.example.evcondata.ui.myCar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import com.example.evcondata.databinding.FragmentMyCarBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MyCarFragment : Fragment() {

    private val myCarViewModel: MyCarViewModel by viewModels()
    private lateinit var binding: FragmentMyCarBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyCarBinding.inflate(layoutInflater)

        binding = FragmentMyCarBinding.inflate(inflater, container, false)

        val carNameList = myCarViewModel.carNames
        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item, carNameList
        )
        adapter.insert("", 0)
        binding.spinnerCars.adapter = adapter

        var check = 0
        binding.spinnerCars.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(++check > 1) {
                    val t = p0!!.getItemAtPosition(p2).toString()
                    myCarViewModel.setMyCar(t)
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?){}
        }

        lifecycle.coroutineScope.launch {
            myCarViewModel.myCarFlow
                .collect { carName ->
                    val spinnerPosition = adapter.getPosition(carName)
                    binding.spinnerCars.setSelection(spinnerPosition)
                    loadData(carName)
                }
        }

        return binding.root
    }

    private fun loadData(carName: String){
        val car = myCarViewModel.myCar(carName)

        if (car != null){
            binding.itemPower.text = String.format("%s kw", if (car.power == null) "?" else car.power.toString())
            binding.itemSpeed.text = String.format("%s kmh", if (car.maxSpeed == null) "?" else car.maxSpeed.toString())
            binding.itemWltpRange.text = String.format("%s km", if (car.wltpRange == null) "?" else car.wltpRange.toString())
            binding.itemWltpConsumption.text = String.format("%s kwh", if (car.wltpConsumption == null) "?" else car.wltpConsumption.toString())
            binding.itemBattery.text = String.format("%s kwh", if (car.battery == null) "?" else car.battery.toString())
            binding.itemChargeSpeed.text = String.format("%s kw", if (car.chargeSpeed == null) "?" else car.chargeSpeed.toString())
            binding.itemAcceleration.text = String.format("%s kw", if (car.acceleration == null) "?" else car.acceleration.toString())

            Picasso.get().load(car.imageUri).into(binding.itemCarImage)
        }

        lifecycle.coroutineScope.launch {
            myCarViewModel.getAvgConsumption(carName)
                .collect { avg ->
                    binding.itemCommunityConsumption.text = String.format("%s kwh",
                        avg?.toString() ?: "?"
                    )
                }
        }

        lifecycle.coroutineScope.launch {
            myCarViewModel.getMyConsumption(carName)
                .collect { avg ->
                    binding.itemMyConsumption.text = String.format("%s kwh",
                        avg?.toString() ?: "?"
                    )
                }
        }
    }

    private fun refresh() {
        context.let {
        val fragmentManager = (context as? AppCompatActivity)?.supportFragmentManager
        fragmentManager?.let {
            val currentFragment = this
            currentFragment.let {
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.detach(it)
                fragmentTransaction.attach(it)
                fragmentTransaction.commit()
            }
        }
    }
    }
}