package com.example.evcondata

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.evcondata.databinding.FragmentMyConsumptionListBinding
import com.example.evcondata.model.Consumption
import com.example.evcondata.util.DatabaseManager
import com.google.android.material.snackbar.Snackbar

/**
 * A fragment representing a list of Items.
 */
class MyConsumptionFragment : Fragment() {

    private lateinit var binding: FragmentMyConsumptionListBinding
    private lateinit var consumptionListAdapter: MyConsumptionRecyclerViewAdapter

    private val dbMgr: DatabaseManager? = null
    private val item1: Consumption = Consumption("Work", 40, 100,100,15f, 23)
    private val item2: Consumption = Consumption("Roadtrip", 67, 900,200,16f, 31)
    private val item3: Consumption = Consumption("Holiday", 629, 4623,4532,18.4f, 35)
    private val sampleData: List<Consumption>? =
        mutableListOf(item1, item2, item3, item1, item2, item3, item1, item2, item3, item1, item2, item3, item1, item2, item3, item1, item2, item3, item1, item2, item3, item1, item2, item3)


    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyConsumptionListBinding.inflate(inflater, container, false)

        // Set the adapter
        consumptionListAdapter = sampleData?.let { MyConsumptionRecyclerViewAdapter(it) }!!
        binding.consumptionList.adapter = consumptionListAdapter
        binding.consumptionList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        binding.addItemActionButton.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        return binding.root
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            MyConsumptionFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}