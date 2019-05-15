package cz.muni.moviewishlist

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
import cz.muni.moviewishlist.database.DbHandler
import cz.muni.moviewishlist.database.ToDo
import kotlinx.android.synthetic.main.activity_dashboard.*

/*
Minimal requirements:

Aplikace pro vedení záznamů o filmech, které uživatel plánuje v budoucnu shlédnout.
Záznamy se ukládají do databáze a při zobrazení je lze řadit dle různých kritérií.
Možnost zaslání upozornění ve formě notifikace v určitý čas.
 */

// TODO:
// Diakritika
// Search bar | Vyhledávat z IMDb nebo přidat kliknutím nahoře
// Odstranit Splash Screen
// Víc barev | Jiná barva toolbaru
// Notifikace dle času

class DashboardActivity : AppCompatActivity() {

    lateinit var dbHandler: DbHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
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
     * Creates a dialog for adding a new [ToDo] entry and refreshes the list.
     */
    private fun addItemDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(R.string.menu_add_title)
        val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
        val todoName = view.findViewById<EditText>(R.id.et_todo)

        dialog.setView(view)
        dialog.setPositiveButton(getString(R.string.add_button)) { _: DialogInterface, _: Int ->
            val name = todoName.text.toString().trim()
            if (TextUtils.isEmpty(name)) {
                // TODO: Prevent from being closed
                todoName.error = getString(R.string.empty_text_error)
            } else {
                val todo = ToDo()
                todo.name = name
                dbHandler.addTodo(todo)
                refreshList()
            }
        }
        dialog.setNegativeButton(getString(R.string.cancel_button)) { _: DialogInterface, _: Int ->
        }
        dialog.show()
    }

    /**
     * Creates a dialog for updating a specific [ToDo], updates it and refreshes the list.
     */
    private fun updateItemDialog(todo : ToDo) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(R.string.menu_edit_title)
        val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
        val todoName = view.findViewById<EditText>(R.id.et_todo)
        todoName.setText(todo.name)

        dialog.setView(view)
        dialog.setPositiveButton(getString(R.string.update_button)) { _: DialogInterface, _: Int ->
            val name = todoName.text.toString().trim()
            if (TextUtils.isEmpty(name)) {
                // TODO: Prevent from being closed
                todoName.error = getString(R.string.empty_text_error)
            } else {
                todo.name = name
                dbHandler.updateTodo(todo)
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
        rv_dashboard.adapter = DashboardAdapter(this, dbHandler.getTodos())
    }

    /**
     * Binds data to [RecyclerView]
     */
    class DashboardAdapter(val activity: DashboardActivity, val list: MutableList<ToDo>) :
        RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val todoName: TextView = view.findViewById(R.id.tv_todo_name)
            val menu: ImageView = view.findViewById(R.id.iv_menu)
        }

        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.rv_child_dashboard, viewGroup, false))
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            val todo = list[position]

            viewHolder.todoName.text = todo.name
            viewHolder.todoName.setOnClickListener {
                val intent = Intent(activity, ItemActivity::class.java)
                intent.putExtra(INTENT_TODO_ID, todo.id)
                intent.putExtra(INTENT_TODO_NAME, todo.name)
                activity.startActivity(intent)
            }
            viewHolder.menu.setOnClickListener {
                val popup = PopupMenu(activity, viewHolder.menu)
                popup.inflate(R.menu.dashboard_child)
                popup.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.menu_edit -> {
                            activity.updateItemDialog(todo)
                        }
                        R.id.menu_delete -> {
                            val warningDialog = AlertDialog.Builder(activity)
                            warningDialog.setTitle(R.string.warning_title)
                            warningDialog.setMessage(R.string.warning_delete)
                            warningDialog.setNegativeButton(R.string.cancel_button) { _: DialogInterface?, _: Int -> }
                            warningDialog.setPositiveButton(R.string.confirm_button) { _: DialogInterface?, _: Int ->
                                activity.dbHandler.deleteTodo(todo.id)
                                activity.refreshList()
                            }
                            warningDialog.show()
                        }
                        R.id.menu_check -> {
                            activity.dbHandler.watchTodoItem(todo.id, true)
                        }
                        R.id.menu_reset -> {
                            activity.dbHandler.watchTodoItem(todo.id, false)
                        }
                    }
                    true
                }
                popup.show()
            }
        }
    }
}