package com.example.evcondata.ui.consumption

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.example.evcondata.R
import com.example.evcondata.databinding.FragmentAddConsumptionBinding
import com.example.evcondata.model.Consumption
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [AddConsumptionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class AddConsumptionFragment : Fragment() {

    private lateinit var binding: FragmentAddConsumptionBinding
    private val consumptionViewModel: ConsumptionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddConsumptionBinding.inflate(inflater, container, false)

        binding.buttonSave.setOnClickListener{ view ->

            val consumptionEntry = Consumption(
                binding.EditTextName.text.toString(),
                binding.EditTextDistance.text.toString().toFloat(),
                binding.EditTextConsumption.text.toString().toFloat(),
                binding.EditTextTemperature.text.toString().toInt(),
                binding.EditTextAltitudeUp.text.toString().toInt(),
                binding.EditTextAltitudeDown.text.toString().toInt())

            val didSave = consumptionViewModel.saveConsumption(consumptionEntry)

            if (didSave) {
                Toast.makeText(context, "Successfully saved consumption!", Toast.LENGTH_SHORT).show()
                Navigation.findNavController(view).navigate(R.id.navigateToMyConsumptionFragment)
            }
            else
                Toast.makeText(context, "Failed to save consumption!", Toast.LENGTH_LONG).show()
        }
        return binding.root
    }
}