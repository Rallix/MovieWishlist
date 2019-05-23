package cz.muni.moviewishlist.movies

class MovieItem {
    var id :Long = -1
    var movieId:Long = -1
    var itemName = ""
    var watched = false
    var order:Long = -1

    override fun toString(): String = "$itemName ($order)"

}