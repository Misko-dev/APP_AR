
package cl.app.autismo_rancagua.Utilidades.CalendarView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cl.app.autismo_rancagua.R
import kotlinx.android.synthetic.main.calendar_list_item.view.*
import org.threeten.bp.Month
import org.threeten.bp.format.TextStyle
import java.util.Locale

class CalendarMonthAdapter(
        var listener: OnMonthListInteractionListener?
) : RecyclerView.Adapter<CalendarMonthAdapter.MonthViewHolder>() {

    private val mMonths = mutableListOf<Month>()

    fun showList(itemMonths: List<Month>) {
        mMonths.clear()
        mMonths.addAll(itemMonths)
        notifyDataSetChanged()
    }

    override fun getItemCount() = mMonths.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder =
            MonthViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.calendar_list_item, parent, false))

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        holder.bind(mMonths[position])
    }

    inner class MonthViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(month: Month) {
            itemView.text_name.text = month.getDisplayName(TextStyle.FULL, Locale.getDefault())
            itemView.setOnClickListener { listener?.onMonthClick(month) }
        }
    }

    interface OnMonthListInteractionListener {
        fun onMonthClick(month: Month)
    }
}