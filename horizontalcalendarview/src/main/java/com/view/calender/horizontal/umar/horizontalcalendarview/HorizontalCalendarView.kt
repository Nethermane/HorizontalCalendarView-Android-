package com.view.calender.horizontal.umar.horizontalcalendarview

import android.content.Context
import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout

import com.view.calender.horizontal.umar.horizontalcalendarview.material.MaterialCalAdapter

import java.lang.reflect.InvocationTargetException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date

class HorizontalCalendarView : LinearLayout {


    lateinit var leftButton: LinearLayout
    lateinit var rightButton: LinearLayout
    lateinit var leftImage: ImageView
    lateinit var rightImage: ImageView
    internal lateinit var rootView: View
    internal var context: Context
    lateinit var recyclerView: RecyclerView
    internal var calAdapter: CalAdapter<*>? = null
    lateinit var currentDayModelList: ArrayList<DayDateMonthYearModel>
    lateinit var horizontalPaginationScroller: HorizontalPaginationScroller
    lateinit var cal: Calendar
    lateinit var calPrevious: Calendar
    lateinit var dateFormat: DateFormat
    lateinit var date: Date
    internal var datePrevious: Date? = null
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var mainBackground: LinearLayout
    lateinit var toCallBack: HorizontalCalendarListener
    private var isLoading: Boolean = false
    private var changedToMaterial: Boolean = false
    private var materialColor: Int = Color.parseColor("#00ff00")
    private var singleItemWidth: Int = 0
    private var mode: CalAdapter.WeekNameMode = CalAdapter.WeekNameMode.SHORT

    constructor(context: Context) : super(context) {
        this.context = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.context = context
        init()
    }

    fun init() {
        rootView = View.inflate(context, R.layout.custom_calender_layout, this)
        leftButton = findViewById(R.id.swipe_left)
        rightButton = findViewById(R.id.swipe_right)
        recyclerView = findViewById(R.id.recycler_view)
        mainBackground = findViewById(R.id.main_background)
        leftImage = findViewById(R.id.left_image_view)
        rightImage = findViewById(R.id.right_image_view)
        loadNextPage()
        leftButton.setOnClickListener {
            //                Toast.makeText(context, "left but", Toast.LENGTH_SHORT).show();
            //                if (linearLayoutManager.findFirstVisibleItemPosition())
            recyclerView.smoothScrollToPosition(linearLayoutManager.findFirstVisibleItemPosition() - 3)
        }
        rightButton.setOnClickListener {
            //                Toast.makeText(context, "right but", Toast.LENGTH_SHORT).show();
            recyclerView.smoothScrollToPosition(linearLayoutManager.findLastVisibleItemPosition() + 3)
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastVisibleIndex = linearLayoutManager.findLastVisibleItemPosition()
                val firstVisibleIndex = linearLayoutManager.findFirstVisibleItemPosition()
                if (dx > 0) {
                    for (i in firstVisibleIndex until lastVisibleIndex) {

                        if (currentDayModelList[i].month!!.compareTo(currentDayModelList[i + 1].month!!) != 0) {
                            try {
                                val cb = CallBack(toCallBack, "updateMonthOnScroll")
                                cb.invoke(currentDayModelList[i + 1])
                            } catch (e: InvocationTargetException) {
                                e.printStackTrace()
                            } catch (e: IllegalAccessException) {
                                e.printStackTrace()
                            } catch (e: NoSuchMethodException) {
                                e.printStackTrace()
                            }

                        }
                    }
                } else if (dx < 0) {
                    for (i in lastVisibleIndex downTo firstVisibleIndex + 1) {

                        if (currentDayModelList[i].month!!.compareTo(currentDayModelList[i + 1].month!!) != 0) {
                            try {
                                val cb = CallBack(toCallBack, "updateMonthOnScroll")
                                cb.invoke(currentDayModelList[i])
                            } catch (e: InvocationTargetException) {
                                e.printStackTrace()
                            } catch (e: IllegalAccessException) {
                                e.printStackTrace()
                            } catch (e: NoSuchMethodException) {
                                e.printStackTrace()
                            }

                        }
                    }
                }
            }
        })

        linearLayoutManager.scrollToPosition(27)
    }

    fun loadNextPage() {
        if (changeCalAdapter()) {
            recyclerView.adapter = calAdapter
        }
        if (calAdapter == null) {
            currentDayModelList = ArrayList()
            dateFormat = SimpleDateFormat("MMMM-EEE-yyyy-MM-dd")
            date = Date()
            //System.out.println("Day 1"+" "+dateFormat.format(date));
            val currentDate = dateFormat.format(date).toString()
            val partsDate = currentDate.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val currentDayModel = DayDateMonthYearModel(partsDate[0]
                    , partsDate[4]
                    , partsDate[1]
                    , partsDate[2]
                    , partsDate[3]
                    , true)
            calPrevious = Calendar.getInstance()
            cal = Calendar.getInstance()
            cal.time = date
            calPrevious.time = date


            for (i in 0..29) {
                calPrevious.add(Calendar.DAY_OF_WEEK, -1)
                val nextDate = dateFormat.format(calPrevious.time)
                val partsNextDate = nextDate.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val previousDayMode = DayDateMonthYearModel(partsNextDate[0]
                        , partsNextDate[4]
                        , partsNextDate[1]
                        , partsNextDate[2]
                        , partsNextDate[3]
                        , false)
                isLoading = false
                //                calAdapter.addPrevious(currentDayMode);
                currentDayModelList.add(0, previousDayMode)
            }
            currentDayModelList.add(currentDayModel)

            //SimpleDateFormat sdf = new SimpleDateFormat("MMM EEE yyyy-MM-dd");
            for (i in 0..29) {
                cal.add(Calendar.DAY_OF_WEEK, 1)
                val nextDate = dateFormat.format(cal.time)
                val partsNextDate = nextDate.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val currentDayMode = DayDateMonthYearModel(partsNextDate[0]
                        , partsNextDate[4]
                        , partsNextDate[1]
                        , partsNextDate[2]
                        , partsNextDate[3]
                        , false)
                currentDayModelList.add(currentDayMode)
                calAdapter = newCalAdapter()
                linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                recyclerView.layoutManager = linearLayoutManager
                recyclerView.itemAnimator = DefaultItemAnimator()
                recyclerView.adapter = calAdapter
                horizontalPaginationScroller = object : HorizontalPaginationScroller(linearLayoutManager) {

                    override val isLoading: Boolean
                        get() = this@HorizontalCalendarView.isLoading

                    override fun loadMoreItems() {
                        this@HorizontalCalendarView.isLoading = true
                        loadNextPage()
                    }

                    override fun loadMoreItemsOnLeft() {
                        //                        isLoading = true;
                        //                        Toast.makeText(context, "Reached Left", Toast.LENGTH_SHORT).show();
                        //                        loadPreviousPage( );
                    }
                }
                recyclerView.addOnScrollListener(horizontalPaginationScroller)
            }
        } else {
            for (i in 0..29) {
                cal.add(Calendar.DAY_OF_WEEK, 1)
                val nextDate = dateFormat.format(cal.time)
                val partsNextDate = nextDate.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val currentDayMode = DayDateMonthYearModel(partsNextDate[0]
                        , partsNextDate[4]
                        , partsNextDate[1]
                        , partsNextDate[2]
                        , partsNextDate[3]
                        , false)
                isLoading = false
                calAdapter!!.add(currentDayMode)
            }
        }
    }

    private fun changeCalAdapter(): Boolean {
        var res = false
        if (calAdapter != null) {
            if (changedToMaterial) {
                calAdapter = newCalAdapter()
                res = true
            }
            calAdapter!!.setWeekMode(mode)
        }
        return res
    }

    private fun newCalAdapter(): CalAdapter<*> {
        val res = if (changedToMaterial) MaterialCalAdapter(context, currentDayModelList, materialColor) else CalAdapter<CalAdapter.MyViewHolder>(context, currentDayModelList)
        reinitCalAdapterData(res)
        changedToMaterial = false
        return res
    }

    private fun reinitCalAdapterData(res: CalAdapter<*>) {
        if (calAdapter != null) {
            calAdapter!!.reloadData(res)
        }
        setNewRecyclerItemWidth(res)
    }

    private fun setNewRecyclerItemWidth(res: CalAdapter<*>) {
        if (singleItemWidth == 0) {
            recyclerView.viewTreeObserver.addOnGlobalLayoutListener(
                    object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            singleItemWidth = recyclerView.width / 7
                            updateRecyclerItemWidth(res)
                            res.notifyDataSetChanged()
                        }
                    }
            )
        } else {
            updateRecyclerItemWidth(res)
        }
    }

    private fun updateRecyclerItemWidth(res: CalAdapter<*>) {
        res.setItemWidth(singleItemWidth)
    }

    override fun setBackgroundColor(color: Int) {
        mainBackground.setBackgroundColor(color)
    }

    fun setControlTint(color: Int) {
        rightImage.setColorFilter(ContextCompat.getColor(context, color), android.graphics.PorterDuff.Mode.SRC_IN)
        leftImage.setColorFilter(ContextCompat.getColor(context, color), android.graphics.PorterDuff.Mode.SRC_IN)
    }

    fun showControls(show: Boolean) {
        if (show) {
            leftButton.visibility = View.VISIBLE
            rightButton.visibility = View.VISIBLE
            val param = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    7.0f
            )
            val paramTwo = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.5f
            )
            paramTwo.topMargin = 20
            paramTwo.bottomMargin = 20
            leftButton.layoutParams = paramTwo
            rightButton.layoutParams = paramTwo
            recyclerView.layoutParams = param

        } else {
            leftButton.visibility = View.GONE
            rightButton.visibility = View.GONE
            val param = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
            recyclerView.layoutParams = param
        }
        askForRecalculateItemWidth()
    }

    private fun askForRecalculateItemWidth() {
        singleItemWidth = 0
        calAdapter?.let {
            setNewRecyclerItemWidth(it)
        }
    }

    fun setContext(toCallBack: HorizontalCalendarListener) {
        this.toCallBack = toCallBack
        calAdapter!!.setCallback(toCallBack)
        val date = Date()
        val currentDate = dateFormat.format(date).toString()
        val partsDate = currentDate.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val currentDayModel = DayDateMonthYearModel(partsDate[0]
                , partsDate[4]
                , partsDate[1]
                , partsDate[2]
                , partsDate[3]
                , true)

        try {
            val cb = CallBack(toCallBack, "updateMonthOnScroll")
            cb.invoke(currentDayModel)
        } catch (e: InvocationTargetException) {
        } catch (e: IllegalAccessException) {
        } catch (e: NoSuchMethodException) {
        }

    }

    fun changeAccent(color: Int) {
        calAdapter!!.changeAccent(color)
    }

    fun setMaterialStyle(isMaterial: Boolean, @ColorInt color: Int) {
        this.changedToMaterial = isMaterial
        this.materialColor = color
        loadNextPage()
    }

    fun setWeekNameMode(mode: CalAdapter.WeekNameMode) {
        this.mode = mode
        if (calAdapter != null) {
            calAdapter!!.setWeekMode(mode)
            calAdapter!!.notifyDataSetChanged()
        }
    }
}
