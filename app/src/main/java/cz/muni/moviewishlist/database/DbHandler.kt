package cz.muni.moviewishlist.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHandler(val context: Context) : SQLiteOpenHelper(context,
    DB_NAME, null,
    DB_VERSION
) {
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
     * Adds a [ToDoItem] entry to the database.
     */
    fun addTodoItem(item : ToDoItem) : Boolean {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(COL_TODO_ITEM_NAME, item.itemName)
        cv.put(COL_TODO_ID, item.toDoId)

        cv.put(COL_TODO_WATCHED, item.watched)

        val result = db.insert(TABLE_TODO_ITEM, null, cv)
        return result != (-1).toLong()
    }

    /**
     * Updates a [ToDoItem] entry in the database.
     */
    fun updateTodoItem(item : ToDoItem) {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(COL_TODO_ITEM_NAME, item.itemName)
        cv.put(COL_TODO_ID, item.toDoId)

        cv.put(COL_TODO_WATCHED, item.watched)

        db.update(TABLE_TODO_ITEM, cv, "$COL_ID=?", arrayOf(item.id.toString()))
    }

    /**
     * Retrieves a collection of [ToDo] from the database
     */
    fun getTodos() : MutableList<ToDo> {
        val db = readableDatabase
        return db.use { database ->
            val result : MutableList<ToDo> = ArrayList()
            val queryResult = database.rawQuery("SELECT * FROM $TABLE_TODO", null)
            if (queryResult.moveToFirst()) {
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

    fun getTodoItems(todoId : Long) : MutableList<ToDoItem> {
        // TODO: Part 3 | https://youtu.be/HTtAOW_GHE4?list=PLCH0RJhrZ8JKBdBo2HzjeIViT9OfdEjbV&t=323
        val db = readableDatabase
        return db.use { database ->
            val result : MutableList<ToDoItem> = ArrayList()
            val queryResult = database.rawQuery("SELECT * FROM $TABLE_TODO_ITEM WHERE $COL_TODO_ID = ?",
                Array(1) { todoId.toString()} )
            if (queryResult.moveToFirst()) {
                queryResult.use {
                    do {
                        val item = ToDoItem()
                        item.toDoId = todoId
                        item.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
                        item.itemName = queryResult.getString(queryResult.getColumnIndex(COL_TODO_ITEM_NAME))
                        item.watched = queryResult.getInt(queryResult.getColumnIndex(COL_TODO_WATCHED)) == 1 // boolean

                        result.add(item)
                    } while (queryResult.moveToNext())
                }
            }

            return@use result
        }
    }
}