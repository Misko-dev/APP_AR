/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package cl.app.autismo_rancagua.Utilidades.CalendarView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cl.app.autismo_rancagua.R
import kotlinx.android.synthetic.main.calendar_list_item.view.*
import org.threeten.bp.Year
import org.threeten.bp.format.DateTimeFormatter

class CalendarYearAdapter(
        var listener: OnYearListInteractionListener?
) : RecyclerView.Adapter<CalendarYearAdapter.YearViewHolder>() {

    private val mItemYears = mutableListOf<Year>()

    fun showList(years: List<Year>) {
        mItemYears.clear()
        mItemYears.addAll(years)
        notifyDataSetChanged()
    }

    override fun getItemCount() = mItemYears.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YearViewHolder =
            YearViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.calendar_list_item, parent, false))

    override fun onBindViewHolder(holder: YearViewHolder, position: Int) {
        holder.bind(mItemYears[position])
    }

    inner class YearViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(year: Year) {
            itemView.text_name.text = year.format(DateTimeFormatter.ofPattern("yyyy"))
            itemView.setOnClickListener { listener?.onYearClick(year) }
        }
    }

    interface OnYearListInteractionListener {
        fun onYearClick(year: Year)
    }
}