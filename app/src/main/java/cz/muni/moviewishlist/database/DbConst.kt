package cz.muni.moviewishlist.database

const val DATABASE_NAME = "MovieList"
const val DB_VERSION = 1

const val COL_ID = "id"

const val TABLE_CATEGORY = "Category"
const val COL_CATEGORY_CREATED_AT = "createdAt"
const val COL_CATEGORY_NAME = "name"

const val TABLE_MOVIE_ITEM = "MovieItem"
const val COL_MOVIE_ITEM_ID = "movieId"
const val COL_MOVIE_ITEM_NAME = "itemName"
const val COL_MOVIE_ITEM_CREATED_AT = "createdAt"
const val COL_MOVIE_ITEM_WATCHED = "watched"
const val COL_MOVIE_ITEM_ORDER = "customOrder"