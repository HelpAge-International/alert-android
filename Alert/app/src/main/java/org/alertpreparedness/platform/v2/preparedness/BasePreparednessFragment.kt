package org.alertpreparedness.platform.v2.preparedness

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.widget.DatePicker
import androidx.annotation.CallSuper
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_page_preparedness.ivStatus
import kotlinx.android.synthetic.main.fragment_page_preparedness.rvActions
import kotlinx.android.synthetic.main.fragment_page_preparedness.tvNoAction
import kotlinx.android.synthetic.main.fragment_page_preparedness.tvStatus
import org.alertpreparedness.platform.v1.R
import org.alertpreparedness.platform.v1.adv_preparedness.activity.EditAPAActivity
import org.alertpreparedness.platform.v1.adv_preparedness.fragment.UsersListDialogFragment
import org.alertpreparedness.platform.v1.firebase.ActionModel
import org.alertpreparedness.platform.v1.min_preparedness.activity.AddNotesActivity
import org.alertpreparedness.platform.v1.min_preparedness.activity.CompleteActionActivity
import org.alertpreparedness.platform.v1.min_preparedness.activity.ViewAttachmentsActivity
import org.alertpreparedness.platform.v2.base.BaseFragment
import org.alertpreparedness.platform.v2.models.Action
import org.alertpreparedness.platform.v2.preparedness.advanced.OnPreparednessOptionClickedListener
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetDialogFragment
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption
import org.alertpreparedness.platform.v2.utils.Nullable
import org.alertpreparedness.platform.v2.utils.extensions.show
import org.jetbrains.anko.imageResource
import org.joda.time.LocalDate
import java.util.Date

abstract class BasePreparednessFragment<VM : BasePreparednessViewModel> : BaseFragment<VM>(),
        OnActionClickedListener, OnPreparednessOptionClickedListener, OnDateSetListener {

    override fun getLayoutId(): Int {
        return R.layout.fragment_page_preparedness
    }

    @StringRes
    abstract fun statusText(): Int

    @DrawableRes
    abstract fun statusIcon(): Int

    @ColorRes
    abstract fun statusColor(): Int

    private lateinit var adapter: PreparednessAdapter

    private lateinit var datePickerDialog: DatePickerDialog

    override fun initViews() {
        super.initViews()

        val localDate = LocalDate.now()
        datePickerDialog = DatePickerDialog(context!!, this, localDate.year, localDate.monthOfYear,
                localDate.dayOfMonth)
        datePickerDialog.datePicker.minDate = Date().time

        tvStatus.setText(statusText())
        tvStatus.setTextColor(getColor(context!!, statusColor()))
        ivStatus.imageResource = statusIcon()

        adapter = PreparednessAdapter(context!!)
        adapter.addOnActionClickedListener(this)
        rvActions.adapter = adapter
        rvActions.layoutManager = LinearLayoutManager(context!!)
        rvActions.addItemDecoration(DividerItemDecoration(context!!, DividerItemDecoration.VERTICAL))
    }

    @CallSuper
    override fun observeViewModel() {
        disposables += viewModel.user()
                .subscribe {
                    adapter.updateUser(it)
                }

        disposables += viewModel.actions()
                .subscribe {
                    adapter.updateItems(it)
                    rvActions.show(it.isNotEmpty())
                    tvNoAction.show(it.isEmpty())
                }

        disposables += viewModel.showBottomSheet()
                .subscribe { (action, options) ->
                    val fragment = PreparednessBottomSheetDialogFragment.newInstance(action, options)
                    fragment.setTargetFragment(this, 0)
                    fragment.show(fragmentManager, "PreparednessBottomSheet")
                }

        disposables += viewModel.showDocumentsActivity()
                .subscribe { (action, countryId) ->
                    val intent = Intent(activity, ViewAttachmentsActivity::class.java)
                    intent.putExtra(ViewAttachmentsActivity.ACTION_ID, action.id)
                    intent.putExtra(ViewAttachmentsActivity.PARENT_ACTION_ID, countryId)
                    startActivity(intent)
                }

        disposables += viewModel.showNotesActivity()
                .subscribe { (action, countryId) ->
                    val intent = Intent(activity, AddNotesActivity::class.java)
                    intent.putExtra(AddNotesActivity.ACTION_ID, action.id)
                    intent.putExtra(AddNotesActivity.PARENT_ACTION_ID, countryId)
                    startActivity(intent)
                }

        disposables += viewModel.showDatePicker()
                .subscribe {
                    datePickerDialog.show()
                }

        disposables += viewModel.updateDueDateSuccess()
                .subscribe {
                    Snackbar.make(rvActions, R.string.due_date_updated, Snackbar.LENGTH_LONG).show()
                }

        disposables += viewModel.assignAction()
                .subscribe {
                    val fragment = UsersListDialogFragment()
                    fragment.setListener {
                        viewModel.onAssignUser(Nullable(it?.userID))
                    }
                    fragment.show(fragmentManager, "Select User")
                }

        disposables += viewModel.genericError()
                .subscribe {
                    Snackbar.make(rvActions, R.string.an_error_occurred, Snackbar.LENGTH_LONG).show()
                }

        disposables += viewModel.showCompleteActivity()
                .subscribe { (action, countryId) ->
                    val intent = Intent(activity, CompleteActionActivity::class.java)
                    intent.putExtra(CompleteActionActivity.REQUIRE_DOC, action.requireDoc)
                    intent.putExtra(CompleteActionActivity.ACTION_KEY, action.id)
                    intent.putExtra(CompleteActionActivity.PARENT_KEY, countryId)
                    startActivity(intent)
                }

        disposables += viewModel.showEditActivity()
                .subscribe { (action, countryId) ->
                    val i = Intent(context, EditAPAActivity::class.java)
                    i.putExtra(EditAPAActivity.MODEL, ActionModel(action, countryId))
                    startActivity(i)
                }

        disposables += viewModel.assignActionPermissionError()
                .subscribe {
                    Snackbar.make(rvActions, R.string.permission_assign_action_error, Snackbar.LENGTH_LONG).show()
                }
        disposables += viewModel.assignActionIncompleteError()
                .subscribe {
                    Snackbar.make(rvActions, R.string.more_info_chs, Snackbar.LENGTH_LONG).show()
                }
        disposables += viewModel.completeActionPermissionError()
                .subscribe {
                    Snackbar.make(rvActions, R.string.permission_complete_error, Snackbar.LENGTH_LONG).show()
                }
    }

    override fun onActionClicked(action: Action) {
        viewModel.actionClicked(action)
    }

    override fun onPreparednessOptionClicked(action: Action, option: PreparednessBottomSheetOption) {
        viewModel.onPreparednessOptionClicked(action, option)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        viewModel.onUpdateDueDate(LocalDate(year, month + 1, dayOfMonth))
    }
}
