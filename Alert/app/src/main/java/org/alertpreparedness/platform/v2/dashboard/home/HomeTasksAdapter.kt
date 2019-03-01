package org.alertpreparedness.platform.v2.dashboard.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_task.view.ivIcon
import kotlinx.android.synthetic.main.item_task.view.tvDescription
import kotlinx.android.synthetic.main.item_task.view.tvTitle
import org.alertpreparedness.platform.R
import org.alertpreparedness.platform.v2.dashboard.home.HomeTasksAdapter.HomeTaskViewHolder
import org.alertpreparedness.platform.v2.models.ActionTask
import org.alertpreparedness.platform.v2.models.ApprovalTask
import org.alertpreparedness.platform.v2.models.IndicatorTask
import org.alertpreparedness.platform.v2.models.Task
import org.alertpreparedness.platform.v2.utils.DiffComparator
import org.alertpreparedness.platform.v2.utils.DiffListAdapter
import org.alertpreparedness.platform.v2.utils.ViewHolder
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

    private val listeners = mutableSetOf<OnTaskClickListener>()

    fun addListener(func: (Task) -> Unit): OnTaskClickListener {
        val listener = object : OnTaskClickListener {
            override fun onTaskClick(task: Task) {
                func(task)
            }
        }
        listeners.add(listener)
        return listener
    }

    fun addListener(listener: OnTaskClickListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: OnTaskClickListener) {
        listeners.remove(listener)
    }

    private fun notifyTaskClick(model: Task) {
        listeners.forEach { it.onTaskClick(model) }
    }

    inner class HomeTaskViewHolder(view: View) : ViewHolder<Task>(view) {
        override fun bind(model: Task, position: Int) {
            tvDescription.text = model.label
            when (model) {
                is IndicatorTask -> bindIndicatorTask(model)
                is ActionTask -> bindActionTask(model)
                is ApprovalTask -> bindApprovalTask(model)
            }
        }

        val ivIcon = view.ivIcon!!
        val tvTitle = view.tvTitle!!
        val tvDescription = view.tvDescription!!

        init {
            view.setOnClickListener {
                notifyTaskClick(model)
            }
        }

        private fun bindIndicatorTask(indicatorTask: IndicatorTask) {
            setDateIcon(indicatorTask.dueDate)

            val triggerLevelString = context.getString(indicatorTask.indicator.triggerLevel.string)

            when {
                indicatorTask.dueDate.hasPassed() -> tvTitle.text = context.getString(R.string.task_indicator_due_passed, triggerLevelString, indicatorTask.dueDate.toString("dd/MM/yy"))
                indicatorTask.dueDate.isToday() -> tvTitle.text = context.getString(R.string.task_indicator_due_today, triggerLevelString)
                else -> tvTitle.text = context.getString(R.string.task_indicator_due_week, triggerLevelString)
            }

        }

        private fun bindActionTask(actionTask: ActionTask) {
            setDateIcon(actionTask.dueDate)

            val actionTypeString = context.getString(actionTask.action.actionLevel.string)

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

interface OnTaskClickListener {
    fun onTaskClick(task: Task)
}