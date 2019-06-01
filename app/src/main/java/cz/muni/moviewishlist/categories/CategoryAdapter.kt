package cz.muni.moviewishlist.categories

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import cz.muni.moviewishlist.R
import cz.muni.moviewishlist.main.INTENT_CATEGORY_ID
import cz.muni.moviewishlist.main.INTENT_CATEGORY_NAME
import cz.muni.moviewishlist.movies.MovieActivity

/**
 * Binds data to [RecyclerView]
 */
class CategoryAdapter(private val activity: CategoryActivity, private val list: MutableList<Category>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryName: TextView = view.findViewById(R.id.category_name_text)
        val menu: ImageView = view.findViewById(R.id.category_menu_icon)
    }

    fun recreate(newList: MutableList<Category>) {
        this.list.clear()
        this.list.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                activity
            ).inflate(R.layout.category_view, viewGroup, false)
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