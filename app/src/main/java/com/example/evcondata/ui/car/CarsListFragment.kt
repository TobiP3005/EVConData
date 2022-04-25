package com.example.evcondata.ui.car

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import com.example.evcondata.databinding.FragmentCarsListBinding
import com.example.evcondata.model.Car

/**
 * A fragment representing a list of Items.
 */
class CarsListFragment : Fragment() {

    private lateinit var binding: FragmentCarsListBinding
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


        val item1 = Car("Peugeot e208", 100, 50.0,16.4, 340,100, 150, "https://www.evspecifications.info/wp-content/uploads/2020/01/peugeot-e-208-gt-evchargeplus-00-1-1024x768.png")
        val item2 = Car("Honda e", 100, 35.5,16.8,180,56, 145, "https://assets.meinauto.de/image/upload/f_auto/q_auto:eco/c_scale,w_auto/v1//vehicledeals/Honda_e_Front")
        val item3 = Car("Tesla Model 3 LR", 350, 82.3,16.0,620,250, 233, "https://adhgfzvyfq.cloudimg.io/v7/https://id-cs.com/media/car_images/car_368/Model_3_Pearl_White_Multi-Coat.png?w=1300")
        val item4 = Car("Renault ZOE", 80, 52.0,17.3,395,46, 135, "https://www.nic-e.shop/wp-content/uploads/2019/09/zoe_neu-1.png")
        val sampleData: List<Car> =
            mutableListOf(item1, item2, item3, item4)

        // Set the adapter
        carsListAdapter = CarsRecyclerViewAdapter(context as Activity, listener, sampleData)
        carsListRecycler.adapter = carsListAdapter

        return binding.root
    }
}

class CarItemListener(private val fragment: Fragment, private val handler: (r: Car) -> NavDirections){
    fun onClick(car: Car){
        val action = handler(car)

        NavHostFragment.findNavController(fragment).navigate(action)
    }
}