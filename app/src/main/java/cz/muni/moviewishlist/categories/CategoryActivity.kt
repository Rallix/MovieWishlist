package cz.muni.moviewishlist.categories

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import cz.muni.moviewishlist.main.INTENT_CATEGORY_ID
import cz.muni.moviewishlist.main.INTENT_CATEGORY_NAME
import cz.muni.moviewishlist.movies.MovieActivity
import cz.muni.moviewishlist.R
import cz.muni.moviewishlist.database.DbHandler
import kotlinx.android.synthetic.main.activity_category.*

/* Minimal requirements:

Aplikace pro vedení záznamů o filmech, které uživatel plánuje v budoucnu shlédnout.
Záznamy se ukládají do databáze a při zobrazení je lze řadit dle různých kritérií.
Možnost zaslání upozornění ve formě notifikace v určitý čas. */

class CategoryActivity : AppCompatActivity() {

    lateinit var dbHandler: DbHandler

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
            if (TextUtils.isEmpty(name)) {
                // TODO: Prevent from being closed
                categoryName.error = getString(R.string.empty_text_error)
            } else {
                val todo = Category()
                todo.name = name
                dbHandler.addCategory(todo)
                refreshList()
            }
        }
        dialog.setNegativeButton(getString(R.string.cancel_button)) { _: DialogInterface, _: Int ->
        }
        dialog.show()
    }

    /**
     * Creates a dialog for updating a specific [Category], updates it and refreshes the list.
     */
    private fun updateItemDialog(todo : Category) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(R.string.menu_edit_title)
        val view = layoutInflater.inflate(R.layout.dialog_category, null)
        val categoryName = view.findViewById<EditText>(R.id.et_category)
        categoryName.setText(todo.name)

        dialog.setView(view)
        dialog.setPositiveButton(getString(R.string.update_button)) { _: DialogInterface, _: Int ->
            val name = categoryName.text.toString().trim()
            if (TextUtils.isEmpty(name)) {
                // TODO: Prevent from being closed
                categoryName.error = getString(R.string.empty_text_error)
            } else {
                todo.name = name
                dbHandler.updateCategory(todo)
                refreshList()
            }
        }
        dialog.setNegativeButton(getString(R.string.cancel_button)) { _: DialogInterface, _: Int ->
        }
        dialog.show()
    }

    /**
     * Populates the [RecyclerView] with newly inserted items.
     */
    private fun refreshList() {
        rv_dashboard.adapter =
            CategoryAdapter(this, dbHandler.getCategories())
    }

    /**
     * Binds data to [RecyclerView]
     */
    class CategoryAdapter(private val activity: CategoryActivity, private val list: MutableList<Category>) :
        RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val categoryName: TextView = view.findViewById(R.id.tv_category_name)
            val menu: ImageView = view.findViewById(R.id.iv_category_menu)
        }

        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(
                    activity
                ).inflate(R.layout.rv_category, viewGroup, false)
            )
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            val category = list[position]

            viewHolder.categoryName.text = category.name
            viewHolder.categoryName.setOnClickListener {
                val intent = Intent(activity, MovieActivity::class.java)
                intent.putExtra(INTENT_CATEGORY_ID, category.id)
                intent.putExtra(INTENT_CATEGORY_NAME, category.name)
                activity.startActivity(intent)
            }
            viewHolder.menu.setOnClickListener {
                val popup = PopupMenu(activity, viewHolder.menu)
                popup.inflate(R.menu.dashboard_child)
                popup.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.menu_edit -> {
                            activity.updateItemDialog(category)
                        }
                        R.id.menu_delete -> {
                            val warningDialog = AlertDialog.Builder(activity)
                            warningDialog.setTitle(R.string.warning_title)
                            warningDialog.setMessage(R.string.warning_delete)
                            warningDialog.setNegativeButton(R.string.cancel_button) { _: DialogInterface?, _: Int -> }
                            warningDialog.setPositiveButton(R.string.confirm_button) { _: DialogInterface?, _: Int ->
                                activity.dbHandler.deleteCategory(category.id)
                                activity.refreshList()
                            }
                            warningDialog.show()
                        }
                        R.id.menu_check -> {
                            activity.dbHandler.watchMovieItem(category.id, true)
                        }
                        R.id.menu_reset -> {
                            activity.dbHandler.watchMovieItem(category.id, false)
                        }
                    }
                    true
                }
                popup.show()
            }
        }
    }
}