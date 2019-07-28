package com.view.calender.horizontal.umar.horizontalcalendarview

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.mini_custom_day_layout.view.*

import java.lang.reflect.InvocationTargetException
import java.util.ArrayList

/**
 * Created by UManzoor on 11/28/2017.
 * Modified by Nethermane 04/07/2019
 */

open class CalAdapter<T : CalAdapter.MyViewHolder>(protected var context: Context, protected val dayModelList: ArrayList<DayDateMonthYearModel>) : RecyclerView.Adapter<T>() {
    var itemWidthPx: Int = 0
    var paddingBetweenElements: Int = 0
    protected var selectedIndex: Int = 0
    protected var color = R.color.black
    var toCallBack: HorizontalCalendarListener? = null
    private var weekMode = WeekNameMode.SHORT
    private var lastToday: DayDateMonthYearModel? = null


    protected open val customLayout: Int
        get() = R.layout.custom_day_layout


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): T {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(customLayout, parent, false)
        return getViewHolder(itemView)
    }

    @Suppress("UNCHECKED_CAST")
    protected open fun getViewHolder(itemView: View): T {
        return MyViewHolder(itemView) as T
    }

    override fun onBindViewHolder(holder: T, position: Int) {
        val t = getWeekDayName(position)
        holder.day.setTextColor(ContextCompat.getColor(context, color))
        holder.date.setTextColor(ContextCompat.getColor(context, color))
        holder.day.text = t
        holder.date.text = dayModelList[position].date
        holder.itemView.tag = position
        holder.itemView.setOnClickListener { v ->
            val pos = Integer.valueOf(v.tag.toString())
            lastToday?.let {
                if(!it.isToday())
                    notifyDataSetChanged()
            }

            dayModelList[selectedIndex].isSelected = false
            notifyItemChanged(selectedIndex)
            dayModelList[pos].isSelected = true
            selectedIndex = pos
            styleCurrentDate(holder)
            try {
                val cb = CallBack(toCallBack!!, "newDateSelected")
                cb.invoke(dayModelList[pos])
            } catch (e: InvocationTargetException) {
            } catch (e: IllegalAccessException) {
            } catch (e: NoSuchMethodException) {
            }
            notifyItemChanged(selectedIndex)
        }
        assignStyles(holder, position)
    }

    private fun assignStyles(holder: T, position: Int) {
        when {
            dayModelList[position].isSelected -> {
                styleCurrentDate(holder)
                selectedIndex = position
            }
            dayModelList[position].isToday() -> {
                styleTodaysDate(holder)
                lastToday = dayModelList[position]
            }
            else -> styleNonSelectedDate(holder)
        }
    }


    internal open fun styleCurrentDate(holder: T) {
        val view = holder.itemView.date
        if (view is TextView) {
            view.background = ContextCompat.getDrawable(context, R.drawable.background_selected_day)
            view.setTextColor(ContextCompat.getColor(context, R.color.white))
            view.setTypeface(view.typeface, Typeface.NORMAL)
        }
    }

    internal open fun styleTodaysDate(holder: T) {
        val view = holder.itemView.date
        if (view is TextView) {
            view.background = ContextCompat.getDrawable(context, R.drawable.currect_date_background)
            view.setTextColor(ContextCompat.getColor(context, R.color.white))
            view.setTypeface(view.typeface, Typeface.NORMAL)
        }
    }

    internal open fun styleNonSelectedDate(holder: T) {
        val view = holder.itemView.date
        if (view is TextView) {
            view.background = null
            view.setTextColor(ContextCompat.getColor(context, R.color.grayTextColor))
            view.setTypeface(view.typeface, Typeface.NORMAL)
        }
    }

    private fun getWeekDayName(position: Int): String? {
        val day = dayModelList[position].day
        return when (weekMode) {
            WeekNameMode.SHORT -> day.substring(0, 1)
            WeekNameMode.MEDIUM -> day.substring(0, 3)
            WeekNameMode.FULL -> day
        }
    }

    override fun getItemCount(): Int {
        return dayModelList.size
    }

    fun add(DDMYModel: DayDateMonthYearModel) {
        dayModelList.add(DDMYModel)
        notifyItemInserted(dayModelList.size - 1)
    }

    fun changeAccent(color: Int) {
        this.color = color
        notifyDataSetChanged()
    }

    fun reloadData(adapter: CalAdapter<*>?) {
        if (adapter != null) {
            adapter.context = this.context
            adapter.toCallBack = this.toCallBack
            adapter.changeAccent(this.color)
        }
    }

    fun setWeekMode(weekMode: WeekNameMode) {
        this.weekMode = weekMode
    }


    enum class WeekNameMode {
        SHORT, MEDIUM, FULL
    }

    open class MyViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        var day: TextView = root.findViewById(R.id.day_of_week)
        var date: TextView = root.findViewById(R.id.date)
        var divider: View = root.findViewById(R.id.divider)

    }
}
