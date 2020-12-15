package fr.esme.esme_map

import android.graphics.Color
import fr.esme.esme_map.interfaces.UserInterface
import fr.esme.esme_map.model.*

class UserImplementation(val user: User) : UserInterface {

    override fun getPOIs(): List<POI> {
        return listOf(
            POI(
                user,
                "Carrefour",
                5,
                Position(2.3904678, 48.8131163),
                Category("Culture", Color())
            ),
            POI(
                user,
                "Pedra Alta",
                2,
                Position(2.3904678, 48.8131163),

                Category("Food", Color())
            )
        )
    }

    override fun getRuns(): List<Run> {
        //TODO call network  ===> DB distance ===> DB ====> Code
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
        //TODO ask GPS TD
        return Position(2.3929998, 48.8140771 )
    }


}