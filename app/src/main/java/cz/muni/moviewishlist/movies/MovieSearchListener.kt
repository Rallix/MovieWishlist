package cz.muni.moviewishlist.movies

import android.content.Context
import android.support.v7.widget.SearchView
import android.widget.ArrayAdapter
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import cz.muni.moviewishlist.R
import cz.muni.moviewishlist.main.OMDB_API
import cz.muni.moviewishlist.main.OMDB_DISPLAY_LIMIT
import org.json.JSONArray

open class MovieSearchListener(var context: Context,
                               var listAdapter: ArrayAdapter<String>,
                               var failListener: (error: String) -> Unit = {},
                               var successListener: () -> Unit) : SearchView.OnQueryTextListener {
    open fun clearView() {
        listAdapter.clear()
        listAdapter.notifyDataSetChanged()
    }

    open fun createRequest(search: String) : JsonObjectRequest {
        return createIMDbRequest(context, search, listAdapter, failListener) { moviesArray ->
            listAdapter.clear()
            for (i in 0 until minOf(moviesArray.length(), OMDB_DISPLAY_LIMIT)) {
                val movieObj = moviesArray.getJSONObject(i)
                val title = movieObj.getString("Title")
                listAdapter.add(title)
            }
            listAdapter.notifyDataSetChanged()
            successListener()
        }
    }

    override fun onQueryTextChange(search: String?): Boolean {
        // clearView()
        return search.isNullOrBlank()
    }

    override fun onQueryTextSubmit(search: String?): Boolean = true

    companion object {

        /**
         * Creates a request for searching for IMDb movie.
         */
        fun createIMDbRequest(context: Context,
                              searchString: String,
                              listAdapter: ArrayAdapter<String>,
                              failListener: (error: String) -> Unit = {},
                              successListener: (movies: JSONArray) -> Unit = {}) : JsonObjectRequest {
            val url = "$OMDB_API&type=movie&s=$searchString"

            val request = JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener { response ->
                    if (!response.getBoolean("Response")) {
                        // Too many results or No result
                        listAdapter.add(response.getString("Error"))
                        listAdapter.notifyDataSetChanged()

                        failListener(response.getString("Error"))
                    } else {
                        val moviesArray = response.getJSONArray("Search")

                        successListener(moviesArray)
                    }
                    listAdapter.notifyDataSetChanged()
                },
                Response.ErrorListener {
                    // Error message
                    listAdapter.add(context.getString(R.string.connection_error))
                    listAdapter.notifyDataSetChanged()

                    failListener(context.getString(R.string.connection_error))
                })
            request.tag = context
            return request
        }
    }
}