package com.view.calender.horizontal.umar.horizontalcalendarview

interface HorizontalCalendarListener {
    fun updateMonthOnScroll(selectedDate: DayDateMonthYearModel)
    fun newDateSelected(selectedDate: DayDateMonthYearModel)
}
