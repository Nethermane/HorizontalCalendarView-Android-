package com.view.calender.horizontal.umar.horizontalcalendarview.material

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.view.calender.horizontal.umar.horizontalcalendarview.*

import java.util.ArrayList

/**
 * Created by UManzoor on 11/28/2017.
 * Modified by Nethermane 04/07/2019
 */

class MaterialCalAdapter(context: Context, dayModelList: ArrayList<DayDateMonthYearModel>, @ColorInt val argb: Int, private val materialSizeStyle: MaterialSizeStyle = MaterialSizeStyle.SMALL) : CalAdapter<MaterialCalAdapter.MaterialViewHolder>(context, dayModelList) {
    companion object {
        enum class MaterialSizeStyle {
            MINI, SMALL, NORMAL
        }
    }

    private var clickedView: Triple<View, TextView, TextView>? = null
    override val customLayout: Int
        get() = when (materialSizeStyle) {
            MaterialSizeStyle.MINI -> R.layout.mini_custom_day_layout
            MaterialSizeStyle.SMALL -> R.layout.small_custom_day_layout
            MaterialSizeStyle.NORMAL -> R.layout.small_custom_day_layout
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val holder = super.onCreateViewHolder(parent, viewType)
        (holder.background.background as GradientDrawable).setColor(argb)
        return holder
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.updateWidth()
    }

    override fun getViewHolder(itemView: View): MaterialViewHolder {
        return MaterialViewHolder(itemView)
    }


    override fun styleCurrentDate(holder: MaterialViewHolder) {
        with(holder) {
            background.alpha = 1f
            background.visibility = View.VISIBLE
            date.textSize = 20f
            if (materialSizeStyle == MaterialSizeStyle.MINI) {
                day.setTextColor(Color.BLACK)
                date.setTextColor(Color.WHITE)
            } else {
                day.setTextColor(Color.WHITE)
                date.setTextColor(Color.WHITE)
            }
        }
    }

    override fun styleTodaysDate(holder: MaterialViewHolder) {
        with(holder) {
            background.alpha = 0.3f
            background.visibility = View.VISIBLE
            date.textSize = 20f
            date.setTextColor(ContextCompat.getColor(context, color))
            day.setTextColor(ContextCompat.getColor(context, color))
        }
    }

    override fun styleNonSelectedDate(holder: MaterialViewHolder) {
        with(holder) {
            background.visibility = View.INVISIBLE
            date.textSize = 18f
            date.setTextColor(ContextCompat.getColor(context, color))
            day.setTextColor(ContextCompat.getColor(context, color))
        }
    }


    inner class MaterialViewHolder(view: View) : MyViewHolder(view) {
        val background: LinearLayout = view.findViewById(R.id.background)


        fun updateWidth() {
            //to show only 7 days on screen at single time
            itemView.setPadding(paddingBetweenElements / 2, 0, paddingBetweenElements / 2, 0)
            if (materialSizeStyle == MaterialSizeStyle.MINI) {
                setNewWidthParent(itemView)
                //setNewSizeSquare(background)
            } else {
                setNewWidth(day)
                setNewWidth(date)
                setNewWidth(background)
            }
        }

        private fun setNewWidth(v: View) {
            if (v.measuredWidth != itemWidthPx) {
                v.layoutParams = getNewParams(v)
            }
        }

        private fun setNewWidthParent(v: View) {
            if (v.measuredWidth != itemWidthPx) {
                v.layoutParams = getNewRecylerViewChildParams(v)
            }
        }

        private fun setNewSizeSquare(v: View) {
            if (v.measuredWidth != itemWidthPx || v.measuredHeight != itemWidthPx) {
                v.layoutParams = getNewSquareParams()
            }
        }

        private fun getNewParams(root: View): FrameLayout.LayoutParams {
            return FrameLayout.LayoutParams(itemWidthPx + paddingBetweenElements / 2, root.layoutParams.height)
        }

        private fun getNewRecylerViewChildParams(root: View): RecyclerView.LayoutParams {
            return RecyclerView.LayoutParams(itemWidthPx + paddingBetweenElements / 2, root.layoutParams.height)
        }

        private fun getNewSquareParams(): FrameLayout.LayoutParams {
            return FrameLayout.LayoutParams(itemWidthPx, itemWidthPx)
        }
    }
}
