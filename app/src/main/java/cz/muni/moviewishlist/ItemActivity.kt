package cz.muni.moviewishlist

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import cz.muni.moviewishlist.database.DbHandler
import cz.muni.moviewishlist.database.ToDo
import cz.muni.moviewishlist.database.ToDoItem
import kotlinx.android.synthetic.main.activity_item.*

class ItemActivity : AppCompatActivity() {

    lateinit var dbHandler: DbHandler
    var todoId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)
        setSupportActionBar(item_toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = intent.getStringExtra(INTENT_TODO_NAME)
        todoId = intent.getLongExtra(INTENT_TODO_ID, -1)

        dbHandler = DbHandler(this)
        rv_item.layoutManager = LinearLayoutManager(this)

        // Create new sub-item on plus button
        fab_item.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
            val todoName = view.findViewById<EditText>(R.id.et_todo)

            dialog.setView(view)
            dialog.setPositiveButton(R.string.add_button) { _: DialogInterface, _: Int ->
                if (todoName.text.isNotEmpty()) {
                    addItem(todoName.text.toString())
                }
            }
            dialog.setNegativeButton(R.string.cancel_button) { _: DialogInterface, _: Int ->
            }
            dialog.show()
        }
    }

    override fun onResume() {
        refreshList()
        super.onResume()
    }

    /**
     * Adds a new [ToDoItem] and refreshes the list.
     */
    private fun addItem(name: String) {
        val item = ToDoItem()
        item.toDoId = todoId
        item.itemName = name
        item.watched = false

        dbHandler.addTodoItem(item)
        refreshList()
    }

    /**
     * Populates the [RecyclerView] with newly inserted items.
     */
    private fun refreshList() {
        rv_item.adapter = ItemAdapter(this, dbHandler, dbHandler.getTodoItems(todoId))
    }

    /**
     * Binds data to [RecyclerView]
     */
    class ItemAdapter(val context: Context, val dbHandler: DbHandler, val list: MutableList<ToDoItem>) :
        RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val itemName: CheckBox = view.findViewById(R.id.chbox_item)
        }

        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(context).inflate(R.layout.rv_child_item, viewGroup, false))
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemName.text = list[position].itemName
            viewHolder.itemName.isChecked = list[position].watched
            viewHolder.itemName.setOnClickListener {
                list[position].watched = !list[position].watched
                dbHandler.updateTodoItem(list[position])
            }
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
