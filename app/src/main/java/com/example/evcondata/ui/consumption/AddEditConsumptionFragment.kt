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
import androidx.lifecycle.coroutineScope
import androidx.navigation.Navigation
import com.example.evcondata.R
import com.example.evcondata.databinding.FragmentAddConsumptionBinding
import com.example.evcondata.model.Consumption
import com.example.evcondata.model.ConsumptionModelDTO
import com.example.evcondata.model.ResultCode.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class AddEditConsumptionFragment : Fragment() {

    private lateinit var binding: FragmentAddConsumptionBinding
    private val consumptionViewModel: ConsumptionViewModel by viewModels()

    private lateinit var mDateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var mTimeSetListener: TimePickerDialog.OnTimeSetListener

    private val myCalendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"))

    private var editConsumptionEntry: ConsumptionModelDTO? = null

    private val timeZone: TimeZone = TimeZone.getTimeZone("Europe/Berlin")
    private val dateFormat = SimpleDateFormat("dd-MM-yy", Locale.GERMANY)
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.GERMANY)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddConsumptionBinding.inflate(inflater, container, false)

        dateFormat.timeZone = timeZone
        timeFormat.timeZone = timeZone

        editConsumptionEntry = arguments?.getParcelable("consumptionEntry")

        if (editConsumptionEntry != null) {

            val item = editConsumptionEntry!!.item

            binding.buttonSave.text = resources.getString(R.string.update)

            val dateTimeString = item.datetime
            val localDateTime = LocalDateTime.parse(dateTimeString)

            val datePart =
                localDateTime.toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yy"))
            val timePart = localDateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))

            binding.editTextDate.setText(datePart)
            binding.editTextTime.setText(timePart)

            val name = item.name
            val distance = item.distance
            val consumption = item.consumption
            val temperature = item.temperature
            val altitudeUp = item.altitudeUp
            val altitudeDown = item.altitudeDown

            name?.let { binding.EditTextName.setText(name) }
            distance?.let { binding.EditTextDistance.setText(distance.toString()) }
            consumption?.let { binding.EditTextConsumption.setText(consumption.toString()) }
            temperature?.let { binding.EditTextTemperature.setText(temperature.toString()) }
            altitudeUp?.let { binding.EditTextAltitudeUp.setText(altitudeUp.toString()) }
            altitudeDown?.let { binding.EditTextAltitudeDown.setText(altitudeDown.toString()) }
        }

        manageDateTimeTextView()

        binding.buttonSave.setOnClickListener { view ->

            val consumptionEntry = Consumption(
                binding.EditTextName.text.toString(),
                getDateTimeString(),
                binding.EditTextDistance.text.toString().toIntOrNull(),
                binding.EditTextConsumption.text.toString().toFloatOrNull(),
                binding.EditTextTemperature.text.toString().toIntOrNull(),
                binding.EditTextAltitudeUp.text.toString().toIntOrNull(),
                binding.EditTextAltitudeDown.text.toString().toIntOrNull(),
                null
            )

            val id: String = if (editConsumptionEntry != null)
                editConsumptionEntry!!.id
            else
                UUID.randomUUID().toString()

            lifecycle.coroutineScope.launch {
                consumptionViewModel.saveConsumption(consumptionEntry, id)
                    .collect { resultCode ->
                        when (resultCode) {
                            SUCCESS -> {
                                Toast.makeText(
                                    context,
                                    "Successfully saved consumption!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Navigation.findNavController(view)
                                    .navigate(R.id.navigateToMyConsumptionFragment)
                            }
                            ERROR -> Toast.makeText(
                                context,
                                "Failed to save consumption!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }
        return binding.root
    }

    private fun getDateTimeString(): String {
        val datePart: LocalDate? = LocalDate.parse(
            binding.editTextDate.text.toString(),
            DateTimeFormatter.ofPattern("dd-MM-yy")
        )
        val timePart: LocalTime? = LocalTime.parse(
            binding.editTextTime.text.toString(),
            DateTimeFormatter.ofPattern("HH:mm")
        )

        return LocalDateTime.of(datePart, timePart).toString()
    }

    private fun manageDateTimeTextView() {


        binding.editTextDate.setText(dateFormat.format(Date()))
        binding.editTextTime.setText(timeFormat.format(Date()))

        binding.editTextDate.setOnClickListener {
            val year = myCalendar.get(Calendar.YEAR)
            val month = myCalendar.get(Calendar.MONTH)
            val day = myCalendar.get(Calendar.DAY_OF_MONTH)

            val dialog = DatePickerDialog(requireContext(), mDateSetListener, year, month, day)
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

            val dialog = TimePickerDialog(requireContext(), mTimeSetListener, hour, minute, false)
            dialog.show()
        }

        mTimeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->

            myCalendar.set(Calendar.HOUR, hour)
            myCalendar.set(Calendar.MINUTE, minute)

            binding.editTextTime.setText(timeFormat.format(myCalendar.time))
        }
    }
}