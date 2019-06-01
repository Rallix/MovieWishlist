package cz.muni.moviewishlist.categories

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.EditText
import android.widget.Toast
import cz.muni.moviewishlist.R
import cz.muni.moviewishlist.database.DbHandler
import cz.muni.moviewishlist.main.Methods
import kotlinx.android.synthetic.main.activity_category.*

class CategoryActivity : AppCompatActivity() {

    lateinit var dbHandler: DbHandler
    var adapter : CategoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        setSupportActionBar(categories_toolbar)
        title = getString(R.string.dashboard_title)

        dbHandler = DbHandler(this)
        categories_view.layoutManager = LinearLayoutManager(this)

        categories_fab.setOnClickListener {
            addCategoryDialog()
        }
    }

    override fun onResume() {
        refreshList()
        super.onResume()
    }

    /**
     * Creates a dialog for adding a new [Category] entry and refreshes the list.
     */
    private fun addCategoryDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_category, null)
        Methods.createDialog(this, view, R.string.menu_add_title, createdListener = {}) { _, _ ->
            val categoryName = view.findViewById<EditText>(R.id.et_category)
            val name = categoryName.text.toString().trim()
            if (name.isNotEmpty()) {
                val category = Category(name)
                dbHandler.addCategory(category)
                refreshList()
            } else {
                Toast.makeText(this, getText(R.string.empty_text_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Creates a dialog for updating a specific [Category], updates it and refreshes the list.
     */
    internal fun updateItemDialog(category : Category) {
        val view = layoutInflater.inflate(R.layout.dialog_category, null)
        val categoryName = view.findViewById<EditText>(R.id.et_category)

        Methods.createDialog(this, view, R.string.menu_edit_title, createdListener = {
            categoryName.setText(category.name)
            categoryName.setSelection(category.name.length) // cursor to the end
        }) { _, _ ->
            val name = categoryName.text.toString().trim()
            if (name.isNotEmpty()) {
                category.name = name
                dbHandler.updateCategory(category)
                refreshList()
            } else {
                // Empty movie name
                Toast.makeText(this, getText(R.string.empty_text_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Populates the [RecyclerView] with newly inserted items.
     */
    internal fun refreshList() {
        if (adapter == null || categories_view.adapter == null) {
            adapter = CategoryAdapter(this, dbHandler.getCategories())
            categories_view.adapter = adapter
        } else {
            adapter?.recreate(dbHandler.getCategories())
        }
    }

}