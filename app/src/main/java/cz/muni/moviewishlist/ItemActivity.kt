package cz.muni.moviewishlist

import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import cz.muni.moviewishlist.database.DbHandler
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
            addItemDialog()
        }
    }

    override fun onResume() {
        refreshList()
        super.onResume()
    }

    /**
     * Creates a dialog for adding a new [ToDoItem] entry and refreshes the list.
     */
    private fun addItemDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(R.string.menu_add_title)
        val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
        val todoName = view.findViewById<EditText>(R.id.et_todo)

        dialog.setView(view)
        dialog.setPositiveButton(R.string.add_button) { _: DialogInterface, _: Int ->
            val name = todoName.text.toString().trim()
            if (TextUtils.isEmpty(name)) {
                // TODO: Prevent from being closed
                todoName.error = getString(R.string.empty_text_error)
            } else {
                val item = ToDoItem()
                item.toDoId = todoId
                item.itemName = name
                item.watched = false
                dbHandler.addTodoItem(item)
                refreshList()
            }
        }
        dialog.setNegativeButton(R.string.cancel_button) { _: DialogInterface, _: Int -> }
        dialog.show()
    }

    private fun updateItemDialog(toDoItem: ToDoItem) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(R.string.menu_edit_title)
        val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
        val todoName = view.findViewById<EditText>(R.id.et_todo)
        todoName.setText(toDoItem.itemName)
        dialog.setView(view)
        dialog.setPositiveButton(R.string.update_button) { _: DialogInterface, _: Int ->
            val name = todoName.text.toString().trim()
            if (TextUtils.isEmpty(name)) {
                // TODO: Prevent from being closed
                todoName.error = getString(R.string.empty_text_error)
            } else {
                toDoItem.toDoId = todoId
                toDoItem.itemName = name
                toDoItem.watched = false

                dbHandler.updateTodoItem(toDoItem)
                refreshList()
            }
        }
        dialog.setNegativeButton(R.string.cancel_button) { _: DialogInterface, _: Int -> }
        dialog.show()
    }

    /**
     * Populates the [RecyclerView] with newly inserted items.
     */
    private fun refreshList() {
        rv_item.adapter = ItemAdapter(this, dbHandler.getTodoItems(todoId))
    }

    /**
     * Binds data to [RecyclerView]
     */
    class ItemAdapter(val activity: ItemActivity, val list: MutableList<ToDoItem>) :
        RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val itemName : CheckBox = view.findViewById(R.id.chbox_item)
            val edit : ImageView = view.findViewById(R.id.iv_edit)
            val delete : ImageView = view.findViewById(R.id.iv_delete)
        }

        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.rv_child_item, viewGroup, false))
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            val todoItem = list[position]

            viewHolder.itemName.text = todoItem.itemName
            viewHolder.itemName.isChecked = todoItem.watched

            // Mark as watched / unwatched
            viewHolder.itemName.setOnClickListener {
                todoItem.watched = !todoItem.watched
                activity.dbHandler.updateTodoItem(todoItem)
            }

            // Edit
            viewHolder.edit.setOnClickListener {
                activity.updateItemDialog(todoItem)
            }

            // Delete
            viewHolder.delete.setOnClickListener {
                val warningDialog = AlertDialog.Builder(activity)
                warningDialog.setTitle(R.string.warning_title)
                warningDialog.setMessage(R.string.warning_delete)
                warningDialog.setNegativeButton(R.string.cancel_button) { _: DialogInterface?, _: Int -> }
                warningDialog.setPositiveButton(R.string.confirm_button) { _: DialogInterface?, _: Int ->
                    activity.dbHandler.deleteTodoItem(todoItem.id)
                    activity.refreshList()
                }
                warningDialog.show()
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
