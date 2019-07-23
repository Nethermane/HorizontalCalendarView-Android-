package com.view.calender.horizontal.umar.horizontalcalendarview

import com.view.calender.horizontal.umar.horizontalcalendarview.DayDateMonthYearModel.Companion.daysOfWeek
import com.view.calender.horizontal.umar.horizontalcalendarview.DayDateMonthYearModel.Companion.monthNames
import java.util.*


/**
 * Created by UManzoor on 11/28/2017.
 */

class DayDateMonthYearModel(
        private val timeInMillis: Long,
        var isSelected: Boolean = false
) {
    private val localDate = Calendar.getInstance().apply { timeInMillis = this@DayDateMonthYearModel.timeInMillis }
    companion object {
        val monthNames: Array<String> = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        val daysOfWeek: Array<String> = arrayOf("Sunday","Monday","Tuesday","Wednesday","Thursday", "Friday","Saturday")
    }
    val month: String
        get() = monthNames[localDate.get(Calendar.MONTH)]
    val year: String
        get() = localDate.get(Calendar.YEAR).toString()
    val date: String
        get() = localDate.get(Calendar.DAY_OF_MONTH).toString()
    val day: String
        get() = daysOfWeek[localDate.get(Calendar.DAY_OF_WEEK)-1]
    val isToday: Boolean
        get() = (this.localDate.timeInMillis / 1000 / 60 / 60 / 24).toInt() == (Calendar.getInstance().timeInMillis/ 1000 / 60 / 60 / 24).toInt()
}
