package fr.esme.esme_map

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import fr.esme.esme_map.interfaces.UserInterface
import fr.esme.esme_map.model.User

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.qualifiedName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user = User("JP") //TODO Call database login user
        val userInterface: UserInterface? = UserImplementation(user)

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
                "Show user POI : longitude = " +
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