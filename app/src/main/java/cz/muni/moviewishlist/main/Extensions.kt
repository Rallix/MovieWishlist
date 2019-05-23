package cz.muni.moviewishlist.main

import android.graphics.Paint
import android.widget.TextView

/**
 * Sets or unsets the [TextView]'s text strike-through flag.
 */
fun TextView.setStrikeThrough(enable: Boolean) {
    if (enable) {
        this.paintFlags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    } else {
        this.paintFlags = this.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }
}