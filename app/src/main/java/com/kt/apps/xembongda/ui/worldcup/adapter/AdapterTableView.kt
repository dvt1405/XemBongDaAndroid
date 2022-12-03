package com.kt.apps.xembongda.ui.worldcup.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.ui.worldcup.model.EuroFootballMatchItem

class AdapterTableView() : AbstractTableAdapter<IColumnHeaderModel, IRowHeaderModel, ICellModel>() {

    init {

    }

    override fun onCreateCellViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.table_view_cell_layout, parent, false)
        return CellHolder(view)
    }

    override fun onCreateColumnHeaderViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.table_view_column_header_layout, parent, false)
        return ColumnHeaderViewHolder(view)
    }


    override fun onCreateRowHeaderViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.table_view_row_header_layout, parent, false)
        return RowHeaderViewHolder(view)
    }


    override fun onCreateCornerView(parent: ViewGroup): View {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.table_view_corner_layout, parent, false)
        return view
    }

    override fun onBindCellViewHolder(holder: AbstractViewHolder, cellItemModel: ICellModel?, columnPosition: Int, rowPosition: Int) {
        (holder as CellHolder).bindData(cellItemModel)
    }

    override fun onBindColumnHeaderViewHolder(holder: AbstractViewHolder, columnHeaderItemModel: IColumnHeaderModel?, columnPosition: Int) {
        (holder as ColumnHeaderViewHolder).bindData(columnHeaderItemModel)

    }

    override fun onBindRowHeaderViewHolder(holder: AbstractViewHolder, rowHeaderItemModel: IRowHeaderModel?, rowPosition: Int) {
        (holder as RowHeaderViewHolder).bindData(rowHeaderItemModel)

    }

    fun setData(footballMatchItem: EuroFootballMatchItem) {
        val listCellData = footballMatchItem.table.map { tableChart ->
            return@map tableChart.toListTableData().map {
                BaseCellData(it)
            }
        }
        val listColumnHeader = columnHeaderList.map {
            BaseColumnHeader(it)
        }
        val listRowHeader = footballMatchItem.table.map {
            BaseRowHeader(it.teamName, it.teamLogo)
        }
        setAllItems(listColumnHeader,listRowHeader,listCellData)
    }

    companion object {
        private val columnHeaderList by lazy {
            listOf("Số Trận", "Đ", "T", "H", "T", "+/-", "HS")
        }
    }

}

