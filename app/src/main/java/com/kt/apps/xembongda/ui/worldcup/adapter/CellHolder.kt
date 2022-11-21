package com.kt.apps.xembongda.ui.worldcup.adapter

import android.view.View
import android.widget.TextView
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.kt.apps.xembongda.R

class CellHolder(view: View) : AbstractViewHolder(view) {
    private val cellDataTitle: TextView by lazy { view.findViewById(R.id.cell_data) }
    fun bindData(model: ICellModel?) {
        cellDataTitle.text = model?.data
    }
}