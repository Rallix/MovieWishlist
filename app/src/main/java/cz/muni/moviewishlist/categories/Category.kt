package cz.muni.moviewishlist.categories

data class Category(var name:String) {
    var id:Long = -1

    override fun toString(): String = name
}