package org.alertpreparedness.platform.v1.risk_monitoring.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import org.alertpreparedness.platform.R
import org.alertpreparedness.platform.v1.risk_monitoring.model.ModelSource
import org.jetbrains.anko.find
import timber.log.Timber

/**
 * Created by fei on 09/11/2017.
 */
class SourceRVAdapter(private val sources: MutableList<ModelSource>, private val context : Context) : RecyclerView.Adapter<SourceViewHolder>() {

    private var listener: OnSourceDeleteListener? = null

    override fun getItemCount(): Int {
        return sources.size
    }

    override fun onBindViewHolder(holder: SourceViewHolder, position: Int) {
        holder?.tvSourceName?.text = sources[position].name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourceViewHolder {
        return SourceViewHolder(View.inflate(context, R.layout.source_item_view, null), listener)
    }

    fun setOnSourceDeleteListener(sourceDeleteListener: OnSourceDeleteListener) {
        listener = sourceDeleteListener
    }

}

class SourceViewHolder(itemView: View, listener: OnSourceDeleteListener?) : RecyclerView.ViewHolder(itemView) {

    val tvSourceName: TextView = itemView.find(R.id.tvSourceItemName)
    val btnRemove: Button = itemView.find(R.id.btnSourceItemRemove)
    val llSource: LinearLayout = itemView.find(R.id.llSourceItem)

    init {
        llSource.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
        btnRemove.setOnClickListener {
            Timber.d("delete position: %s", adapterPosition)
            listener?.sourceRemovePosition(adapterPosition)
        }
    }

}

interface OnSourceDeleteListener {
    fun sourceRemovePosition(position: Int)
}