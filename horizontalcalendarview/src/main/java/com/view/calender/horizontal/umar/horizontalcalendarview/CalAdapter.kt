package com.view.calender.horizontal.umar.horizontalcalendarview

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
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

    protected open fun getViewHolder(itemView: View): T {
        return MyViewHolder(itemView) as T
    }

    override fun onBindViewHolder(holder: T, position: Int) {
        val t = getWeekDayName(position)
        holder.day.setTextColor(context.resources.getColor(color))
        holder.date.setTextColor(context.resources.getColor(color))
        if (dayModelList[position].isToday!!) {
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

        holder.day.setText(t!! + " ")
        holder.date.setText(dayModelList[position].date)
        holder.itemView.setTag(position)
        dateArrayList.add(holder.date)
        dayArrayList.add(holder.day)
        dividerArrayList.add(holder.divider)
        holder.divider.setBackgroundColor(context.resources.getColor(color))
        holder.itemView.setOnClickListener(View.OnClickListener { v ->
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
        })
    }

    protected open fun updateSelectedItemUI(root: View) {
        val date = root.findViewById<TextView>(R.id.date)
        if (clickedTextView == null) {
            clickedTextView = date
            clickedTextView!!.background = context.resources.getDrawable(R.drawable.background_selected_day)
            clickedTextView!!.setTextColor(context.resources.getColor(R.color.white))
            clickedTextView!!.setTypeface(clickedTextView!!.typeface, Typeface.NORMAL)
        } else {
            //                    if(!dayModelList.get(pos).isToday) {
            if (lastDaySelected != null && lastDaySelected!!.isToday!!) {
                clickedTextView!!.background = context.resources.getDrawable(R.drawable.currect_date_background)
                clickedTextView!!.setTextColor(context.resources.getColor(R.color.white))
                clickedTextView!!.setTypeface(clickedTextView!!.typeface, Typeface.NORMAL)
            } else {
                clickedTextView!!.background = null
                clickedTextView!!.setTextColor(context.resources.getColor(R.color.grayTextColor))
                clickedTextView!!.setTypeface(clickedTextView!!.typeface, Typeface.NORMAL)
            }
            clickedTextView = date
            clickedTextView!!.background = context.resources.getDrawable(R.drawable.background_selected_day)
            clickedTextView!!.setTextColor(context.resources.getColor(R.color.white))
            clickedTextView!!.setTypeface(clickedTextView!!.typeface, Typeface.NORMAL)
        }
    }

    private fun getWeekDayName(position: Int): String? {
        val day = dayModelList[position].day
        when (weekMode) {
            CalAdapter.WeekNameMode.SHORT -> return day!!.substring(0, 1)
            CalAdapter.WeekNameMode.MEDIUM -> return day
            else ->
                //never happens
                return null
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
            dayArrayList[i].setTextColor(context.resources.getColor(color))
            dateArrayList[i].setTextColor(context.resources.getColor(color))
            dividerArrayList[i].setBackgroundColor(context.resources.getColor(color))
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

    open class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var day: TextView
        var date: TextView
        var background: View
        var divider: View
        var root: View? = null

        init {
            day = view.findViewById(R.id.day)
            date = view.findViewById(R.id.date)
            divider = view.findViewById(R.id.divider)
            background = date
        }

        protected open fun setBackgroundColor(drawable: Drawable) {
            background.background = drawable
        }
    }
}
