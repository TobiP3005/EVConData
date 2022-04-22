package com.example.evcondata.ui.car

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.evcondata.databinding.FragmentCarsListBinding
import com.example.evcondata.model.Car
import com.example.evcondata.ui.consumption.adapter.MyConsumptionRecyclerViewAdapter

/**
 * A fragment representing a list of Items.
 */
class CarsListFragment : Fragment() {

    private lateinit var binding: FragmentCarsListBinding
    private lateinit var carsListRecycler: RecyclerView
    private lateinit var carsListAdapter: CarsRecyclerViewAdapter
    private var columnCount = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCarsListBinding.inflate(inflater, container, false)
        carsListRecycler = binding.carsList

        val item1 = Car("Peugeot e208", 100, 50.0,17.0, 340,100, 150, "https://im-efahrer.chip.de/files/6012dd32bc488-peugeot-e-208-front-2.jpg?imPolicy=IfOrientation&width=720&height=405&color=%23000000&hash=ab22a03b6ab36ada765f214809f8bfe8113e26366e3b8c9ecf718d92bc202fa4")
        val item2 = Car("Honda e", 100, 35.5,16.8,180,56, 145, "https://media0.faz.net/ppmedia/aktuell/1289144484/1.7193140/format_top1_breit/honda-e.jpg")
        val item3 = Car("Tesla Model 3 LR", 350, 82.3,16.0,620,250, 233, "https://ecomento.de/wp-content/uploads/2022/04/Tesla-Model-3.jpg")
        val item4 = Car("Renault ZOE", 80, 52.0,17.3,395,46, 135, "https://www.nic-e.shop/wp-content/uploads/2019/09/zoe_neu-1.png")
        val sampleData: List<Car> =
            mutableListOf(item1, item2, item3, item4)

        // Set the adapter
        carsListAdapter = CarsRecyclerViewAdapter(context as Activity, sampleData)
        carsListRecycler.adapter = carsListAdapter

        return binding.root
    }
}