package fr.esme.esme_map

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import fr.esme.esme_map.interfaces.UserInterface
import fr.esme.esme_map.model.POI
import fr.esme.esme_map.model.Position

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val TAG = MainActivity::class.qualifiedName
    private lateinit var mMap: GoogleMap
    private lateinit var viewModel: MainActivityViewModel

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        viewModel.getPOIFromViewModel()
        viewModel.getPositionFromViewModel()

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        setContentView(R.layout.activity_main)

        //MAP
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel = MainActivityViewModel()

        viewModel.poisLiveData.observe(this, { listPOIs ->
            showPOI(listPOIs)
        })

        viewModel.myPositionLiveData.observe(this, { position ->
            showMyPosition(position)
        })


    }

    //TODO show POI
    fun showPOI(POIs: List<POI>) {
        POIs?.forEach {
            val poiPos = LatLng(it.position.latitude, it.position.longitude)
            mMap.addMarker(MarkerOptions().position(poiPos).title(it.name))
        }
    }

    //TODO show MyPosition
    fun showMyPosition(position: Position) {
        val myPos = LatLng(position.latitude, position.longitude)
        mMap.addMarker(MarkerOptions().position(myPos).title(""))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 14f))
    }


    //TODO show Travel
    //TODO show USer

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")

    }


    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")

    }


    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")

    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()

        mMap.clear()
    }
}