package org.alertpreparedness.platform.v2.preparedness

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_preparedness_bottom_sheet.view.ivIcon
import kotlinx.android.synthetic.main.item_preparedness_bottom_sheet.view.tvNumberIndicator
import kotlinx.android.synthetic.main.item_preparedness_bottom_sheet.view.tvTitle
import org.alertpreparedness.platform.R
import org.alertpreparedness.platform.v2.preparedness.PreparednessBottomSheetAdapter.PreparednessBottomSheetViewHolder
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption
import org.alertpreparedness.platform.v2.utils.DiffComparator
import org.alertpreparedness.platform.v2.utils.DiffListAdapter
import org.alertpreparedness.platform.v2.utils.ViewHolder
import org.alertpreparedness.platform.v2.utils.extensions.show
import org.jetbrains.anko.imageResource

class PreparednessBottomSheetAdapter(val context: Context) :
        DiffListAdapter<Pair<PreparednessBottomSheetOption, Int>, PreparednessBottomSheetViewHolder>(object :
                DiffComparator<Pair<PreparednessBottomSheetOption, Int>> {
            override fun areItemsTheSame(o1: Pair<PreparednessBottomSheetOption, Int>,
                    o2: Pair<PreparednessBottomSheetOption, Int>): Boolean {
                return o1.first == o2.first
            }

            override fun areContentsTheSame(o1: Pair<PreparednessBottomSheetOption, Int>,
                    o2: Pair<PreparednessBottomSheetOption, Int>): Boolean {
                return o1 == o2
            }
        }) {

    val listeners = mutableListOf<PreparednessBottomSheetAdapterListener>()

    fun addListener(listener: PreparednessBottomSheetAdapterListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: PreparednessBottomSheetAdapterListener) {
        listeners.remove(listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreparednessBottomSheetViewHolder {
        return PreparednessBottomSheetViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_preparedness_bottom_sheet, parent, false))
    }

    inner class PreparednessBottomSheetViewHolder(view: View) :
            ViewHolder<Pair<PreparednessBottomSheetOption, Int>>(view) {

        val ivIcon = view.ivIcon
        val tvNumberIndicator = view.tvNumberIndicator
        val tvTitle = view.tvTitle
        private lateinit var option: PreparednessBottomSheetOption

        init {
            view.setOnClickListener {
                onOptionSelected(option)
            }
        }

        override fun bind(model: Pair<PreparednessBottomSheetOption, Int>, position: Int) {
            val (option, dotNumber) = model

            ivIcon.imageResource = option.icon
            tvTitle.setText(option.string)

            tvNumberIndicator.show(dotNumber > 0)
            if (dotNumber > 0) {
                tvNumberIndicator.text = dotNumber.toString()
            }

            this.option = option
        }
    }

    private fun onOptionSelected(option: PreparednessBottomSheetOption) {
        listeners.forEach { it.onOptionSelected(option) }
    }
}

interface PreparednessBottomSheetAdapterListener {
    fun onOptionSelected(option: PreparednessBottomSheetOption)
}