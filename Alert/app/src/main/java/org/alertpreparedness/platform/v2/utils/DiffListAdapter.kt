package org.alertpreparedness.platform.v2.utils

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class DiffListAdapter<MODEL : Any, VIEW_HOLDER : ViewHolder<MODEL>>(
        val diffComparator: DiffComparator<MODEL>) :
        RecyclerView.Adapter<VIEW_HOLDER>() {
    val items = mutableListOf<MODEL>()

    fun updateItems(newItems: List<MODEL>) {
        val diffResult = DiffUtil.calculateDiff(DiffComparatorCallback(items, newItems, diffComparator))

        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun notifyAllItemsUpdated() {
        notifyItemRangeChanged(0, items.size)
    }

    override fun onBindViewHolder(holder: VIEW_HOLDER, position: Int) {
        onBindViewHolder(holder, position, items[position])
    }

    open fun onBindViewHolder(holder: VIEW_HOLDER, position: Int, model: MODEL){
        holder.bindInt(model, position)
    }
}

class DiffComparatorCallback<MODEL>(val oldItems: List<MODEL>, val newItems: List<MODEL>, val comparator: DiffComparator<MODEL>): DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return comparator.areItemsTheSame(oldItems[oldItemPosition], newItems[newItemPosition])
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return comparator.areContentsTheSame(oldItems[oldItemPosition], newItems[newItemPosition])
    }

    override fun getOldListSize(): Int {
        return oldItems.size
    }

    override fun getNewListSize(): Int {
        return newItems.size
    }
}

interface DiffComparator<MODEL> {
    fun areItemsTheSame(o1: MODEL, o2: MODEL): Boolean
    fun areContentsTheSame(o1: MODEL, o2: MODEL): Boolean
}

abstract class ViewHolder<MODEL : Any>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    lateinit var model: MODEL

    fun bindInt(model: MODEL, position: Int) {
        this.model = model
        bind(model, position)
    }
    abstract fun bind(model: MODEL, position: Int)
}
