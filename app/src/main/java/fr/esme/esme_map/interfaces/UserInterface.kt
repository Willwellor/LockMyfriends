package fr.esme.esme_map.interfaces

import fr.esme.esme_map.model.POI
import fr.esme.esme_map.model.Position
import fr.esme.esme_map.model.Run

interface UserInterface {

    fun getPOIs() : List<POI>
    fun getRuns() : List<Run>
    fun getMyPosition() : Position

}