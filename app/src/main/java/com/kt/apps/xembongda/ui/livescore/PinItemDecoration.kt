package com.kt.apps.xembongda.ui.livescore

import android.graphics.Canvas
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener

class PinItemDecoration(
    private val _recyclerView: RecyclerView,
    private val _listener: Listener
) : RecyclerView.ItemDecoration() {
    private var _pinItemHeight: Int = 0

    init {
        _recyclerView.addOnItemTouchListener(object : OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (e.y <= _pinItemHeight) return true
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            }

        })
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val topView = parent.getChildAt(0) ?: return

        val topPosition = parent.getChildAdapterPosition(topView)
        if (topPosition == RecyclerView.NO_POSITION) return
        val currentHeaderView: View = getHeaderViewForItem(topPosition, parent)
        fixLayoutSize(parent, currentHeaderView)

        val currentBottomHeader = currentHeaderView.bottom
        val childView: View = getChildInContact(parent, currentBottomHeader) ?: return

        if (_listener.isHeader(parent.getChildAdapterPosition(childView))) {
            changeHeader(c, currentHeaderView, childView);
            return
        }

        drawHeader(c, currentHeaderView)
    }

    private fun getChildInContact(recyclerView: RecyclerView, currentBottomHeader: Int): View? {
        var childInContact: View? = null
        for (i in 0 until recyclerView.childCount) {
            val child = recyclerView.getChildAt(i)
            if (child.bottom > currentBottomHeader) {
                if (child.top <= currentBottomHeader) {
                    // This child overlaps the contactPoint
                    childInContact = child
                    break
                }
            }
        }
        return childInContact
    }


    private var oldHeaderPosition = 0
    private fun getHeaderViewForItem(topPosition: Int, parent: RecyclerView): View {
        val headerPosition = _listener.getHeaderPositionForItem(topPosition)
        val layoutRes = _listener.getHeaderLayout(headerPosition)
        val header: View = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        _listener.bindHeaderData(header, headerPosition)
        return header
    }

    private fun fixLayoutSize(recyclerView: RecyclerView, currentHeaderView: View) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(recyclerView.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(recyclerView.height, View.MeasureSpec.UNSPECIFIED)
        val childWidthSpec = ViewGroup.getChildMeasureSpec(
            widthSpec,
            recyclerView.paddingLeft + recyclerView.paddingRight,
            currentHeaderView.layoutParams.width
        )
        val childHeightSpec = ViewGroup.getChildMeasureSpec(
            heightSpec,
            recyclerView.paddingTop + recyclerView.paddingBottom,
            currentHeaderView.layoutParams.height
        )

        currentHeaderView.measure(childWidthSpec, childHeightSpec)
        _pinItemHeight = Math.max(currentHeaderView.measuredHeight, _pinItemHeight)
        currentHeaderView.layout(0, 0, currentHeaderView.measuredWidth, currentHeaderView.measuredHeight)
    }

    private fun changeHeader(canvas: Canvas, currentHeaderView: View, nextHeader: View) {
        canvas.save()
        canvas.translate(0f, (nextHeader.top - currentHeaderView.height).toFloat())
        currentHeaderView.draw(canvas)
        canvas.restore()
    }

    private fun drawHeader(canvas: Canvas, currentHeader: View) {
        canvas.save()
        canvas.translate(0f, 0f)
        currentHeader.draw(canvas)
        canvas.restore()
    }


    interface Listener {
        fun getHeaderPositionForItem(itemPosition: Int): Int

        fun getHeaderLayout(headerPosition: Int): Int

        fun bindHeaderData(header: View?, headerPosition: Int)

        fun isHeader(itemPosition: Int): Boolean
    }

}