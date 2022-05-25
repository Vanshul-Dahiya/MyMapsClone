package com.example.mymaps

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.mymaps.databinding.ActivityCreateMapsBinding
import com.example.mymaps.model.Place
import com.example.mymaps.model.UserMap
import com.google.android.gms.maps.model.Marker
import com.google.android.material.snackbar.Snackbar

class CreateMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityCreateMapsBinding
    private  var markerslist : MutableList<Marker> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setting title as per given by user
        supportActionBar?.title = intent.getStringExtra(EXTRA_MAP_TITLE)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map1) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapFragment.view?.let {
            Snackbar.make(it,"Long press anywhere to add a marker",Snackbar.LENGTH_INDEFINITE)
                .setAction("ok",{})
                .show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_create_map,menu)
        return super.onCreateOptionsMenu(menu)
    }

//    here we are creating a map based on data given by user and passing it back to mainActivity
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuItemSave){
//            if markerlist is empty => user hasn't drawn any point yet
            if (markerslist.isEmpty()) {
                Toast.makeText(this, "There must be atleat one marker on the map", Toast.LENGTH_SHORT).show()
                return true
            }
//            otherwise we'll generate a usermap which will take two parameters -> title and list of places
//            we can get title from intent and for list of places , we will iterate through list of marker and for each marker we'll generate a place
//            this will be achieved by map function

            val places = markerslist.map { marker -> Place(marker.title,marker.snippet,marker.position.longitude,marker.position.latitude) }
            val usermap = UserMap(intent.getStringExtra(EXTRA_MAP_TITLE),places)
//            passing this data back to main activity
            val data = Intent()
            data.putExtra(EXTRA_USER_MAP,usermap)
            setResult(Activity.RESULT_OK,data)
            finish()
//            once this happens -> goto main activity onActivityResult
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


//      to delete the marker, user taps on info window when tapped on marker
        mMap.setOnInfoWindowClickListener {  markerToDelete ->
//            delete it from list
            markerslist.remove(markerToDelete)
//            remove from map
            markerToDelete.remove()
        }


//        long press to add a marker
//        every time user adds a marker , add it to list
//        but first show a alert dialog box
        mMap.setOnMapLongClickListener { latLng ->
          showAlerDailog(latLng)
        }

        // Add a marker in Sydney and move the camera
        val Bahadurgarh = LatLng(26.69, 76.93)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Bahadurgarh,11f))
    }

    private fun showAlerDailog(latLng: LatLng) {
//        this will only be done when user has tapped ok and info is filled

//        Floating action button will be clicked then dailog box will appear
//        fill out the info and select ok
//        a marker will be created after long pressing at that location

        val placeFormView = LayoutInflater.from(this).inflate(R.layout.dialog_create_place,null)
        val dailog =AlertDialog.Builder(this)
            .setTitle("Create a Marker")
            .setView(placeFormView)
            .setPositiveButton("ok",null)
            .setNegativeButton("Cancel",null)
            .show()

        dailog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val title = placeFormView.findViewById<EditText>(R.id.edTitle).text.toString()
            val description = placeFormView.findViewById<EditText>(R.id.edDescription).text.toString()
            if (title.trim().isEmpty() || description.trim().isEmpty()) {
                Toast.makeText(this,"Places must have non empty title and description",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val marker = mMap.addMarker(MarkerOptions().title(title)
                .position(latLng).snippet(description))
            if (marker != null) {
                markerslist.add(marker)

//                when ok is clicked , dismiss the alert dialog box
                dailog.dismiss()
            }
        }
    }

}