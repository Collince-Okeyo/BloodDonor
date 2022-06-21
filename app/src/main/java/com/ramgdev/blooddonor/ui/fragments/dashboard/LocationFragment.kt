package com.ramgdev.blooddonor.ui.fragments.dashboard

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ramgdev.blooddonor.R
import com.ramgdev.blooddonor.databinding.FragmentLocationBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*


class LocationFragment : Fragment() {

    private lateinit var binding: FragmentLocationBinding

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val currentLocation = LatLng(0.620019, 34.522920)
        googleMap.addMarker(MarkerOptions().position(currentLocation).title("Marker in Kibabii"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))

        val latitude = 0.620019
        val longitude = 34.522920

        val zoomLevel = 15f

        val homeLatLong = LatLng(latitude, longitude)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLong, zoomLevel))
        googleMap.addMarker(MarkerOptions().position(homeLatLong))

        setMapLongClicked(googleMap)
        setPoiClick(googleMap)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLocationBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun setMapLongClicked(map: GoogleMap) {
        map.setOnMapLongClickListener { latLong ->
            val snippet = String.format(
                Locale.getDefault(), "Lat: %1$.5f, Long: %2$.5f",
                latLong.latitude,
                latLong.longitude
            )

            map.addMarker(
                MarkerOptions()
                    .position(latLong)
                    .title("Dropped Pin")
                    .snippet(snippet)
            )

        }
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            poiMarker?.showInfoWindow()
        }
    }


    private fun searLocation(){

        val searchView = binding.idSearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                val location = searchView.query.toString()
                var addressList: List<Address>? = null

                val geocoder = Geocoder(requireContext())

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        addressList = geocoder.getFromLocationName(location, 1)
                    } catch (e: IOException){
                        e.printStackTrace()
                    }

                    val address: Address = addressList!!.get(0)

                    val latLng = LatLng(address.latitude, address.longitude)

                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // To Do
                return true
            }
        })

    }
}