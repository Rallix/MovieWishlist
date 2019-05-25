package cz.muni.moviewishlist.categories

import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.EditText
import android.widget.Toast
import cz.muni.moviewishlist.R
import cz.muni.moviewishlist.database.DbHandler
import kotlinx.android.synthetic.main.activity_category.*

/* Minimal requirements:

Aplikace pro vedení záznamů o filmech, které uživatel plánuje v budoucnu shlédnout.
Záznamy se ukládají do databáze a při zobrazení je lze řadit dle různých kritérií.
Možnost zaslání upozornění ve formě notifikace v určitý čas. */

class CategoryActivity : AppCompatActivity() {

    lateinit var dbHandler: DbHandler
    var adapter : CategoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        setSupportActionBar(dashboard_toolbar)
        title = getString(R.string.dashboard_title)

        dbHandler = DbHandler(this)
        rv_dashboard.layoutManager = LinearLayoutManager(this)

        fab_dashboard.setOnClickListener {
            addItemDialog()
        }
    }

    override fun onResume() {
        refreshList()
        super.onResume()
    }

    /**
     * Creates a dialog for adding a new [Category] entry and refreshes the list.
     */
    private fun addItemDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(R.string.menu_add_title)
        val view = layoutInflater.inflate(R.layout.dialog_category, null)
        val categoryName = view.findViewById<EditText>(R.id.et_category)

        dialog.setView(view)
        dialog.setPositiveButton(getString(R.string.add_button)) { _: DialogInterface, _: Int ->
            val name = categoryName.text.toString().trim()
            if (name.isNotEmpty()) {
                val category = Category()
                category.name = name
                dbHandler.addCategory(category)
                refreshList()
            } else {
                // categoryName.error = getString(R.string.empty_text_error)
                Toast.makeText(this, getText(R.string.empty_text_error), Toast.LENGTH_SHORT).show()
            }
        }
        dialog.setNegativeButton(getString(R.string.cancel_button)) { _: DialogInterface, _: Int ->
        }
        dialog.show()
    }

    /**
     * Creates a dialog for updating a specific [Category], updates it and refreshes the list.
     */
    internal fun updateItemDialog(category : Category) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(R.string.menu_edit_title)
        val view = layoutInflater.inflate(R.layout.dialog_category, null)
        val categoryName = view.findViewById<EditText>(R.id.et_category)
        categoryName.setText(category.name)

        dialog.setView(view)
        dialog.setPositiveButton(getString(R.string.update_button)) { _: DialogInterface, _: Int ->
            val name = categoryName.text.toString().trim()
            if (name.isNotEmpty()) {
                category.name = name
                dbHandler.updateCategory(category)
                refreshList()
            } else {
                // Empty movie name
                // categoryName.error = getString(R.string.empty_text_error) // ← TODO: Prevent from being closed
                Toast.makeText(this, getText(R.string.empty_text_error), Toast.LENGTH_SHORT).show()
            }
        }
        dialog.setNegativeButton(getString(R.string.cancel_button)) { _: DialogInterface, _: Int -> }
        dialog.show()
    }

    /**
     * Populates the [RecyclerView] with newly inserted items.
     */
    internal fun refreshList() {
        if (adapter == null || rv_dashboard.adapter == null) {
            adapter = CategoryAdapter(this, dbHandler.getCategories())
            rv_dashboard.adapter = adapter
        } else {
            adapter?.recreate(dbHandler.getCategories())
        }
    }

}