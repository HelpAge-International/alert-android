package org.alertpreparedness.platform.v2.preparedness.advanced

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_bottom_sheet_preparedness.rvList
import org.alertpreparedness.platform.v1.R
import org.alertpreparedness.platform.v2.base.BaseBottomSheetDialogFragment
import org.alertpreparedness.platform.v2.models.Action
import org.alertpreparedness.platform.v2.models.enums.ActionType
import org.alertpreparedness.platform.v2.preparedness.PreparednessBottomSheetAdapter
import org.alertpreparedness.platform.v2.preparedness.PreparednessBottomSheetAdapterListener
import org.alertpreparedness.platform.v2.preparedness.PreparednessBottomSheetViewModel
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption.ATTACHMENTS
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption.NOTES
import org.alertpreparedness.platform.v2.utils.extensions.combineWithPair
import org.jetbrains.anko.bundleOf

enum class PreparednessBottomSheetOption(@StringRes val string: Int, @DrawableRes val icon: Int) {
    COMPLETE(R.string.complete_action, R.drawable.complete_action),
    REASSIGN(R.string.reassign_action, R.drawable.assign_action),
    ASSIGN(R.string.assign_action, R.drawable.assign_action),
    NOTES(R.string.notes_action, R.drawable.notes_action),
    ATTACHMENTS(R.string.attachments_action, R.drawable.attach_action),
    UPDATE_DUE_DATE(R.string.update_due_date_action, R.drawable.update_due_date_action),
    EDIT(R.string.action_edit, R.drawable.action_edit)
}

class PreparednessBottomSheetDialogFragment : BaseBottomSheetDialogFragment<PreparednessBottomSheetViewModel>(),
        PreparednessBottomSheetAdapterListener {

    private lateinit var actionId: String

    private lateinit var actionType: ActionType
    private lateinit var options: List<PreparednessBottomSheetOption>
    override fun getLayoutId(): Int {
        return R.layout.fragment_bottom_sheet_preparedness
    }

    override fun viewModelClass(): Class<PreparednessBottomSheetViewModel> {
        return PreparednessBottomSheetViewModel::class.java
    }

    override fun observeViewModel() {
        viewModel.action(actionId, actionType)

        disposables += viewModel.notesCount().combineWithPair(viewModel.attachmentCount())
                .startWith(Pair(0, 0))
                .subscribe { (notes, attachments) ->
                    adapter.updateItems(
                            options.map { option ->
                                option to when (option) {
                                    NOTES -> notes
                                    ATTACHMENTS -> attachments
                                    else -> 0
                                }
                            }
                    )
                }

        disposables += viewModel.actionOptionClicked()
                .subscribe { (action, option) ->
                    if (targetFragment is OnPreparednessOptionClickedListener) {
                        (targetFragment as OnPreparednessOptionClickedListener)
                                .onPreparednessOptionClicked(action, option)
                    }
                    dismiss()
                }
    }

    override fun onOptionSelected(option: PreparednessBottomSheetOption) {
        viewModel.onActionOptionClicked(option)
    }

    private lateinit var adapter: PreparednessBottomSheetAdapter

    override fun initViews() {
        super.initViews()

        adapter = PreparednessBottomSheetAdapter(context!!)
        adapter.addListener(this)
        rvList.layoutManager = LinearLayoutManager(context!!)
        rvList.adapter = adapter
    }

    override fun arguments(bundle: Bundle) {
        super.arguments(bundle)

        actionId = bundle.getString(ACTION_ID_KEY)!!
        actionType = ActionType.values()[bundle.getInt(ACTION_TYPE_KEY)]
        options = bundle.getIntegerArrayList(
                OPTIONS_KEY)!!.map { PreparednessBottomSheetOption.values()[it] }.distinct()
    }

    companion object {

        val ACTION_ID_KEY = "ACTION_ID_KEY"
        val ACTION_TYPE_KEY = "ACTION_TYPE_KEY"
        val OPTIONS_KEY = "OPTIONS_KEY"

        fun newInstance(action: Action,
                options: List<PreparednessBottomSheetOption>): PreparednessBottomSheetDialogFragment {
            return newInstance(action.id, action.actionType, options)
        }

        fun newInstance(actionId: String, actionType: ActionType,
                options: List<PreparednessBottomSheetOption>): PreparednessBottomSheetDialogFragment {
            val fragment = PreparednessBottomSheetDialogFragment()

            fragment.arguments = bundleOf(
                    ACTION_ID_KEY to actionId,
                    ACTION_TYPE_KEY to actionType.ordinal,
                    OPTIONS_KEY to options.map { it.ordinal }
            )

            return fragment
        }
    }
}

interface OnPreparednessOptionClickedListener {
    fun onPreparednessOptionClicked(action: Action, option: PreparednessBottomSheetOption)
}