package com.kt.apps.xembongda.base.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import java.lang.ClassCastException


fun tryCatchCast(vararg units: () -> Unit) {
    val totalUnit = units.size - 1
    var count = 0
    while (count < totalUnit) {
        val unit = units[count]
        try {
            unit()
            break
        } catch (e: ClassCastException) {
            count++
        }
    }
}

abstract class BaseMultiItemLayoutAdapter<ITEM1, ITEM2, VB1 : ViewDataBinding, VB2 : ViewDataBinding>
    : RecyclerView.Adapter<BaseMultiItemLayoutAdapter.ViewHolder>() {
    abstract val item1Res: Int
    abstract val item2Res: Int


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return try {
            val binding: VB1 = getBinding(parent, item1Res)
            object : ViewHolder(binding) {

            }
        } catch (e: Exception) {
            val binding: VB2 = getBinding(parent, item2Res)
            object : ViewHolder(binding) {

            }
        }
    }

    fun <VB : ViewDataBinding> getBinding(parent: ViewGroup, layoutRes: Int): VB {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            layoutRes,
            parent,
            false
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getItemViewType(position: Int): Int {
        return item1Res
    }

    abstract class ViewHolder(val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

}

