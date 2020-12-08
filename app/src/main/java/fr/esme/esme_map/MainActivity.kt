package fr.esme.esme_map

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import fr.esme.esme_map.model.Category
import fr.esme.esme_map.model.POI
import fr.esme.esme_map.model.Position
import fr.esme.esme_map.model.User

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val TAG = MainActivity::class.qualifiedName
    private lateinit var mMap: GoogleMap
    private lateinit var viewModel: MainActivityViewModel
    private var isFriendShow = false


    private val POI_ACTIVITY = 1


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        viewModel.getPOIFromViewModel()
        viewModel.getPositionFromViewModel()

        mMap.setOnMapClickListener {

            val intent = Intent(this, CreatePOIActivity::class.java).apply {
                putExtra("LATLNG",it)
            }

            //intent.putExtra("LATLNG",it)

            startActivityForResult(intent, POI_ACTIVITY)


        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == POI_ACTIVITY){
            var t = data?.getStringExtra("POICREATE")
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        setContentView(R.layout.activity_main)

        //button
        findViewById<FloatingActionButton>(R.id.showFriendsButton).setOnClickListener {
            manageUserVisibility()
        }

        //MAP
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel = MainActivityViewModel()

        viewModel.poisLiveData.observe(this, { listPOIs ->
            showPOIs(listPOIs)
        })

        viewModel.myPositionLiveData.observe(this, { position ->
            showMyPosition(position)
        })


    }

    //TODO show POI
    fun showPOIs(POIs: List<POI>) {
        POIs?.forEach {
            val poiPos = LatLng(it.position.latitude, it.position.longitude)
            mMap.addMarker(MarkerOptions().position(poiPos).title(it.name))
        }
    }

    fun showPOI(poi: POI) {
        mMap.addMarker(
            MarkerOptions().position(
                LatLng(
                    poi.position.latitude,
                    poi.position.longitude
                )
            ).title(poi.name)
        )
    }

    //TODO show MyPosition
    fun showMyPosition(position: Position) {
        val myPos = LatLng(position.latitude, position.longitude)
        mMap.addMarker(MarkerOptions().position(myPos).title(""))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 14f))
    }


    //TODO show Travel

    //TODO show USer
    fun manageUserVisibility() {

        if (isFriendShow) {
            isFriendShow = false
            findViewById<ImageView>(R.id.friend1).visibility = View.INVISIBLE
            findViewById<ImageView>(R.id.friend2).visibility = View.INVISIBLE
            findViewById<ImageView>(R.id.friend3).visibility = View.INVISIBLE
        } else {
            isFriendShow = true
            findViewById<ImageView>(R.id.friend1).visibility = View.VISIBLE
            findViewById<ImageView>(R.id.friend2).visibility = View.VISIBLE
            findViewById<ImageView>(R.id.friend3).visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")

        //TODO mettre une photo a mes potte
        Picasso.get().load("https://assets.stickpng.com/images/5a8efbf0b15d5c051b36901e.png")
            .into(findViewById<ImageView>(R.id.friend1))
        Picasso.get().load("https://assets.stickpng.com/images/5a8efbf0b15d5c051b36901e.png")
            .into(findViewById<ImageView>(R.id.friend2))
        Picasso.get().load("https://assets.stickpng.com/images/5a8efbf0b15d5c051b36901e.png")
            .into(findViewById<ImageView>(R.id.friend3))

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