package com.view.calender.horizontal.umar.horizontalcalendarview

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import java.lang.reflect.InvocationTargetException
import java.util.ArrayList

/**
 * Created by UManzoor on 11/28/2017.
 * Modified by Nethermane 04/07/2019
 */

open class CalAdapter<T : CalAdapter.MyViewHolder>(protected var context: Context, private val dayModelList: ArrayList<DayDateMonthYearModel>) : RecyclerView.Adapter<T>() {
    protected var itemWidthPx: Int = 0
    protected var lastDaySelected: DayDateMonthYearModel? = null
    protected var color = R.color.black
    private var toCallBack: HorizontalCalendarListener? = null
    private var clickedTextView: TextView? = null
    private val dateArrayList = ArrayList<TextView>()
    private val dayArrayList = ArrayList<TextView>()
    private val dividerArrayList = ArrayList<View>()
    private var weekMode = WeekNameMode.SHORT

    protected open val customLayout: Int
        get() = R.layout.custom_day_layout

    fun setCallback(toCallBack: HorizontalCalendarListener) {
        this.toCallBack = toCallBack
    }

    fun setItemWidth(widthPx: Int) {
        this.itemWidthPx = widthPx
    }

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
        if (dayModelList[position].isToday) {
            updateSelectedItemUI(holder.itemView)
            lastDaySelected = dayModelList[position]
            try {
                val cb = CallBack(toCallBack!!, "newDateSelected")
                cb.invoke(dayModelList[position])
            } catch (e: InvocationTargetException) {
                //non
            } catch (e: IllegalAccessException) {
            } catch (e: NoSuchMethodException) {
            }

        }

        holder.day.text = context.getString(R.string.add_space_to_end, t)
        holder.date.text = dayModelList[position].date
        holder.itemView.tag = position
        dateArrayList.add(holder.date)
        dayArrayList.add(holder.day)
        dividerArrayList.add(holder.divider)
        holder.divider.setBackgroundColor(ContextCompat.getColor(context, color))
        holder.itemView.setOnClickListener { v ->
            val pos = Integer.valueOf(v.tag.toString())
            updateSelectedItemUI(v)

            try {
                val cb = CallBack(toCallBack!!, "newDateSelected")
                cb.invoke(dayModelList[pos])
            } catch (e: InvocationTargetException) {
                //non
            } catch (e: IllegalAccessException) {
            } catch (e: NoSuchMethodException) {
            }

            lastDaySelected = dayModelList[pos]
        }
    }

    protected open fun updateSelectedItemUI(root: View) {
        val date = root.findViewById<TextView>(R.id.date)
        if (clickedTextView == null) {
            clickedTextView = date
            clickedTextView!!.background = ContextCompat.getDrawable(context, R.drawable.background_selected_day)
            clickedTextView!!.setTextColor(ContextCompat.getColor(context, R.color.white))
            clickedTextView!!.setTypeface(clickedTextView!!.typeface, Typeface.NORMAL)
        } else {
            //                    if(!dayModelList.get(pos).isToday) {
            if (lastDaySelected != null && lastDaySelected!!.isToday) {
                clickedTextView!!.background = ContextCompat.getDrawable(context, R.drawable.currect_date_background)
                clickedTextView!!.setTextColor(ContextCompat.getColor(context, R.color.white))
                clickedTextView!!.setTypeface(clickedTextView!!.typeface, Typeface.NORMAL)
            } else {
                clickedTextView!!.background = null
                clickedTextView!!.setTextColor(ContextCompat.getColor(context, R.color.grayTextColor))
                clickedTextView!!.setTypeface(clickedTextView!!.typeface, Typeface.NORMAL)
            }
            clickedTextView = date
            clickedTextView!!.background = ContextCompat.getDrawable(context, R.drawable.background_selected_day)
            clickedTextView!!.setTextColor(ContextCompat.getColor(context, R.color.white))
            clickedTextView!!.setTypeface(clickedTextView!!.typeface, Typeface.NORMAL)
        }
    }

    private fun getWeekDayName(position: Int): String? {
        val day = dayModelList[position].day
        return when (weekMode) {
            WeekNameMode.SHORT -> day.substring(0, 1)
            WeekNameMode.MEDIUM -> day
        }
    }

    override fun getItemCount(): Int {
        return dayModelList.size
    }

    fun add(DDMYModel: DayDateMonthYearModel) {
        dayModelList.add(DDMYModel)
        notifyItemInserted(dayModelList.size - 1)
    }

    override fun onViewAttachedToWindow(holder: T) {
        holder.setIsRecyclable(false)
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: T) {
        holder.setIsRecyclable(false)
        super.onViewDetachedFromWindow(holder)
    }

    fun changeAccent(color: Int) {
        this.color = color
        for (i in dateArrayList.indices) {
            dayArrayList[i].setTextColor(ContextCompat.getColor(context, color))
            dateArrayList[i].setTextColor(ContextCompat.getColor(context, color))
            dividerArrayList[i].setBackgroundColor(ContextCompat.getColor(context, color))
        }
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
        SHORT, MEDIUM
    }

    open class MyViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        var day: TextView = root.findViewById(R.id.day)
        var date: TextView = root.findViewById(R.id.date)
        var background: View
        var divider: View = root.findViewById(R.id.divider)

        init {
            background = date
        }

        protected open fun setBackgroundColor(drawable: Drawable) {
            background.background = drawable
        }
    }
}
