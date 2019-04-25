package cz.muni.moviewishlist

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.TextUtils
import android.view.*
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import cz.muni.moviewishlist.database.DbHandler
import cz.muni.moviewishlist.database.ToDoItem
import kotlinx.android.synthetic.main.activity_item.*
import java.util.*

class ItemActivity : AppCompatActivity() {

    lateinit var dbHandler: DbHandler
    var todoId: Long = -1

    var touchHelper :ItemTouchHelper? = null
    var adapter : ItemAdapter? = null
    var list : MutableList<ToDoItem>? = null


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

        // Drag & Drop
        // TODO: Drag & Drop order is not preserved -> should be saved back to database || new row 'custom_order'
        touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
            override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
                // Swamp two items (vertically)
                val sourcePosition = p1.adapterPosition
                val targetPosition = p2.adapterPosition
                Collections.swap(list, sourcePosition, targetPosition)
                adapter?.notifyItemMoved(sourcePosition, targetPosition)
                return true
            }

            override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
                // Never called because swipeDirs = 0
            }
        })
        touchHelper?.attachToRecyclerView(rv_item)
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
        dialog.setTitle(R.string.menu_add_item_title)
        val view = layoutInflater.inflate(R.layout.dialog_child, null)
        val todoName = view.findViewById<EditText>(R.id.et_todoItem)

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

    /**
     * Creates a dialog for updating a [ToDoItem] and refreshes the list afterwards
     */
    private fun updateItemDialog(toDoItem: ToDoItem) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(R.string.menu_edit_title)
        val view = layoutInflater.inflate(R.layout.dialog_child, null)
        val todoName = view.findViewById<EditText>(R.id.et_todoItem)
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
        list = dbHandler.getTodoItems(todoId)
        adapter = ItemAdapter(this, list!!)
        rv_item.adapter = adapter
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
            val move : ImageView = view.findViewById(R.id.iv_move)
        }

        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.rv_child_item, viewGroup, false))
        }

        @SuppressLint("ClickableViewAccessibility")
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

            // Move
            viewHolder.move.setOnTouchListener { _, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    activity.touchHelper?.startDrag(viewHolder)
                }
                false
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
