package com.kt.apps.xembongda.base.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T, VB : ViewDataBinding> : RecyclerView.Adapter<BaseViewHolder<T, VB>>() {
    abstract val itemLayoutRes: Int
    open lateinit var onItemRecyclerViewCLickListener: OnItemRecyclerViewCLickListener<T>
    private val _listItem by lazy { mutableListOf<T>() }
    var currentSelectedItem: T? = null
        get() = field
        set(value) {
            val oldPosition = _listItem.indexOf(field)
            val newPosition = _listItem.indexOf(value)
            field = value
            notifyItemChanged(oldPosition)
            notifyItemChanged(newPosition)
        }

    val isNextAvailable: Boolean
        get() = currentSelectedItem?.let {
            val index = _listItem.indexOf(it) + 1
            index < _listItem.size && _listItem.size > 1
        } ?: false

    val isPreviousAvailable: Boolean
        get() = currentSelectedItem?.let {
            val index = _listItem.indexOf(it) - 1
            index >= 0 && _listItem.size > 1
        } ?: false

    val nextItem: T?
        get() = if (currentSelectedItem != null) {
            val index = _listItem.indexOf(currentSelectedItem!!) + 1
            currentSelectedItem = if (index < _listItem.size) {
                _listItem[index]
            } else {
                _listItem[0]
            }
            currentSelectedItem!!
        } else null
    val previousItem: T?
        get() = if (currentSelectedItem != null) {
            val index = _listItem.indexOf(currentSelectedItem!!) - 1
            currentSelectedItem = if (index >= 0) {
                _listItem[index]
            } else {
                null
            }
            currentSelectedItem!!
        } else null

    val listItem: List<T>
        get() = _listItem

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T, VB> {
        val view = LayoutInflater.from(parent.context).inflate(itemLayoutRes, parent, false)
        val viewBinding: VB = DataBindingUtil.bind(view)!!
        return object : BaseViewHolder<T, VB>(viewBinding) {
            override fun onBind(item: T, position: Int) {
                bindItem(item, viewBinding)
            }

        }
    }

    abstract fun bindItem(item: T, binding: VB)

    open fun onRefresh(items: List<T>, notifyDataSetChange: Boolean = true) {
        _listItem.clear()
        _listItem.addAll(items)
        if (notifyDataSetChange) {
            notifyDataSetChanged()
        }
    }

    open fun onAdd(item: T) {
        _listItem.add(item)
        notifyItemInserted(_listItem.size)
    }

    open fun onAdd(position: Int, item: T) {
        _listItem.add(0, item)
        notifyItemInserted(position)
    }

    open fun onAdd(items: List<T>) {
        _listItem.addAll(items)
        notifyItemRangeInserted(_listItem.size, items.size)
    }

    open fun onDelete(item: T) {
        val index = _listItem.indexOf(item)
        _listItem.removeAt(index)
        notifyItemRemoved(index)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T, VB>, position: Int) {
        holder.bindItem(_listItem[position], if (this::onItemRecyclerViewCLickListener.isInitialized) onItemRecyclerViewCLickListener else null)
    }

    override fun getItemCount(): Int = _listItem.size


}

abstract class BaseViewHolder<T, VB : ViewDataBinding>(viewBinding: VB) : RecyclerView.ViewHolder(viewBinding.root) {
    fun bindItem(item: T, itemClickListener: OnItemRecyclerViewCLickListener<T>? = null) {
        onBind(item, adapterPosition)
        itemView.setOnClickListener {
            itemClickListener?.invoke(item, adapterPosition)
        }
    }

    abstract fun onBind(item: T, position: Int)
}

abstract class BaseAdsViewHolder<VB : ViewDataBinding>(viewBinding: VB) : RecyclerView.ViewHolder(viewBinding.root) {
    abstract fun onBind()
}
typealias OnItemRecyclerViewCLickListener<T> = (item: T, position: Int) -> Unit