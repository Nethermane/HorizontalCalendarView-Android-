package com.view.calender.horizontal.umar.horizontalcalendarview.material

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView

import com.view.calender.horizontal.umar.horizontalcalendarview.CalAdapter
import com.view.calender.horizontal.umar.horizontalcalendarview.DayDateMonthYearModel
import com.view.calender.horizontal.umar.horizontalcalendarview.R

import java.util.ArrayList

/**
 * Created by UManzoor on 11/28/2017.
 * Modified by Nethermane 04/07/2019
 */

class MaterialCalAdapter(context: Context, dayModelList: ArrayList<DayDateMonthYearModel>, @ColorInt val argb: Int) : CalAdapter<MaterialCalAdapter.MaterialViewHolder>(context, dayModelList) {
    private var clickedView: Triple<View, TextView, TextView>? = null
    override val customLayout: Int
        get() = R.layout.material_custom_day_layout

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.updateWidth()
    }

    override fun updateSelectedItemUI(root: View) {
        val clicked = root.findViewById<View>(R.id.background)
        (clicked.background as GradientDrawable).setColor(argb)
        val shape = GradientDrawable()
        shape.cornerRadius = 16f
        shape.setColor(argb)
        clicked.background = shape
        val day = root.findViewById<TextView>(R.id.day)
        val date = root.findViewById<TextView>(R.id.date)
        with(clickedView) {
            if (this != null) {
                //Selecting a date from todays date being selected
                if (lastDaySelected != null && lastDaySelected!!.isToday) {
                    styleTodaysDate(this)
                } else {
                    //Selecting a date where the last selected wasn't today
                    styleNonSelectedDate(this)
                }
                //set selected date
                clickedView = Triple(clicked, date, day).also { newClickedView ->
                    styleCurrentDate(newClickedView)
                }
            } else {
                clickedView = Triple(clicked, date, day).also { newClickedView ->
                    styleCurrentDate(newClickedView)
                }

            }
        }
    }
    private fun styleCurrentDate(view: Triple<View, TextView, TextView>) {
        view.first.alpha = 1f
        view.first.visibility = View.VISIBLE
        view.second.textSize = 20f
        view.second.setTextColor(Color.WHITE)
        view.third.setTextColor(Color.WHITE)
    }
    private fun styleTodaysDate(view: Triple<View, TextView, TextView>) {
        view.first.alpha = 0.3f
        view.first.visibility = View.VISIBLE
        view.second.textSize = 20f
        view.second.setTextColor(ContextCompat.getColor(context, color))
        view.third.setTextColor(ContextCompat.getColor(context, color))
    }
    private fun styleNonSelectedDate(view: Triple<View, TextView, TextView>) {
        view.first.visibility = View.INVISIBLE
        view.second.textSize = 18f
        view.second.setTextColor(ContextCompat.getColor(context, color))
        view.third.setTextColor(ContextCompat.getColor(context, color))
    }
    override fun getViewHolder(itemView: View): MaterialViewHolder {
        return MaterialViewHolder(itemView)
    }


    inner class MaterialViewHolder(view: View) : MyViewHolder(view) {
        init {
            background = view.findViewById(R.id.background)
        }

        override fun setBackgroundColor(drawable: Drawable) {
            this.background.visibility = View.VISIBLE
        }

        fun updateWidth() {
            //to show only 7 days on screen at single time
            setNewWidth(day)
            setNewWidth(date)
            setNewWidth(background)
        }

        private fun setNewWidth(v: View) {
            if (v.measuredWidth != itemWidthPx) {
                v.layoutParams = getNewParams(v)
            }
        }

        private fun getNewParams(root: View): FrameLayout.LayoutParams {
            return FrameLayout.LayoutParams(itemWidthPx, root.layoutParams.height)
        }
    }
}
