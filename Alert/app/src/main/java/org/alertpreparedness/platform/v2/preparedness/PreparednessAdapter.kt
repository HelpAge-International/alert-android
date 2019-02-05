package org.alertpreparedness.platform.v2.preparedness

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.item_preparedness_action.view.tvActionName
import kotlinx.android.synthetic.main.item_preparedness_action.view.tvActionType
import kotlinx.android.synthetic.main.item_preparedness_action.view.tvAssignee
import kotlinx.android.synthetic.main.item_preparedness_action.view.tvBudget
import kotlinx.android.synthetic.main.item_preparedness_action.view.tvDate
import org.alertpreparedness.platform.v1.R
import org.alertpreparedness.platform.v2.models.Action
import org.alertpreparedness.platform.v2.models.User
import org.alertpreparedness.platform.v2.preparedness.PreparednessAdapter.PreparednessViewHolder
import org.alertpreparedness.platform.v2.utils.DiffComparator
import org.alertpreparedness.platform.v2.utils.DiffListAdapter
import org.alertpreparedness.platform.v2.utils.ViewHolder
import org.alertpreparedness.platform.v2.utils.extensions.hide
import org.alertpreparedness.platform.v2.utils.extensions.show
import kotlin.math.roundToLong

class PreparednessAdapter(val context: Context) :
        DiffListAdapter<Action, PreparednessViewHolder>(object : DiffComparator<Action> {
            override fun areItemsTheSame(o1: Action, o2: Action): Boolean {
                return o1.id == o2.id
            }

            override fun areContentsTheSame(o1: Action, o2: Action): Boolean {
                return o1 == o2
            }
        }) {

    var user: User? = null

    fun updateUser(user: User) {
        this.user = user
        notifyAllItemsUpdated()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreparednessViewHolder {
        return PreparednessViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_preparedness_action, parent, false))
    }

    inner class PreparednessViewHolder(view: View) : ViewHolder<Action>(view) {
        val tvActionType: TextView = view.tvActionType
        val tvDate: TextView = view.tvDate
        val tvActionName: TextView = view.tvActionName
        val tvAssignee: TextView = view.tvAssignee
        val tvBudget: TextView = view.tvBudget

        override fun bind(model: Action, position: Int) {
            tvActionType.setText(model.actionType.text)
            tvDate.text = if (model.dueDate != null) {
                context.getString(R.string.due_on, model.dueDate.toString("MMM dd,yyyy"))
            } else {
                context.getString(R.string.not_assigned)
            }
            tvActionName.text = model.task
            if (model.assignee == null) {
                tvAssignee.text = context.getString(R.string.unassigned_title)
            } else if (user != null && model.assignee == user!!.id) {
                tvAssignee.text = context.getString(R.string.full_name, user!!.firstName, user!!.lastName)
            } else {
                tvAssignee.text = ""
            }

            if (model.budget == null) {
                tvBudget.hide()
            } else {
                tvBudget.show()
                tvBudget.text = context.getString(R.string.usd_formatted, model.budget.roundToLong())
            }
        }
    }
}