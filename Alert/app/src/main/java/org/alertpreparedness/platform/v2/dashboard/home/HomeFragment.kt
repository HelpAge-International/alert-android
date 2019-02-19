package org.alertpreparedness.platform.v2.dashboard.home

import android.app.AlertDialog
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.grpEmptyTasks
import kotlinx.android.synthetic.main.fragment_home.pbTasks
import kotlinx.android.synthetic.main.fragment_home.rvAlerts
import kotlinx.android.synthetic.main.fragment_home.rvMyTasks
import org.alertpreparedness.platform.v1.MainDrawer
import org.alertpreparedness.platform.v1.MainDrawer.ActionBarState.ALERT
import org.alertpreparedness.platform.v1.R
import org.alertpreparedness.platform.v1.dashboard.activity.AlertDetailActivity
import org.alertpreparedness.platform.v1.firebase.AlertModel
import org.alertpreparedness.platform.v1.min_preparedness.activity.CompleteActionActivity
import org.alertpreparedness.platform.v1.risk_monitoring.view.UpdateIndicatorActivity
import org.alertpreparedness.platform.v2.base.BaseFragment
import org.alertpreparedness.platform.v2.models.enums.AlertLevel.AMBER
import org.alertpreparedness.platform.v2.models.enums.AlertLevel.GREEN
import org.alertpreparedness.platform.v2.models.enums.AlertLevel.RED
import org.alertpreparedness.platform.v2.repository.Repository.userObservable
import org.alertpreparedness.platform.v2.utils.extensions.hide
import org.alertpreparedness.platform.v2.utils.extensions.show
import org.alertpreparedness.platform.v2.utils.extensions.withLatestFromPair

class HomeFragment : BaseFragment<HomeViewModel>() {
    private lateinit var tasksAdapter: HomeTasksAdapter
    private lateinit var alertsAdapter: HomeAlertsAdapter

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    override fun viewModelClass(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun initViews() {
        super.initViews()

        (activity as MainDrawer).toggleActionBar(MainDrawer.ActionBarState.ALERT)

        tasksAdapter = HomeTasksAdapter(context!!)
        tasksAdapter.addListener {
            viewModel.onTaskClick(it)
        }
        rvMyTasks.layoutManager = LinearLayoutManager(context)
        rvMyTasks.adapter = tasksAdapter

        alertsAdapter = HomeAlertsAdapter(context!!)
        alertsAdapter.addListener {
            viewModel.onAlertClick(it)
        }
        rvAlerts.layoutManager = LinearLayoutManager(context)
        rvAlerts.adapter = alertsAdapter
    }

    override fun observeViewModel() {

        disposables += viewModel.tasks()
                .subscribe { tasks ->
                    tasksAdapter.updateItems(tasks)
                }

        disposables += viewModel.tasks()
                .map { it.isNotEmpty() }
                .subscribe { tasksVisible ->
                    pbTasks.hide()
                    rvMyTasks.show(tasksVisible)
                    grpEmptyTasks.show(!tasksVisible)
                }

        disposables += viewModel.alerts()
                .subscribe { items ->
                    rvAlerts.show(items.isNotEmpty())
                    alertsAdapter.updateItems(items)
                }

        disposables += viewModel.showAlertActivity()
                .withLatestFromPair(userObservable)
                .map { (alert, user) ->
                    AlertModel(alert, user.countryId, user.agencyAdminId)
                }
                .subscribe { alert ->
                    val intent = Intent(activity, AlertDetailActivity::class.java)
                    intent.putExtra(AlertDetailActivity.EXTRA_ALERT, alert)
                    startActivity(intent)
                }

        disposables += viewModel.showIndicatorActivity()
                .subscribe { indicator ->
                    UpdateIndicatorActivity.startActivity(activity!!, indicator.parentId, indicator.id)
                }

        disposables += viewModel.showActionActivity()
                .withLatestFromPair(userObservable)
                .subscribe { (action, user) ->
                    val intent = Intent(activity, CompleteActionActivity::class.java)
                    intent.putExtra(CompleteActionActivity.ACTION_KEY, action.id)
                    intent.putExtra(CompleteActionActivity.PARENT_KEY, user.countryId)
                    intent.putExtra(CompleteActionActivity.REQUIRE_DOC, action.requireDoc)
                    startActivity(intent)
                }

        disposables += viewModel.showReviewPlanDialog()
                .subscribe { _ ->
                    AlertDialog.Builder(activity)
                            .setTitle(R.string.review_plan)
                            .setMessage(R.string.review_plan_content)
                            .setNegativeButton(R.string.close, null)
                            .show()
                }

        disposables += viewModel.toolbarAlertLevel()
                .subscribe { level ->
                    val mainDrawer = activity as MainDrawer

                    when (level) {
                        GREEN -> {
                            mainDrawer.toggleActionBarWithTitle(
                                    ALERT,
                                    R.string.green_alert_level,
                                    R.drawable.alert_green_button_bg
                            )
                        }
                        AMBER -> {
                            mainDrawer.toggleActionBarWithTitle(
                                    ALERT,
                                    R.string.amber_alert_level,
                                    R.drawable.alert_amber_button_bg
                            )
                        }
                        RED -> {
                            mainDrawer.toggleActionBarWithTitle(
                                    ALERT,
                                    R.string.red_alert_level,
                                    R.drawable.alert_red_button_bg
                            )
                        }
                    }
                }

    }
}
