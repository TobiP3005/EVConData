package com.example.evcondata.ui.consumption

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.evcondata.databinding.FragmentPublicConsumptionListBinding
import com.example.evcondata.ui.consumption.adapter.PublicConsumptionRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
@AndroidEntryPoint
class PublicConsumptionFragment : Fragment() {

    private lateinit var binding: FragmentPublicConsumptionListBinding
    private lateinit var consumptionListRecycler: RecyclerView
    private val consumptionViewModel: ConsumptionViewModel by viewModels()
    private lateinit var consumptionListAdapter: PublicConsumptionRecyclerViewAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPublicConsumptionListBinding.inflate(inflater, container, false)

        consumptionListRecycler = binding.consumptionList

        // Set the adapter
        consumptionListAdapter = PublicConsumptionRecyclerViewAdapter()
        consumptionListRecycler.adapter = consumptionListAdapter
        consumptionListRecycler.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        keepConsumptionListUpdated()

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun keepConsumptionListUpdated() {

        lifecycle.coroutineScope.launch {
            consumptionViewModel.publicConsumptionList
                .collect { list ->
                    consumptionListAdapter.setConsumptionList(list)
                    consumptionListAdapter.notifyDataSetChanged()
                }
        }
    }
}