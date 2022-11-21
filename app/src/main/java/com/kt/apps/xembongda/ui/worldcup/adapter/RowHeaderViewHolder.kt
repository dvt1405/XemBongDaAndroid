package com.kt.apps.xembongda.ui.worldcup.adapter

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.ui.worldcup.adapter.IRowHeaderModel

class RowHeaderViewHolder(view: View) : AbstractViewHolder(view) {
    private val rowHeader: TextView by lazy { view.findViewById(R.id.row_header_textview) }
    init {
        view.setBackgroundColor(ContextCompat.getColor(view.context, com.kt.apps.xembongda.base.R.color.itemBackgroundColor))
    }
    fun bindData(model: IRowHeaderModel?) {
        rowHeader.text = model?.title
    }
}