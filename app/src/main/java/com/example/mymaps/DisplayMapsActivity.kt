package com.example.mymaps

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mymaps.databinding.ActivityDisplayMapsBinding
import com.example.mymaps.model.UserMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class DisplayMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var userMaps : UserMap
    private lateinit var binding: ActivityDisplayMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDisplayMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        retrieve value of user map from intent
        userMaps = intent.getSerializableExtra(EXTRA_USER_MAP) as UserMap

//        update title of map as per user map's title
        supportActionBar?.title = userMaps.title

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map2) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

//      make map start from center and contain the area just with markers
        val boundsBuilder = LatLngBounds.Builder()
        for (place in userMaps.places){
            val LatLng = LatLng(place.latitude,place.longtitude)
            boundsBuilder.include(LatLng)
            mMap.addMarker(MarkerOptions().position(LatLng).title(place.tittle).snippet(place.description))
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(),1000,1000,0))

        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}