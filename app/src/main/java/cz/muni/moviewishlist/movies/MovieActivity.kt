package cz.muni.moviewishlist.movies

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import cz.muni.moviewishlist.R
import cz.muni.moviewishlist.database.DbHandler
import cz.muni.moviewishlist.main.*
import kotlinx.android.synthetic.main.activity_movie.*
import java.util.*

class MovieActivity : AppCompatActivity() {

    lateinit var dbHandler: DbHandler
    private var categoryId: Long = -1

    var touchHelper :ItemTouchHelper? = null
    var adapter : MovieAdapter? = null
    var list : MutableList<MovieItem>? = null

    private var displayList : MutableList<MovieItem>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)
        setSupportActionBar(movie_toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = intent.getStringExtra(INTENT_CATEGORY_NAME)
        categoryId = intent.getLongExtra(INTENT_CATEGORY_ID, -1)

        dbHandler = DbHandler(this)
        movie_view.layoutManager = LinearLayoutManager(this)

        // Create new movie with plus button
        movie_fab.setOnClickListener {
            addItemDialog()
        }

        // Drag & Drop
        // TODO: Swapping when search-filtering doesn't make sense
        touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
            override fun onMove(p0: RecyclerView, position1: RecyclerView.ViewHolder, position2: RecyclerView.ViewHolder): Boolean {
                // Swap two items (vertically)
                val sourcePosition = position1.adapterPosition
                val targetPosition = position2.adapterPosition
                Collections.swap(list, sourcePosition, targetPosition)

                adapter?.notifyItemMoved(sourcePosition, targetPosition)
                return true
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                // Recalculate new order when Drag & Drop finishes
                list?.forEachIndexed {index, movie ->
                    movie.order = index.toLong() + 1
                    dbHandler.addOrUpdateMovieItem(movie)
                }
                // TODO: notifyDatasetChanged here changes the order back
            }

            override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
                // Never called because swipeDirs = 0
            }
        })
        touchHelper?.attachToRecyclerView(movie_view)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Search
        menuInflater.inflate(R.menu.main, menu)
        val searchItem = menu?.findItem(R.id.menu_search)
        if (searchItem != null) {
            val searchView = searchItem.actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText!!.isNotBlank()) refreshList(newText)
                    else refreshList()

                    return true
                }
            })
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onResume() {
        refreshList()
        super.onResume()
    }

    /**
     * Creates a search dialog for adding a new [MovieItem] entry and refreshes the list.
     */
    private fun addItemDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(R.string.menu_add_item_title)
        val dialogView = layoutInflater.inflate(R.layout.dialog_search_movie, null)
        val search = dialogView.findViewById<SearchView>(R.id.sv_search_movieItem)
        val searchResults = dialogView.findViewById<ListView>(R.id.lv_search_movieItem)
        val listAdapter = ArrayAdapter<String>(this, R.layout.movie_results_view)
        searchResults.adapter = listAdapter

        dialog.setView(dialogView)
        dialog.setPositiveButton(R.string.add_button) { _: DialogInterface, _: Int ->
            val name = search.query.toString().trim()
            if (name.isNotEmpty()) {
                val item = MovieItem(categoryId, name, false)
                dbHandler.addOrUpdateMovieItem(item)
                refreshList()
            } else {
                // Empty movie name
                Toast.makeText(this, getText(R.string.empty_text_error), Toast.LENGTH_SHORT).show()
            }
        }
        dialog.setNegativeButton(R.string.cancel_button, null)

        val alert = dialog.create()
        alert.show()

        var success = false
        searchResults.setOnItemClickListener { _, _, position, _ ->
            if (success) {
                val movieTitle = searchResults.getItemAtPosition(position).toString()

                val item = MovieItem(categoryId, movieTitle, false)
                dbHandler.addOrUpdateMovieItem(item)
                refreshList()
                // Close dialog
                alert.dismiss()
            }
        }

        search.setOnQueryTextListener (object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(s: String?): Boolean {
                listAdapter.clear()
                listAdapter.notifyDataSetChanged()
                success = false
                return true
            }
            override fun onQueryTextSubmit(s: String?): Boolean {
                listAdapter.clear()
                val url = "$OMDB_API&type=movie&s=${s.toString()}"

                val queue = Volley.newRequestQueue(this@MovieActivity)
                val req = JsonObjectRequest(Request.Method.GET, url, null,
                    Response.Listener { response ->
                        if (!response.getBoolean("Response")) {
                            // Too many results or No result

                            // LEAK! Kdyz otocim displej nebo odejdu z aktivity, view by melo umrit, ale Volley
                            // request si tady bude cely obrovsky Context aktivity bude drzet, dokud sam neskonci,
                            // protoze si drzi listAdapter
                            // Resi se pomoci WeakReference
                            // Stejny problem nastava u vsech asynchronnich volani z tridy extendujici Context,
                            // v jejichz vysledku se nejak pristupuje k view, takze predpokladam, ze to budes mit
                            // na vice mistech
                            listAdapter.add(response.getString("Error"))
                        } else {
                            success = true
                            val moviesArray = response.getJSONArray("Search")
                            for (i in 0 until minOf(moviesArray.length(), OMDB_DISPLAY_LIMIT)) {
                                val movieObj = moviesArray.getJSONObject(i)
                                val title = movieObj.getString("Title")
                                listAdapter.add(title)
                            }
                        }
                        listAdapter.notifyDataSetChanged()
                    },
                    Response.ErrorListener {
                        // Error message
                        listAdapter.add(getString(R.string.connection_error))
                        listAdapter.notifyDataSetChanged()
                    })
                queue.add(req)
                return true
            }
        })
    }

    /**
     * Creates a dialog for updating a [MovieItem] and refreshes the list afterwards
     */
    internal fun updateMovieDialog(movieItem: MovieItem) {
        val view = layoutInflater.inflate(R.layout.dialog_movie, null)
        val movieName = view.findViewById<EditText>(R.id.et_movieItem)
        Methods.createDialog(this, view, R.string.menu_edit_title, createdListener = {
            movieName.setText(movieItem.itemName)
            movieName.setSelection(movieItem.itemName.length) // cursor to the end
        }) { _, _ ->
            val name = movieName.text.toString().trim()
            if (name.isNotEmpty()) {
                movieItem.categoryId = categoryId
                movieItem.itemName = name
                movieItem.watched = false

                dbHandler.addOrUpdateMovieItem(movieItem)
                refreshList()
            } else {
                // Empty movie name
                Toast.makeText(this, getText(R.string.empty_text_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Populates the [RecyclerView] with newly inserted items and optionally filters them.
     */
    internal fun refreshList(searchFilter:String = "") {
        list = dbHandler.getMovieItems(categoryId)
        displayList?.clear()
        if (searchFilter.isNotBlank()) {
            // Filter searched
            val search = searchFilter.trim().toLowerCase()
            list?.forEach {
                if (it.itemName.toLowerCase().contains(search)) {
                    displayList?.add(it)
                }
            }
        } else {
            // Copy the entire list
            displayList = list?.toMutableList()
        }
        if (adapter == null || movie_view.adapter == null) {
            adapter = MovieAdapter(this, displayList!!) // TODO: Don't recreate
            movie_view.adapter = adapter
        } else {
            adapter?.recreate(displayList!!)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            finish() // Quit activity on back button
            true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }
}
