package fr.esme.esme_map

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.esme.esme_map.interfaces.UserInterface
import fr.esme.esme_map.model.User

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val TAG = MainActivity::class.qualifiedName
    private lateinit var mMap: GoogleMap

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val user = User("JP") //Me
        val userInterface: UserInterface? = UserImplementation(user)

        //MAP
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //BUTTON
        var button = findViewById<FloatingActionButton>(R.id.button)
        var helloWorldTextView = findViewById<TextView>(R.id.helloWorldTextView)

        //CallBack
        button.setOnClickListener {

            var POIString: String = ""
            userInterface?.getPOIs()?.forEach {

                POIString += "\n " + it.name +"   longitude = " +
                        it.position.longitude +
                        " latitude =" + it.position.latitude

            }

            helloWorldTextView.text = POIString

        }

        // Show user Position
        Log.d(
            TAG,
            "Show user Position: longitude = " +
                    userInterface?.getMyPosition()?.longitude +
                    " latitude =" + userInterface?.getMyPosition()?.latitude
        )

        userInterface?.getPOIs()?.forEach {
            Log.d(
                TAG,
                "Show user Run : longitude = " +
                        it.position.longitude +
                        " latitude =" + it.position.latitude
            )
        }

        userInterface?.getRuns()?.forEach {
            Log.d(
                TAG,
                "Show user POI : longitude = " +
                        it.positions +
                        " latitude =" + it.positions
            )
        }

    }

}