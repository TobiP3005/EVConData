package com.example.evcondata.ui.consumption

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.evcondata.R
import com.example.evcondata.databinding.FragmentMyConsumptionListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
@AndroidEntryPoint
class MyConsumptionListFragment : Fragment() {

    private lateinit var binding: FragmentMyConsumptionListBinding
    private val consumptionViewModel: ConsumptionViewModel by viewModels()
    private lateinit var consumptionListAdapter: MyConsumptionRecyclerViewAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyConsumptionListBinding.inflate(inflater, container, false)

        // Set the adapter
        consumptionListAdapter = MyConsumptionRecyclerViewAdapter()
        binding.consumptionList.adapter = consumptionListAdapter
        binding.consumptionList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        binding.addItemActionButton.setOnClickListener { view ->
            Navigation.findNavController(view).navigate(R.id.navigateToAddConsumptionFragment)
        }

        keepConsumptionListUpdated()

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun keepConsumptionListUpdated() {

        lifecycle.coroutineScope.launch {
            consumptionViewModel.consumptionList
                .collect { list ->
                    consumptionListAdapter.setConsumptionList(list)
                    consumptionListAdapter.notifyDataSetChanged()
                }
        }
    }
}