package com.example.evcondata.ui.myCar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.evcondata.databinding.FragmentMyCarBinding
import com.squareup.picasso.Picasso

class MyCarFragment : Fragment() {

    private lateinit var binding: FragmentMyCarBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyCarBinding.inflate(layoutInflater)

        binding = FragmentMyCarBinding.inflate(inflater, container, false)

        Picasso.get().load("https://www.evspecifications.info/wp-content/uploads/2020/01/peugeot-e-208-gt-evchargeplus-00-1-1024x768.png").into(binding.imageView3)

        return binding.root
    }
}