package com.kt.apps.xembongda.ui.worldcup.adapter

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.kt.apps.xembongda.R

class ColumnHeaderViewHolder(view: View) : AbstractViewHolder(view) {
    private val headerTitle: TextView by lazy { view.findViewById(R.id.column_header_textView) }
    fun bindData(model: IColumnHeaderModel?) {
        itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, com.kt.apps.xembongda.base.R.color.itemBackgroundColor))
        headerTitle.text = model?.title
    }
}