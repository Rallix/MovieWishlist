package cz.muni.moviewishlist.database

import android.util.Log

class ToDo {
    var id : Long = -1
    var name = ""
    var createdAt = ""

    var items : MutableList<ToDoItem> = ArrayList()

    override fun toString(): String {
        Log.d("To String", "ToDo Name: $name")
        return name
    }
}