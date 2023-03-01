package com.kt.apps.xembongda.ui.highlight

import android.graphics.drawable.AnimationDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.base.adapter.BaseAdapter
import com.kt.apps.xembongda.base.adapter.BaseViewHolder
import com.kt.apps.xembongda.databinding.ItemHighLightBinding
import com.kt.skeleton.runAnimationChangeBackground

class AdapterHighLightsNoAds : BaseAdapter<ItemHighLights.DTO, ViewDataBinding>() {
    override val itemLayoutRes: Int
        get() = R.layout.item_high_light
    var isLoading = false
    fun onLoading() {
        isLoading = true
//        notifyItemInserted(listItem.size)
    }

    override fun getItemCount(): Int {
        return if (_listItem.isEmpty()) 0 else _listItem.size + 1
    }


    override fun getItemViewType(position: Int): Int {
        return when {
            position == listItem.size -> R.layout.item_loading
            else -> itemLayoutRes
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemHighLights.DTO, ViewDataBinding>, position: Int) {
        if (position == listItem.size) {
            (holder.itemView as ViewGroup).forEach {
                if (it.background is AnimationDrawable) {
                    it.runAnimationChangeBackground()
                }
            }
        } else {
            super.onBindViewHolder(holder, position)
        }
    }

    override fun bindItem(item: ItemHighLights.DTO, binding: ViewDataBinding, position: Int) {
        try {
            if (binding is ItemHighLightBinding) {
                val dto = item.dto
                binding.item = dto
            }

        } catch (_: Exception) {
        }
    }

}