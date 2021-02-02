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
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
import fr.esme.esme_map.interfaces.UserClickInterface
import fr.esme.esme_map.model.POI
import fr.esme.esme_map.model.Position
import fr.esme.esme_map.model.User

class MainActivity : AppCompatActivity(), OnMapReadyCallback, UserClickInterface {

    private val TAG = MainActivity::class.qualifiedName
    private lateinit var mMap: GoogleMap
    private lateinit var viewModel: MainActivityViewModel
    private var isFriendShow = true


    private val POI_ACTIVITY = 1
    private val USER_ACTIVITY = 2
    private lateinit var fusedLocationClient : FusedLocationProviderClient

    //génération de la carte depuis GoogleMap
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        viewModel.getPOIFromViewModel()
        viewModel.getPositionFromViewModel()

        mMap.setOnMapClickListener {

            val intent = Intent(this, CreatePOIActivity::class.java).apply {
                putExtra("LATLNG", it)
            }

            //activation de la vue avec la map
            startActivityForResult(intent, POI_ACTIVITY)


        }

    }

    //
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

        //affichage de la page général
        setContentView(R.layout.activity_main)

        //affichage du bouton de la liste des amis ( ############# a changer pour avoir la liste des amis proche de nous ###############)
        findViewById<FloatingActionButton>(R.id.showFriendsButton).setOnClickListener {
            manageUserVisibility()
        }

        //affichage de maniere asynchrone de la carte dans la vue
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //Base de donnée interne au téléphone
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).build()



        //vue général
        viewModel = MainActivityViewModel(db)

        //affichage des positions sur la vue
        viewModel.poisLiveData.observe(this, { listPOIs ->
            showPOIs(listPOIs)
        })

        //affichage de ma position sur la vue
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

        //requete de rafraichissement de la position renvoyée par le GPS
        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        //fonction de rapelle apres la mise a jour de la position GPS, rafraichissement de la page
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

    //liste des positions des lieux
    //TODO show POI
    fun showPOIs(POIs: List<POI>) {
        POIs?.forEach {
            val poiPos = LatLng(it.position.latitude, it.position.longitude)
            mMap.addMarker(MarkerOptions().position(poiPos).title(it.name))
        }
    }

    //affichage de la position d'un lieu
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

    //affichage de ma position par une pastille noir
    //TODO show MyPosition
    fun showMyPosition(position: Position) {

        //mettre une condition sur la paosition pour eviter le rafraichissement de la page à chaque requète

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

            val adapter = FriendsAdapter(this, ArrayList(friends))
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

    //activation de la page d'ajout d'une activité
    override fun OnUserClick(user: User) {

        Log.d("ADAPTER", user.username)

        val intent = Intent(this, UserActivity::class.java).apply {
            putExtra("USER", Gson().toJson(user))
        }

        //activation de la vue d'ajout d'activité
        startActivityForResult(intent, USER_ACTIVITY)



    }
}