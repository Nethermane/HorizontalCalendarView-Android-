package com.view.calender.horizontal.umar.horizontalcalendarview

import android.content.Context
import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
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


    private lateinit var leftImage: ImageView
    private lateinit var rightImage: ImageView
    private lateinit var mRootView: View
    private var mContext: Context
    lateinit var recyclerView: RecyclerView
    private var calAdapter: CalAdapter<*>? = null
    lateinit var currentDayModelList: ArrayList<DayDateMonthYearModel>
    private lateinit var horizontalPaginationScroller: HorizontalPaginationScroller
    private lateinit var cal: Calendar
    private lateinit var calPrevious: Calendar
    private lateinit var dateFormat: DateFormat
    private lateinit var date: Date
    //internal var datePrevious: Date? = null
    lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var mainBackground: ConstraintLayout
    lateinit var toCallBack: HorizontalCalendarListener
    private var isLoading = false
    private var changedToMaterial = false
    private var materialColor: Int = Color.parseColor("#00ff00")
    private var singleItemWidth = 0
    private var mode: CalAdapter.WeekNameMode = CalAdapter.WeekNameMode.SHORT
    private lateinit var materialSizeStyle: MaterialCalAdapter.Companion.MaterialSizeStyle
    private var paddingBetweenElements = 10
    private var todayDateModel: DayDateMonthYearModel? = null
    constructor(mContext: Context) : super(mContext) {
        this.mContext = mContext
        init()
    }

    constructor(mContext: Context, attrs: AttributeSet?) : super(mContext, attrs) {
        this.mContext = mContext
        init()
    }

    private fun init() {
        mRootView = View.inflate(mContext, R.layout.custom_calender_layout, this)
        recyclerView = findViewById(R.id.recycler_view)
        mainBackground = findViewById(R.id.main_background)
        leftImage = findViewById(R.id.left_image_view)
        rightImage = findViewById(R.id.right_image_view)
        loadNextPage()
        leftImage.setOnClickListener {
            //                Toast.makeText(mContext, "left but", Toast.LENGTH_SHORT).show();
            //                if (linearLayoutManager.findFirstVisibleItemPosition())
            recyclerView.smoothScrollToPosition(linearLayoutManager.findFirstVisibleItemPosition() - 3)
        }
        rightImage.setOnClickListener {
            //                Toast.makeText(mContext, "right but", Toast.LENGTH_SHORT).show();
            recyclerView.smoothScrollToPosition(linearLayoutManager.findLastVisibleItemPosition() + 3)
        }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastVisibleIndex = linearLayoutManager.findLastVisibleItemPosition()
                val firstVisibleIndex = linearLayoutManager.findFirstVisibleItemPosition()
                if (dx > 0) {
                    for (i in firstVisibleIndex until lastVisibleIndex) {

                        if (currentDayModelList[i].month.compareTo(currentDayModelList[i + 1].month) != 0) {
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

                        if (currentDayModelList[i].month.compareTo(currentDayModelList[i + 1].month) != 0) {
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
            dateFormat = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                SimpleDateFormat("MMMM-EEE-yyyy-MM-dd", mContext.resources.configuration.locales[0])
            } else {
                @Suppress("DEPRECATION")
                SimpleDateFormat("MMMM-EEE-yyyy-MM-dd", mContext.resources.configuration.locale)
            }
            date = Date()
            cal = Calendar.getInstance()
            //System.out.println("Day 1"+" "+dateFormat.format(date));
            val currentDayModel = DayDateMonthYearModel(cal.timeInMillis, true)
            todayDateModel = currentDayModel
            calPrevious = Calendar.getInstance()
            cal = Calendar.getInstance()
            cal.time = date
            calPrevious.time = date


            for (i in 0..29) {
                calPrevious.add(Calendar.DAY_OF_WEEK, -1)
                val previousDayModel = DayDateMonthYearModel(calPrevious.timeInMillis)
                isLoading = false
                //                calAdapter.addPrevious(currentDayMode);
                currentDayModelList.add(0, previousDayModel)
            }
            currentDayModelList.add(currentDayModel)

            //SimpleDateFormat sdf = new SimpleDateFormat("MMM EEE yyyy-MM-dd");
            for (i in 0..29) {
                cal.add(Calendar.DAY_OF_WEEK, 1)
                val nextDayModel = DayDateMonthYearModel(cal.timeInMillis)
                currentDayModelList.add(nextDayModel)
            }
            calAdapter = newCalAdapter()
            linearLayoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.layoutManager = linearLayoutManager
            recyclerView.itemAnimator = null
            recyclerView.adapter = calAdapter
            LinearSnapHelper().attachToRecyclerView(recyclerView)
            horizontalPaginationScroller = object : HorizontalPaginationScroller(linearLayoutManager) {

                override val isLoading: Boolean
                    get() = this@HorizontalCalendarView.isLoading

                override fun loadMoreItems() {
                    this@HorizontalCalendarView.isLoading = true
                    loadNextPage()
                }

                override fun loadMoreItemsOnLeft() {
                    //                        isLoading = true;
                    //                        Toast.makeText(mContext, "Reached Left", Toast.LENGTH_SHORT).show();
                    //                        loadPreviousPage( );
                }
            }
            recyclerView.addOnScrollListener(horizontalPaginationScroller)
        } else {
            for (i in 0..29) {
                cal.add(Calendar.DAY_OF_WEEK, 1)
                val nextDayModel = DayDateMonthYearModel(cal.timeInMillis)
                isLoading = false
                calAdapter!!.add(nextDayModel)
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
        val res = if (changedToMaterial) MaterialCalAdapter(mContext, currentDayModelList, materialColor, materialSizeStyle) else CalAdapter<CalAdapter.MyViewHolder>(mContext, currentDayModelList)
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
                            singleItemWidth = recyclerView.width / 7 - paddingBetweenElements
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
        res.itemWidthPx = singleItemWidth
        res.paddingBetweenElements = this.paddingBetweenElements
    }

    override fun setBackgroundColor(color: Int) {
        mainBackground.setBackgroundColor(color)
    }

    fun setControlTint(color: Int) {
        rightImage.setColorFilter(ContextCompat.getColor(mContext, color), android.graphics.PorterDuff.Mode.SRC_IN)
        leftImage.setColorFilter(ContextCompat.getColor(mContext, color), android.graphics.PorterDuff.Mode.SRC_IN)
    }

    fun showControls(show: Boolean) {
        if (show) {
            leftImage.visibility = View.VISIBLE
            rightImage.visibility = View.VISIBLE


        } else {
            leftImage.visibility = View.GONE
            rightImage.visibility = View.GONE
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
        calAdapter!!.toCallBack = toCallBack
        val currentDayModel = DayDateMonthYearModel(Calendar.getInstance().timeInMillis)

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

    fun setMaterialStyle(isMaterial: Boolean, @ColorInt color: Int, materialSizeStyle: MaterialCalAdapter.Companion.MaterialSizeStyle = MaterialCalAdapter.Companion.MaterialSizeStyle.SMALL) {
        this.changedToMaterial = isMaterial
        this.materialColor = color
        this.materialSizeStyle = materialSizeStyle
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
