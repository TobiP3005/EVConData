package com.example.evcondata.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import com.example.evcondata.R
import com.example.evcondata.model.Location
import com.example.evcondata.model.ResultCode
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_maps.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class MapsFragment : Fragment() {

    private val locationViewModel: MapsViewModel by viewModels()
    private lateinit var map: GoogleMap

    var myMarker: Marker? = null

    private val callback = OnMapReadyCallback { googleMap ->

        map = googleMap
        val locationHM = LatLng(48.15347871934155, 11.552724867284013)

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationHM, 8.0f))

        val myLocation = locationViewModel.myLocation
        if (myLocation != null) {
            myMarker = googleMap.addMarker(
                MarkerOptions()
                    .draggable(true)
                    .position(LatLng(myLocation.item.lat, myLocation.item.lon))
            )
        }

        googleMap.setOnMapLongClickListener { point ->
            myMarker?.remove()
            myMarker = googleMap.addMarker(MarkerOptions().position(point))

            lifecycle.coroutineScope.launch {
                val id: String = myLocation?.id ?: UUID.randomUUID().toString()

                locationViewModel.saveLocation(
                    Location(
                        null,
                        null,
                        point.latitude,
                        point.longitude,
                        null
                    ), id
                )
                    .collect { resultCode ->
                        when (resultCode) {
                            ResultCode.SUCCESS -> {
                                Toast.makeText(
                                    context,
                                    "Successfully saved location!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            ResultCode.ERROR -> Toast.makeText(
                                context,
                                "Failed to save location!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }

        googleMap.uiSettings.isMyLocationButtonEnabled = true

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        keepLocationListUpdated()

        publishLocationSwitch.isChecked = locationViewModel.locationSharedBool

        publishLocationSwitch.setOnCheckedChangeListener { _, isChecked ->
            locationViewModel.publishLocation(isChecked)
        }
    }

    private var communityMarkerList = ArrayList<Marker>()

    @SuppressLint("NotifyDataSetChanged")
    private fun keepLocationListUpdated() {

        lifecycle.coroutineScope.launch {
            locationViewModel.locationList
                ?.collect { locationList ->
                    communityMarkerList.forEach { marker -> marker.remove() }
                    locationList.forEach { location ->
                        map.addMarker(
                            MarkerOptions()
                                .position(LatLng(location.lat, location.lon))
                                .title(location.username).icon(
                                    BitmapDescriptorFactory.defaultMarker(
                                        BitmapDescriptorFactory.HUE_VIOLET
                                    )
                                )
                        )
                            ?.let {
                                communityMarkerList.add(
                                    it
                                )
                            }
                    }
                }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            map.isMyLocationEnabled = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}