package cz.muni.moviewishlist.movies

data class MovieItem(var categoryId:Long,
                     var itemName:String,
                     var watched:Boolean = false) {

    var id:Long = -1
    var order:Long = -1

    override fun toString(): String = itemName
}