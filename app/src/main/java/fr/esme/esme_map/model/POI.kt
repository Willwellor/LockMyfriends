package fr.esme.esme_map.model

class POI(
    val owner: User,
    val name: String,
    var score: Int,
    var position: Position,
    var category: Category
)