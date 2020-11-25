package fr.esme.esme_map

import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import fr.esme.esme_map.interfaces.UserInterface
import fr.esme.esme_map.model.User

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val TAG = MainActivity::class.qualifiedName
    private lateinit var mMap: GoogleMap

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

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
        var imageView = findViewById<ImageView>(R.id.imageView)

        Picasso.get().load(user.imageUrl).into(imageView)

        //TELECHARGER DEPUIS UNE URL
        //CallBack
        button.setOnClickListener {

            //
            imageView.visibility = View.INVISIBLE

            //afficher ma position
            val myPosition = userInterface?.getMyPosition()
            val myPos = LatLng(myPosition!!.latitude, myPosition.longitude)
            mMap.addMarker(
                MarkerOptions().position(myPos).icon(
                    BitmapDescriptorFactory.fromBitmap(imageView.drawable.toBitmap(100,100))
                ).title("My Position")
            )



            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 14f))

            //TODO afficher les POI

            var POIString: String = ""
            userInterface?.getPOIs()?.forEach {
                val poiPos = LatLng(it.position.latitude, it.position.longitude)
                mMap.addMarker(MarkerOptions().position(poiPos).title(it.name))
            }


        }
    }

}