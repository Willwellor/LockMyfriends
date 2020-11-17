package fr.esme.esme_map

import android.graphics.Color
import fr.esme.esme_map.interfaces.UserInterface
import fr.esme.esme_map.model.*

class UserImplementation(val user: User) : UserInterface {

    override fun getPOIs(): List<POI> {
        return listOf(
            POI(
                user,
                "Monument",
                5,
                Position(0.2, 0.0),
                Category("Culture", Color())
            ),
            POI(
                user,
                "Sushi",
                2,
                Position(0.1, 0.0),
                Category("Food", Color())
            )
        )
    }

    override fun getRuns(): List<Run> {
        //TODO call network
        return listOf(
            Run(
                user,
                "Footing Paris",
                5,
                listOf(Position(0.0, 0.0)),
                Category("Sport", Color())
            ),
            Run(
                user,
                "Footing Versaille",
                5,
                listOf(Position(0.0, 0.0)),
                Category("Sport", Color())
            )
        )
    }

    override fun getMyPosition(): Position {
        //TODO ask GPS
        return Position(0.0, 0.0)
    }


}