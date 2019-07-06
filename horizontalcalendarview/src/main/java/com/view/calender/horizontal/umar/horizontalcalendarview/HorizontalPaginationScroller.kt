package com.view.calender.horizontal.umar.horizontalcalendarview


import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

/**
 * Created by UManzoor on 11/28/2017.
 */

abstract class HorizontalPaginationScroller(private var layoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {

    abstract val isLoading: Boolean

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

        if (!isLoading) {
            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                loadMoreItems()
            }
            if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                loadMoreItemsOnLeft()
            }

        }
    }

    protected abstract fun loadMoreItems()

    protected abstract fun loadMoreItemsOnLeft()
}
