package com.example.evcondata.ui.car

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import com.example.evcondata.databinding.FragmentCarsListBinding
import com.example.evcondata.model.Car
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
@AndroidEntryPoint
class CarsListFragment : Fragment() {

    private lateinit var binding: FragmentCarsListBinding
    private val carListViewModel: CarListViewModel by viewModels()
    private lateinit var carsListRecycler: RecyclerView
    private lateinit var carsListAdapter: CarsRecyclerViewAdapter
    private lateinit var listener: CarItemListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCarsListBinding.inflate(inflater, container, false)
        carsListRecycler = binding.carsList
        listener = CarItemListener(this, CarsListFragmentDirections::actionCarsListFragmentToCarFragment)

        // Set the adapter
        carsListAdapter = CarsRecyclerViewAdapter(requireContext(), listener)
        carsListRecycler.adapter = carsListAdapter

        keepConsumptionListUpdated()

        return binding.root
    }

    private fun keepConsumptionListUpdated() {

        lifecycle.coroutineScope.launch {
            carListViewModel.carList
                ?.collect { list ->
                    carsListAdapter.setCarList(list)
                    carsListAdapter.notifyDataSetChanged()
                }
        }
    }
}

class CarItemListener(private val fragment: Fragment, private val handler: (r: Car) -> NavDirections){
    fun onClick(car: Car){
        val action = handler(car)

        NavHostFragment.findNavController(fragment).navigate(action)
    }
}