package cz.muni.moviewishlist.database

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import cz.muni.moviewishlist.R
import cz.muni.moviewishlist.categories.Category
import cz.muni.moviewishlist.movies.MovieItem

class DbHandler(private val context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME, null,
    DB_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        val createToDoTable = "CREATE TABLE $TABLE_CATEGORY (" +
                "$COL_ID integer PRIMARY KEY AUTOINCREMENT," +
                "$COL_CATEGORY_CREATED_AT datetime DEFAULT CURRENT_TIMESTAMP," +
                "$COL_CATEGORY_NAME varchar);"
        val createToDoItemTable = "CREATE TABLE $TABLE_MOVIE_ITEM (" +
                "$COL_ID integer PRIMARY KEY AUTOINCREMENT," +
                "$COL_MOVIE_ITEM_CATEGORY_ID integer," +
                "$COL_MOVIE_ITEM_CREATED_AT datetime DEFAULT CURRENT_TIMESTAMP," +
                "$COL_MOVIE_ITEM_NAME varchar," +
                "$COL_MOVIE_ITEM_WATCHED boolean," +
                "$COL_MOVIE_ITEM_ORDER integer);"

        db.execSQL(createToDoTable)
        db.execSQL(createToDoItemTable)

        // Add two default categories
        val movieCategory = ContentValues()
        movieCategory.put(COL_CATEGORY_NAME, context.getString(R.string.category_movies))
        val seriesCategory = ContentValues()
        seriesCategory.put(COL_CATEGORY_NAME, context.getString(R.string.category_series))

        db.insert(TABLE_CATEGORY, null, movieCategory)
        db.insert(TABLE_CATEGORY, null, seriesCategory)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        // TODO: Migrations
        /*
        // Migration 1: order of movie items
        if (oldVersion < 2) {
            db?.execSQL("ALTER TABLE $TABLE_MOVIE_ITEM ADD COLUMN $COL_MOVIE_ITEM_ORDER integer DEFAULT 0")

            var currentOrder = 0
            val queryResult = db?.rawQuery("SELECT * FROM $TABLE_MOVIE_ITEM", null)
            if (queryResult?.moveToFirst() == true) {
                queryResult.use {
                    var hasItem = queryResult.moveToFirst()
                    while(hasItem) {
                        val id = queryResult.getInt(queryResult.getColumnIndex(COL_MOVIE_ITEM_CATEGORY_ID))
                        val values = ContentValues()
                        currentOrder += 20
                        values.put(COL_MOVIE_ITEM_ORDER, currentOrder)
                        db.update(TABLE_MOVIE_ITEM, values, "$COL_ID = ?", arrayOf(id.toString()))
                        hasItem = queryResult.moveToNext()
                    }
                    queryResult.close()
                }
            }
        }
        */
    }

    /**
     * Adds a [Category] entry to the database.
     */
    fun addCategory(category: Category): Boolean {
        val db = writableDatabase
        val cv = ContentValues()

        cv.put(COL_CATEGORY_NAME, category.name)

        val result = db.insert(TABLE_CATEGORY, null, cv)
        return result != (-1).toLong()
    }

    /**
     * Adds a [MovieItem] entry to the database.
     */
    fun addMovieItem(movie: MovieItem): Boolean {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(COL_MOVIE_ITEM_NAME, movie.itemName)
        cv.put(COL_MOVIE_ITEM_CATEGORY_ID, movie.categoryId)

        cv.put(COL_MOVIE_ITEM_WATCHED, movie.watched)

        val totalCountInCategory = DatabaseUtils.queryNumEntries(db, TABLE_MOVIE_ITEM,
            "$COL_MOVIE_ITEM_CATEGORY_ID = ?", arrayOf(movie.categoryId.toString()))
        cv.put(COL_MOVIE_ITEM_ORDER, totalCountInCategory + 1)

        val result = db.insert(TABLE_MOVIE_ITEM, null, cv)
        return result != (-1).toLong()
    }

    /**
     * Retrieves a collection of [Category] from the database
     */
    fun getCategories(): MutableList<Category> {
        val db = readableDatabase
        return db.use { database ->
            val result: MutableList<Category> = ArrayList()
            val queryResult = database.rawQuery("SELECT * FROM $TABLE_CATEGORY", null)
            if (queryResult.moveToFirst()) {
                queryResult.use {
                    do {
                        val category = Category()
                        category.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
                        category.name = queryResult.getString(queryResult.getColumnIndex(COL_CATEGORY_NAME))
                        result.add(category)
                    } while (queryResult.moveToNext())
                }
            }
            return@use result
        }
    }

    fun getMovieItems(categoryId: Long): MutableList<MovieItem> {
        val db = readableDatabase
        return db.use { database ->
            val result: MutableList<MovieItem> = ArrayList()
            val queryResult = database.rawQuery(
                "SELECT * FROM $TABLE_MOVIE_ITEM WHERE $COL_MOVIE_ITEM_CATEGORY_ID = ?",
                arrayOf(categoryId.toString())
            )
            if (queryResult.moveToFirst()) {
                queryResult.use {
                    do {
                        val item = MovieItem()
                        item.categoryId = categoryId
                        item.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
                        item.itemName = queryResult.getString(queryResult.getColumnIndex(COL_MOVIE_ITEM_NAME))
                        item.watched = queryResult.getInt(queryResult.getColumnIndex(COL_MOVIE_ITEM_WATCHED)) == 1 // boolean
                        item.order = queryResult.getLong(queryResult.getColumnIndex(COL_MOVIE_ITEM_ORDER))
                        result.add(item)
                    } while (queryResult.moveToNext())
                }
            }
            result.sortBy { it.order }
            return@use result
        }
    }

    /**
     * Updates a [Category] entry in the database.
     */
    fun updateCategory(category: Category) {
        val db = writableDatabase
        val cv = ContentValues()

        cv.put(COL_CATEGORY_NAME, category.name)
        db.update(TABLE_CATEGORY, cv, "$COL_ID = ?", arrayOf(category.id.toString()))
    }

    /**
     * Updates a [MovieItem] entry in the database.
     */
    fun updateMovieItem(item: MovieItem) {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(COL_MOVIE_ITEM_NAME, item.itemName)
        cv.put(COL_MOVIE_ITEM_CATEGORY_ID, item.categoryId)
        cv.put(COL_MOVIE_ITEM_WATCHED, item.watched)
        cv.put(COL_MOVIE_ITEM_ORDER, item.order)

        db.update(TABLE_MOVIE_ITEM, cv, "$COL_ID = ?", arrayOf(item.id.toString()))
    }



    fun deleteCategory(categoryId: Long) {
        val db = writableDatabase
        // Delete with all its children
        db.delete(TABLE_MOVIE_ITEM, "$COL_MOVIE_ITEM_CATEGORY_ID = ?", arrayOf(categoryId.toString()))
        db.delete(TABLE_CATEGORY, "$COL_ID = ?", arrayOf(categoryId.toString()))
    }

    fun deleteMovieItem(itemId: Long) {
        val db = writableDatabase
        db.delete(TABLE_MOVIE_ITEM, "$COL_ID = ?", arrayOf(itemId.toString()))
    }

    fun watchMovieItem(categoryId: Long, watched: Boolean) {
        val db = writableDatabase
        val queryResult = db.rawQuery(
            "SELECT * FROM $TABLE_MOVIE_ITEM WHERE $COL_MOVIE_ITEM_CATEGORY_ID = ?",
            arrayOf(categoryId.toString())
        )
        if (queryResult.moveToFirst()) {
            queryResult.use {
                do {
                    val item = MovieItem()
                    item.categoryId = categoryId
                    item.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
                    item.itemName = queryResult.getString(queryResult.getColumnIndex(COL_MOVIE_ITEM_NAME))
                    item.watched = watched
                    updateMovieItem(item)
                } while (queryResult.moveToNext())
            }
        }
    }
}