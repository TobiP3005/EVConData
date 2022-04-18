package com.example.evcondata.ui.consumption

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
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

    private lateinit var mDateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var mTimeSetListener: TimePickerDialog.OnTimeSetListener

    private val myCalendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"))


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddConsumptionBinding.inflate(inflater, container, false)

        manageDateTimeTextView()

        binding.buttonSave.setOnClickListener{ view ->

            val consumptionEntry = Consumption(
                binding.EditTextName.text.toString(),
                getDateTimeString(),
                binding.EditTextDistance.text.toString().toFloatOrNull(),
                binding.EditTextConsumption.text.toString().toFloatOrNull(),
                binding.EditTextTemperature.text.toString().toIntOrNull(),
                binding.EditTextAltitudeUp.text.toString().toIntOrNull(),
                binding.EditTextAltitudeDown.text.toString().toIntOrNull())

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

    private fun getDateTimeString(): String {
        val datePart: LocalDate? = LocalDate.parse(binding.editTextDate.text.toString(), DateTimeFormatter.ofPattern("dd-MM-yy"))
        val timePart: LocalTime? = LocalTime.parse(binding.editTextTime.text.toString(), DateTimeFormatter.ofPattern("HH:mm"))

        return LocalDateTime.of(datePart, timePart).toString()
    }

    private fun manageDateTimeTextView(){

        val timeZone = TimeZone.getTimeZone("Europe/Berlin")
        val dateFormat = SimpleDateFormat("dd-MM-yy", Locale.GERMANY)
        dateFormat.timeZone = timeZone
        binding.editTextDate.setText(dateFormat.format(Date()))

        val timeFormat = SimpleDateFormat("HH:mm", Locale.GERMANY)
        timeFormat.timeZone = timeZone
        binding.editTextTime.setText(timeFormat.format(Date()))

        binding.editTextDate.setOnClickListener { view ->
            val year = myCalendar.get(Calendar.YEAR)
            val month = myCalendar.get(Calendar.MONTH)
            val day = myCalendar.get(Calendar.DAY_OF_MONTH)

            val dialog = DatePickerDialog(requireContext(), mDateSetListener,  year, month, day)
            dialog.show()
        }

        mDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->

            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, day)

            binding.editTextDate.setText(dateFormat.format(myCalendar.time))
        }

        binding.editTextTime.setOnClickListener { view ->
            val hour = myCalendar.get(Calendar.HOUR_OF_DAY)
            val minute = myCalendar.get(Calendar.MINUTE)

            val dialog = TimePickerDialog(requireContext(), mTimeSetListener,  hour, minute, false)
            dialog.show()
        }

        mTimeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->

            myCalendar.set(Calendar.HOUR, hour)
            myCalendar.set(Calendar.MINUTE, minute)

            binding.editTextTime.setText(timeFormat.format(myCalendar.time))
        }
    }
}