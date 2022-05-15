package com.example.evcondata.ui.consumption

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.evcondata.R
import com.example.evcondata.databinding.FragmentMyConsumptionListBinding
import com.example.evcondata.model.ResultCode
import com.example.evcondata.ui.consumption.adapter.MyConsumptionRecyclerViewAdapter
import com.example.evcondata.ui.consumption.adapter.SwipeToDeleteCallback
import com.example.evcondata.ui.consumption.adapter.SwipeToEditCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
@AndroidEntryPoint
class MyConsumptionListFragment : Fragment() {

    private lateinit var binding: FragmentMyConsumptionListBinding
    private lateinit var consumptionListRecycler: RecyclerView
    private val consumptionViewModel: ConsumptionViewModel by viewModels()
    private lateinit var consumptionListAdapter: MyConsumptionRecyclerViewAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyConsumptionListBinding.inflate(inflater, container, false)

        consumptionListRecycler = binding.consumptionList

        // Set the adapter
        consumptionListAdapter = MyConsumptionRecyclerViewAdapter()
        consumptionListRecycler.adapter = consumptionListAdapter
        consumptionListRecycler.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        binding.addItemActionButton.setOnClickListener { view ->
            Navigation.findNavController(view).navigate(R.id.navigateToAddConsumptionFragment)
        }

        keepConsumptionListUpdated()

        lifecycle.coroutineScope.launch {
            consumptionViewModel.sharedConFlow
                .collect { bool ->
                    binding.publishDataSwitch.isChecked = bool.toBoolean()
                }
        }

        binding.publishDataSwitch.setOnCheckedChangeListener { _, isChecked ->
            consumptionViewModel.updatePublishData(isChecked)
        }

        val swipeDeleteHandler = object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val entry =
                    consumptionListAdapter.consumptionList[viewHolder.absoluteAdapterPosition]
                lifecycle.coroutineScope.launch {
                    entry.id.let {
                        consumptionViewModel.deleteConsumption(it)
                            .collect { resultCode ->
                                when (resultCode) {
                                    ResultCode.SUCCESS -> {
                                        Toast.makeText(
                                            context,
                                            "Successfully deleted consumption!",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                    ResultCode.ERROR -> Toast.makeText(
                                        context,
                                        "Failed to delete consumption!",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                }
                            }
                    }
                }
                consumptionListAdapter.notifyDataSetChanged()
            }
        }

        val swipeEditHandler = object : SwipeToEditCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val entry =
                    consumptionListAdapter.consumptionList[viewHolder.absoluteAdapterPosition]
                view?.let {
                    Navigation.findNavController(it).navigate(
                        MyConsumptionListFragmentDirections.actionMyConsumptionFragmentToAddConsumptionFragment(
                            entry
                        )
                    )
                }
                consumptionListAdapter.notifyDataSetChanged()
            }
        }
        val itemTouchHelperDelete = ItemTouchHelper(swipeDeleteHandler)
        val itemTouchHelperEdit = ItemTouchHelper(swipeEditHandler)
        itemTouchHelperDelete.attachToRecyclerView(consumptionListRecycler)
        itemTouchHelperEdit.attachToRecyclerView(consumptionListRecycler)

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun keepConsumptionListUpdated() {

        lifecycle.coroutineScope.launch {
            consumptionViewModel.consumptionList
                ?.collect { list ->
                    consumptionListAdapter.setConsumptionList(list)
                    consumptionListAdapter.notifyDataSetChanged()
                }
        }
    }
}