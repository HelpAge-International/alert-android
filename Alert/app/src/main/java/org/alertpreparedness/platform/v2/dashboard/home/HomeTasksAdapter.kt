package org.alertpreparedness.platform.v2.dashboard.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_task.view.ivIcon
import kotlinx.android.synthetic.main.item_task.view.tvDescription
import kotlinx.android.synthetic.main.item_task.view.tvTitle
import org.alertpreparedness.platform.v1.R
import org.alertpreparedness.platform.v1.utils.Constants
import org.alertpreparedness.platform.v2.dashboard.home.HomeTasksAdapter.HomeTaskViewHolder
import org.alertpreparedness.platform.v2.utils.DiffListAdapter
import org.alertpreparedness.platform.v2.utils.DiffComparator
import org.alertpreparedness.platform.v2.models.*
import org.alertpreparedness.platform.v2.utils.extensions.hasPassed
import org.alertpreparedness.platform.v2.utils.extensions.isToday
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.textResource
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

    override fun onBindViewHolder(holder: HomeTaskViewHolder, position: Int, model: Task) {
        return holder.bind(model)
    }

    inner class HomeTaskViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val ivIcon = view.ivIcon!!
        val tvTitle = view.tvTitle!!
        val tvDescription = view.tvDescription!!

        fun bind(task: Task){
            tvDescription.text = task.label
            when(task) {
                is IndicatorTask -> bindIndicatorTask(task)
                is ActionTask -> bindActionTask(task)
                is ApprovalTask -> bindApprovalTask(task)
            }
        }

        private fun bindIndicatorTask(indicatorTask: IndicatorTask) {
            setDateIcon(indicatorTask.dueDate)

            val triggerLevelString = context.getString(indicatorTask.indicatorTriggerLevel.string)

            when {
                indicatorTask.dueDate.hasPassed() -> tvTitle.text = context.getString(R.string.task_indicator_due_passed, triggerLevelString, indicatorTask.dueDate.toString("dd/MM/yy"))
                indicatorTask.dueDate.isToday() -> tvTitle.text = context.getString(R.string.task_indicator_due_today, triggerLevelString)
                else -> tvTitle.text = context.getString(R.string.task_indicator_due_week, triggerLevelString)
            }

        }

        private fun bindActionTask(actionTask: ActionTask) {
            setDateIcon(actionTask.dueDate)

            val actionTypeString = context.getString(actionTask.actionType.string)

            when {
                actionTask.dueDate.hasPassed() -> tvTitle.text = context.getString(R.string.task_action_due_passed, actionTypeString, actionTask.dueDate.toString("dd/MM/yy"))
                actionTask.dueDate.isToday() -> tvTitle.text = context.getString(R.string.task_action_due_today, actionTypeString)
                else -> tvTitle.text = context.getString(R.string.task_action_due_week, actionTypeString)
            }

        }

        private fun bindApprovalTask(approvalTask: ApprovalTask) {
            ivIcon.imageResource = R.drawable.home_task_blue
            tvTitle.textResource = R.string.task_approval
        }

        private fun setDateIcon(dateTime: DateTime){
            if (dateTime.isToday() || dateTime.hasPassed()){
                ivIcon.imageResource = R.drawable.home_task_red
            }
            else {
                ivIcon.imageResource = R.drawable.home_task_amber
            }
        }
    }
}