package com.example.evcondata

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.evcondata.databinding.FragmentAddConsumptionBinding
import com.example.evcondata.databinding.FragmentMyConsumptionListBinding

/**
 * A simple [Fragment] subclass.
 * Use the [AddConsumptionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddConsumptionFragment : Fragment() {

    private lateinit var binding: FragmentAddConsumptionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddConsumptionBinding.inflate(inflater, container, false)

        binding.buttonSave.setOnClickListener{ view ->
            Navigation.findNavController(view).navigate(R.id.navigateToMyConsumptionFragment)
        }


        return binding.root
    }
}