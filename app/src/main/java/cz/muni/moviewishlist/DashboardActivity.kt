package cz.muni.moviewishlist

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import cz.muni.moviewishlist.database.ToDo
import kotlinx.android.synthetic.main.activity_dashboard.*

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
            val dialog = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
            val todoName = view.findViewById<EditText>(R.id.et_todo)

            dialog.setView(view)
            dialog.setPositiveButton(getString(R.string.add_button)) { _: DialogInterface, _: Int ->
                if (todoName.text.isNotEmpty()) {
                    addItem(todoName.text.toString())
                }
            }
            dialog.setNegativeButton(getString(R.string.cancel_button)) { _: DialogInterface, _: Int ->
            }
            dialog.show()
        }
    }

    override fun onResume() {
        refreshList()
        super.onResume()
    }

    /**
     * Adds a new [ToDo] and refreshes the list.
     */
    private fun addItem(name: String) {
        val todo = ToDo()
        todo.name = name
        dbHandler.addTodo(todo)
        refreshList()
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
    class DashboardAdapter(val context: Context, val list: MutableList<ToDo>) :
        RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val todoName: TextView = view.findViewById(R.id.tv_todo_name)
        }

        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(context).inflate(R.layout.rv_child_dashboard, viewGroup, false))
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.todoName.text = list[position].name
        }
    }
}