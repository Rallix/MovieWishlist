package cz.muni.moviewishlist.movies

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import cz.muni.moviewishlist.R
import cz.muni.moviewishlist.main.setStrikeThrough

/**
 * Binds data to [MovieItem]'s [RecyclerView].
 */
class MovieAdapter(private val activity: MovieActivity, private val list: MutableList<MovieItem>) :
    RecyclerView.Adapter<MovieAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox : CheckBox = view.findViewById(R.id.movie_checkbox)
        val movieName : TextView = view.findViewById(R.id.movie_name_text)
        val edit : ImageView = view.findViewById(R.id.movie_icon_edit)
        val delete : ImageView = view.findViewById(R.id.movie_icon_delete)
        val move : ImageView = view.findViewById(R.id.movie_icon_move)
    }

    fun recreate(newList: MutableList<MovieItem>) {
        this.list.clear()
        this.list.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(activity).inflate(
                R.layout.movie_view,
                viewGroup,
                false
            )
        )
    }

    @SuppressLint("ClickableViewAccessibility", "PrivateResource")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val movieItem = list[position]

        viewHolder.movieName.text = movieItem.toString()
        viewHolder.checkBox.isChecked = movieItem.watched

        // Apply colour + strikethrough
        fun applyWatched() {
            viewHolder.movieName.setTextColor(
                if (!movieItem.watched) activity.getColor(R.color.primary_text_default_material_light)
                else activity.getColor(R.color.primary_text_disabled_material_light)
            )
            viewHolder.movieName.setStrikeThrough(movieItem.watched)
        }

        applyWatched()

        // Mark as watched / unwatched
        fun markWatched() {
            movieItem.watched = !movieItem.watched
            activity.dbHandler.addOrUpdateMovieItem(movieItem)
            applyWatched()
        }

        viewHolder.checkBox.setOnClickListener {
            markWatched()
        }
        viewHolder.movieName.setOnClickListener {
            markWatched()
        }

        // Edit
        viewHolder.edit.setOnClickListener {
            activity.updateMovieDialog(movieItem)
        }

        // Delete
        viewHolder.delete.setOnClickListener {
            val warningDialog = AlertDialog.Builder(activity)
            warningDialog.setTitle(R.string.warning_title)
            warningDialog.setMessage(R.string.warning_delete)
            warningDialog.setNegativeButton(R.string.cancel_button) { _: DialogInterface?, _: Int -> }
            warningDialog.setPositiveButton(R.string.confirm_button) { _: DialogInterface?, _: Int ->
                activity.dbHandler.deleteMovieItem(movieItem.id)
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