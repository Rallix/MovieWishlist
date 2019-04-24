package cz.muni.moviewishlist

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import cz.muni.moviewishlist.database.ToDo

class DbHandler(val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val createToDoTable = "CREATE TABLE $TABLE_TODO (" +
                "$COL_ID integer PRIMARY KEY AUTOINCREMENT," +
                "$COL_CREATED_AT datetime DEFAULT CURRENT_TIMESTAMP," +
                "$COL_NAME varchar);"
        val createToDoItemTable = "CREATE TABLE $TABLE_TODO_ITEM (" +
                "$COL_ID integer PRIMARY KEY AUTOINCREMENT," +
                "$COL_TODO_ID integer," +
                "$COL_CREATED_AT datetime DEFAULT CURRENT_TIMESTAMP," +
                "$COL_TODO_ITEM_NAME varchar," +
                "$COL_TODO_WATCHED boolean);"

        db.execSQL(createToDoTable)
        db.execSQL(createToDoItemTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Adds a [ToDo] entry to the database.
     */
    fun addTodo(todo: ToDo) : Boolean {
        val db = writableDatabase
        val cv = ContentValues()

        cv.put(COL_NAME, todo.name)

        val result = db.insert(TABLE_TODO, null, cv)
        return result != (-1).toLong()
    }

    /**
     * Retrieves a collection of [ToDo] from the database
     */
    fun getTodos() : MutableList<ToDo> {
        val db = readableDatabase
        return db.use { database ->
            val queryResult = database.rawQuery("SELECT * FROM $TABLE_TODO", null)
            val result : MutableList<ToDo> = ArrayList()
            // TODO: Part 2 | 9:30 | https://youtu.be/kdQrElD6ak4?list=PLCH0RJhrZ8JKBdBo2HzjeIViT9OfdEjbV&t=599
            if(queryResult.moveToFirst()) {
                queryResult.use {
                    do {
                        val todo = ToDo()
                        todo.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
                        todo.name = queryResult.getString(queryResult.getColumnIndex(COL_NAME))

                        result.add(todo)
                    } while (queryResult.moveToNext())
                }
            }
            return@use result
        }
    }
}