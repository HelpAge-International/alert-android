package org.alertpreparedness.platform.v2.dashboard.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_task.view.ivTask
import kotlinx.android.synthetic.main.item_task.view.tvDescription
import kotlinx.android.synthetic.main.item_task.view.tvTitle
import org.alertpreparedness.platform.v1.R
import org.alertpreparedness.platform.v2.dashboard.home.HomeTasksAdapter.HomeTaskViewHolder
import org.alertpreparedness.platform.v2.utils.DiffListAdapter
import org.alertpreparedness.platform.v2.utils.DiffComparator
import org.alertpreparedness.platform.v2.models.*
import org.joda.time.DateTime

class HomeTasksAdapter(val context: Context): DiffListAdapter<Task, HomeTaskViewHolder>(object: DiffComparator<Task> {
    override fun areItemsTheSame(o1: Task, o2: Task): Boolean {
        return o1.id == o2.id
    }

    override fun areContentsTheSame(o1: Task, o2: Task): Boolean {
        return o1 == o2
    }
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeTaskViewHolder {
        return HomeTaskViewHolder(LayoutInflater.from(context).inflate(R.layout.item_task, parent, false))
    }

    inner class HomeTaskViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val ivTask = view.ivTask
        val tvTitle = view.tvTitle
        val tvDescription = view.tvDescription

        fun bind(task: Task){
            when(task) {
                is IndicatorTask -> bindIndicatorTask(task)
                is ActionTask -> bindActionTask(task)
                is ApprovalTask -> bindApprovalTask(task)
            }
        }

        private fun bindIndicatorTask(indicatorTask: IndicatorTask) {

        }

        private fun bindActionTask(actionTask: ActionTask) {

        }

        private fun bindApprovalTask(approvalTask: ApprovalTask) {

        }
    }
}