package com.example.mymaps

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymaps.model.Place
import com.example.mymaps.model.UserMap
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*

const val TAGs = "MainActivity"
const val EXTRA_USER_MAP = "EXTRA_USER_MAP"
const val EXTRA_MAP_TITLE = "EXTRA_MAP_TITLE"
private const val REQUEST_CODE = 1234    // can be any arbitrary int
private const val  FILENAME = "Usermaps.data"

class MainActivity : AppCompatActivity() {

    private  lateinit var  usermaps : MutableList<UserMap>
    private  lateinit var  mapAdapter : MapsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        set layout manager and adapter on recyclerview
        usermaps = deserializedUserMaps(this).toMutableList()
        rVmaps.layoutManager = LinearLayoutManager(this)
        mapAdapter = MapsAdapter(this, usermaps,object : MapsAdapter.onClickListener{
            override fun onItemClick(position: Int) {

//        when user taps on view in recyclerview , navigate to new activity
                Log.i(TAGs, "onItemClick: item clicked @ $position")
                Toast.makeText(this@MainActivity,"Item clicked @ $position",Toast.LENGTH_SHORT).show()

                val intent = Intent(this@MainActivity,DisplayMapsActivity::class.java)
//        made it serializable so that it can be passed through intent in diff activities
                intent.putExtra(EXTRA_USER_MAP, usermaps[position])
                startActivity(intent)
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right)
            }
        })
        rVmaps.adapter = mapAdapter

//        Floating action button will be clicked then dailog box will appear
//        fill out the info and select ok
//        a marker will be created after long pressing at that location

        floatingActionButton.setOnClickListener{
//            navigate to a new activity where user will add a place and locations
//            and that new activity will retrieve data back to main activity
//            also make a dailog box appear asking for map title
            Toast.makeText(this,"Clicked",Toast.LENGTH_LONG).show()
            showAlerDailog()
        }
    }
    private fun showAlerDailog() {

        val mapFormView = LayoutInflater.from(this).inflate(R.layout.dialog_create_map,null)
        val dailog = AlertDialog.Builder(this)
            .setTitle("Map Title")
            .setView(mapFormView)
            .setPositiveButton("ok",null)
            .setNegativeButton("Cancel",null)
            .show()

        dailog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val title = mapFormView.findViewById<EditText>(R.id.edMapTitle).text.toString()
            if (title.trim().isEmpty() ) {
                Toast.makeText(this,"Map must have a Title",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

//            Navigate to create map activity
            val intent = Intent(this,CreateMapsActivity ::class.java)
            intent.putExtra(EXTRA_MAP_TITLE , title)
            startActivityForResult(intent,REQUEST_CODE)
            dailog.dismiss()
        }
    }


//    when createmapActivity is done doing it's job i.e create new user map
//    we'll capture it's result back in main activity to update recyclerview
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
//            get new map data from user
            val usermap = data?.getSerializableExtra(EXTRA_USER_MAP) as UserMap
//          telling adapter we have another entry and adding created map to recyclerview
            usermaps.add(usermap)
            mapAdapter.notifyItemInserted(usermaps.size-1)
            serializedUserMaps(this,usermaps)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

//    Read -> DeSerialization ,  write -> Serialisation
//   should be called whenever data around the map is changing and that happens  when user creates new usermap i.e onActivityResult
    private fun serializedUserMaps(context: Context,userMAps : List<UserMap>) {
    Log.i(TAG, "DeSerializavleFile")
    ObjectOutputStream(FileOutputStream(getDataFile((context)))).use {
            it.writeObject(userMAps)
        }
    }
//  called whenever we want to display list i.e at the very beginning
    private fun deserializedUserMaps(context: Context) : List<UserMap>{
    Log.i(TAG, "getDataFile: ${context.filesDir}")
    val dataFile = getDataFile(context)
        if (!dataFile.exists()){
            return emptyList()
        }
//        Read info from file and create a list of usermaps from it
        ObjectInputStream(FileInputStream(dataFile)).use {
            return it.readObject() as List<UserMap>
        }
    }

//    return the file which other methods can read/write
    private fun getDataFile(context: Context) : File {
    Log.i(TAG, "getDataFile: ${context.filesDir}")
        return File(context.filesDir,FILENAME)
    }

    private fun generateSampleData(): List<UserMap> {
        return listOf(
            UserMap(
                "Memories from University",
                listOf(
                    Place("Branner Hall", "Best dorm at Stanford", 37.426, -122.163),
                    Place("Gates CS building", "Many long nights in this basement", 37.430, -122.173),
                    Place("Pinkberry", "First date with my wife", 37.444, -122.170)
                )),
            UserMap("January vacation planning!",
                listOf(
                    Place("Tokyo", "Overnight layover", 35.67, 139.65),
                    Place("Ranchi", "Family visit + wedding!", 23.34, 85.31),
                    Place("Singapore", "Inspired by \"Crazy Rich Asians\"", 1.35, 103.82)
                )),
            UserMap("Singapore travel itinerary",
                listOf(
                    Place("Gardens by the Bay", "Amazing urban nature park", 1.282, 103.864),
                    Place("Jurong Bird Park", "Family-friendly park with many varieties of birds", 1.319, 103.706),
                    Place("Sentosa", "Island resort with panoramic views", 1.249, 103.830),
                    Place("Botanic Gardens", "One of the world's greatest tropical gardens", 1.3138, 103.8159)
                )),
            UserMap("My favorite places in the Midwest",
                listOf(
                    Place("Chicago", "Urban center of the midwest, the \"Windy City\"", 41.878, -87.630),
                    Place("Rochester, Michigan", "The best of Detroit suburbia", 42.681, -83.134),
                    Place("Mackinaw City", "The entrance into the Upper Peninsula", 45.777, -84.727),
                    Place("Michigan State University", "Home to the Spartans", 42.701, -84.482),
                    Place("University of Michigan", "Home to the Wolverines", 42.278, -83.738)
                )
            ),
            UserMap("Restaurants to try",
                listOf(
                    Place("Champ's Diner", "Retro diner in Brooklyn", 40.709, -73.941),
                    Place("Althea", "Chicago upscale dining with an amazing view", 41.895, -87.625),
                    Place("Shizen", "Elegant sushi in San Francisco", 37.768, -122.422),
                    Place("Citizen Eatery", "Bright cafe in Austin with a pink rabbit", 30.322, -97.739),
                    Place("Kati Thai", "Authentic Portland Thai food, served with love", 45.505, -122.635)
                ))
        )
    }

}