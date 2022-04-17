package com.example.evcondata.ui.myCar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.evcondata.databinding.FragmentMyCarBinding

class MyCarFragment : Fragment() {

    private lateinit var binding: FragmentMyCarBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyCarBinding.inflate(layoutInflater)

        binding = FragmentMyCarBinding.inflate(inflater, container, false)

        return binding.root
    }
}