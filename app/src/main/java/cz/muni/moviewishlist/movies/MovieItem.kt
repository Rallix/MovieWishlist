package cz.muni.moviewishlist.movies
// data class
class MovieItem {
    var id :Long = -1
    var categoryId:Long = -1
    var itemName = ""
    var watched = false
    var order:Long = -1

    override fun toString(): String = itemName

}