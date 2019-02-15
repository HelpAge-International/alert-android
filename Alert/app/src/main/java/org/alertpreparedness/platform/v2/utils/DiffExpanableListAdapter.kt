package org.alertpreparedness.platform.v2.utils

import android.os.Build.VERSION_CODES.P
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/** DEV NOTE: This was done for quickness, needs reworking completely/library that does something similar
 * Handles animations between group transitions and dynamically adding items*/
abstract class DiffGroupAdapter<GROUP : Any, ITEM : Any, VIEW_HOLDER_GROUP : DiffGroupAdapter<GROUP, ITEM, VIEW_HOLDER_GROUP, VIEW_HOLDER_ITEM>.ViewHolderGroup, VIEW_HOLDER_ITEM : DiffGroupAdapter<GROUP, ITEM, VIEW_HOLDER_GROUP, VIEW_HOLDER_ITEM>.ViewHolderItem>(
        val diffComparatorGroup: DiffGroupComparator<GROUP, ITEM>,
        val groupClass: Class<GROUP>,
        val itemClass: Class<ITEM>
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM: Int = 0
    private val VIEW_TYPE_GROUP: Int = 1


    val items = linkedMapOf<GROUP, List<ITEM>>()
    val expanded = mutableSetOf<GROUP>()

    fun updateItems(newItems: LinkedHashMap<GROUP, List<ITEM>>) {
        val oldFlattened = getFlattened(items)
        val newFlattened = getFlattened(newItems)

        val diffResult = DiffUtil.calculateDiff(DiffGroupComparatorCallback(oldFlattened, newFlattened))

        items.clear()
        items.putAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    private fun getFlattened(items: LinkedHashMap<GROUP, List<ITEM>> = this.items): List<Any> {
        return items.flatMap { (group, content) ->
            listOf(group) + (if (expanded.any { diffComparatorGroup.areGroupsTheSame(it, group) }) content else emptyList())
        }
    }

    override fun getItemCount(): Int {
        return getFlattened().size
    }

    fun notifyAllItemsUpdated() {
        notifyItemRangeChanged(0, itemCount)
    }

    fun isExpanded(group: GROUP): Boolean {
        return expanded.any { diffComparatorGroup.areGroupsTheSame(it, group) }
    }

    fun toggleGroup(group: GROUP): Boolean {
        val shouldExpand = !expanded.any { diffComparatorGroup.areGroupsTheSame(it, group) }
        val oldList = getFlattened()

        if (shouldExpand) {
            expanded.removeAll { diffComparatorGroup.areGroupsTheSame(it, group) }
            expanded.add(group)
        } else {
            expanded.removeAll { diffComparatorGroup.areGroupsTheSame(it, group) }
        }

        DiffUtil.calculateDiff(DiffGroupComparatorCallback(oldList, getFlattened())).dispatchUpdatesTo(this)

        return shouldExpand
    }

    override fun getItemViewType(position: Int): Int {
        val item = getFlattened()[position]
        return when (item::class.java) {
            groupClass -> VIEW_TYPE_GROUP
            else -> VIEW_TYPE_ITEM
        }
    }

    abstract inner class ViewHolderGroup(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var model: GROUP

        abstract fun bind(model: GROUP, position: Int)
        fun intBind(model: GROUP, position: Int) {
            this.model = model
            bind(model, position)
        }

        fun toggle() {
            if (toggleGroup(model)) {
                expanded()
            } else {
                collapsed()
            }
        }

        fun isExpanded(): Boolean {
            return isExpanded(model)
        }

        open fun expanded() {}

        open fun collapsed() {}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_GROUP -> onCreateGroupViewHolder(parent)
            else -> onCreateItemViewHolder(parent)
        }
    }
    abstract fun onCreateGroupViewHolder(parent: ViewGroup): VIEW_HOLDER_GROUP
    abstract fun onCreateItemViewHolder(parent: ViewGroup): VIEW_HOLDER_ITEM


    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        val item = getFlattened()[position]

        when(viewType) {
            VIEW_TYPE_GROUP -> {
                (holder as VIEW_HOLDER_GROUP).intBind(item as GROUP, position)
            }
            else -> {
                (holder as VIEW_HOLDER_ITEM).intBind(item as ITEM, position)
            }
        }
    }


    abstract inner class ViewHolderItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var model: ITEM

        abstract fun bind(model: ITEM, position: Int)
        fun intBind(model: ITEM, position: Int) {
            this.model = model
            bind(model, position)
        }
    }

    @Suppress("UNCHECKED_CAST")
    inner class DiffGroupComparatorCallback(val oldItems: List<Any>, val newItems: List<Any>) : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]

            return when {
                oldItem::class.java == groupClass && newItem::class.java == groupClass -> {
                    diffComparatorGroup.areGroupsTheSame(oldItem as GROUP, newItem as GROUP)
                }
                oldItem::class.java == itemClass && newItem::class.java == itemClass -> {
                    diffComparatorGroup.areItemsTheSame(oldItem as ITEM, newItem as ITEM)
                }
                else -> false
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]

            return when {
                oldItem::class.java == groupClass && newItem::class.java == groupClass -> {
                    diffComparatorGroup.areGroupContentsTheSame(oldItem as GROUP, newItem as GROUP)
                }
                oldItem::class.java == itemClass && newItem::class.java == itemClass -> {
                    diffComparatorGroup.areItemContentsTheSame(oldItem as ITEM, newItem as ITEM)
                }
                else -> false
            }
        }

        override fun getOldListSize(): Int {
            return oldItems.size
        }

        override fun getNewListSize(): Int {
            return newItems.size
        }
    }
}

interface DiffGroupComparator<GROUP, ITEM> {
    fun areGroupsTheSame(o1: GROUP, o2: GROUP): Boolean
    fun areGroupContentsTheSame(o1: GROUP, o2: GROUP): Boolean
    fun areItemsTheSame(o1: ITEM, o2: ITEM): Boolean
    fun areItemContentsTheSame(o1: ITEM, o2: ITEM): Boolean
}

