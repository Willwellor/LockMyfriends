package fr.esme.esme_map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import fr.esme.esme_map.dao.AppDatabase
import fr.esme.esme_map.model.POI
import fr.esme.esme_map.model.Position

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val TAG = MainActivity::class.qualifiedName
    private lateinit var mMap: GoogleMap
    private lateinit var viewModel: MainActivityViewModel
    private var isFriendShow = true


    private val POI_ACTIVITY = 1
    private lateinit var fusedLocationClient : FusedLocationProviderClient

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        viewModel.getPOIFromViewModel()
        viewModel.getPositionFromViewModel()

        mMap.setOnMapClickListener {

            val intent = Intent(this, CreatePOIActivity::class.java).apply {
                putExtra("LATLNG", it)
            }

            startActivityForResult(intent, POI_ACTIVITY)


        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == POI_ACTIVITY) {
            var t = data?.getStringExtra("poi")
            var poi = Gson().fromJson<POI>(t, POI::class.java)
            viewModel.savePOI(poi)
            showPOI(Gson().fromJson<POI>(t, POI::class.java))
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

        //BaseData
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).build()



        viewModel = MainActivityViewModel(db)

        viewModel.poisLiveData.observe(this, { listPOIs ->
            showPOIs(listPOIs)
        })

        viewModel.myPositionLiveData.observe(this, { position ->
            showMyPosition(position)
        })


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
       ) {

           this.requestPermissions(
               arrayOf<String>(
                   Manifest.permission.ACCESS_FINE_LOCATION,
                   Manifest.permission.ACCESS_COARSE_LOCATION
               ), 1
           )
       }

        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        var locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    showMyPosition(Position(location.latitude, location.longitude))
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
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


        val circleOptions = CircleOptions()
        circleOptions.center(myPos)
        circleOptions.radius(80.0)
        circleOptions.strokeColor(Color.WHITE)
        circleOptions.fillColor(Color.BLACK)
        circleOptions.strokeWidth(6f)

        if(this::myPositionCircle.isInitialized) {
            myPositionCircle.remove()
        }
        myPositionCircle =  mMap.addCircle(circleOptions)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 14f))
    }

    lateinit var myPositionCircle : Circle

    //TODO show Travel

    //TODO show USer
    fun manageUserVisibility() {

        if (isFriendShow) {
            isFriendShow = false
            findViewById<ListView>(R.id.friendsListRecyclerview).visibility = View.INVISIBLE
        } else {
            isFriendShow = true

            var friends = viewModel.getUsers()



            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, friends)
            findViewById<ListView>(R.id.friendsListRecyclerview).adapter = adapter




            findViewById<ListView>(R.id.friendsListRecyclerview).visibility = View.VISIBLE
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
      /*  Picasso.get().load("https://assets.stickpng.com/images/5a8efbf0b15d5c051b36901e.png")
            .into(findViewById<ImageView>(R.id.friend1))
        Picasso.get().load("https://assets.stickpng.com/images/5a8efbf0b15d5c051b36901e.png")
            .into(findViewById<ImageView>(R.id.friend2))
        Picasso.get().load("https://assets.stickpng.com/images/5a8efbf0b15d5c051b36901e.png")
            .into(findViewById<ImageView>(R.id.friend3))*/

    }

    var myPosition : Location? = null

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