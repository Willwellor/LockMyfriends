package fr.esme.esme_map

import android.graphics.Color
import androidx.room.Room
import fr.esme.esme_map.dao.AppDatabase
import fr.esme.esme_map.interfaces.UserInterface
import fr.esme.esme_map.model.*

class UserImplementation(val user: User, val appDatabase: AppDatabase) : UserInterface {

    override fun getPOIs(): List<POI> {
        return appDatabase.poiDao().getPOIs()
    }

    override fun getRuns(): List<Run> {
        //TODO call network  ===> DB distance ===> DB ====> Code
        return listOf(
            Run(
                user,
                "Footing Paris",
                5,
                listOf(Position(0.0, 0.0)),
                Category("Sport",12)
            ),
            Run(
                user,
                "Footing Versaille",
                5,
                listOf(Position(0.0, 0.0)),
                Category("Sport",12)
            )
        )
    }

    override fun getMyPosition(): Position {
        //TODO ask GPS TD
        return Position(2.3929998, 48.8140771 )
    }


    override fun getUsers(): List<User> {
        return listOf(user,user,user,user,user,user,user,user,user,user,user,user)
    }

}