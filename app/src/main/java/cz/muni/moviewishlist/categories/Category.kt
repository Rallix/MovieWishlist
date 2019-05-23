package cz.muni.moviewishlist.categories

import cz.muni.moviewishlist.movies.MovieItem

class Category {
    var id : Long = -1
    var name = ""
    // var createdAt = ""

    var items : MutableList<MovieItem> = ArrayList()

    override fun toString(): String = name
}