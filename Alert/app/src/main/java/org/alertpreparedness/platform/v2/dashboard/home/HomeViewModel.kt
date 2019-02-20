package org.alertpreparedness.platform.v2.dashboard.home

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.alertpreparedness.platform.v2.base.BaseViewModel
import org.alertpreparedness.platform.v2.dashboard.home.AlertActionType.NORMAL
import org.alertpreparedness.platform.v2.dashboard.home.AlertActionType.RED_ALERT_REQUESTED
import org.alertpreparedness.platform.v2.dashboard.home.AlertActionType.RED_ALERT_REQUIRES_ACTION
import org.alertpreparedness.platform.v2.models.Action
import org.alertpreparedness.platform.v2.models.ActionTask
import org.alertpreparedness.platform.v2.models.Alert
import org.alertpreparedness.platform.v2.models.ApprovalTask
import org.alertpreparedness.platform.v2.models.Indicator
import org.alertpreparedness.platform.v2.models.IndicatorTask
import org.alertpreparedness.platform.v2.models.ResponsePlan
import org.alertpreparedness.platform.v2.models.Task
import org.alertpreparedness.platform.v2.models.UserType.COUNTRY_DIRECTOR
import org.alertpreparedness.platform.v2.models.enums.AlertApprovalState.APPROVED
import org.alertpreparedness.platform.v2.models.enums.AlertApprovalState.REJECTED
import org.alertpreparedness.platform.v2.models.enums.AlertApprovalState.WAITING_RESPONSE
import org.alertpreparedness.platform.v2.models.enums.AlertLevel
import org.alertpreparedness.platform.v2.models.enums.AlertLevel.AMBER
import org.alertpreparedness.platform.v2.models.enums.AlertLevel.GREEN
import org.alertpreparedness.platform.v2.models.enums.AlertLevel.RED
import org.alertpreparedness.platform.v2.repository.Repository.actionsObservable
import org.alertpreparedness.platform.v2.repository.Repository.alertsObservable
import org.alertpreparedness.platform.v2.repository.Repository.indicatorsObservable
import org.alertpreparedness.platform.v2.repository.Repository.responsePlansObservable
import org.alertpreparedness.platform.v2.repository.Repository.userObservable
import org.alertpreparedness.platform.v2.utils.extensions.combineFlatten
import org.alertpreparedness.platform.v2.utils.extensions.combineWithPair
import org.alertpreparedness.platform.v2.utils.extensions.filterList
import org.alertpreparedness.platform.v2.utils.extensions.hasPassed
import org.alertpreparedness.platform.v2.utils.extensions.isThisWeek
import org.alertpreparedness.platform.v2.utils.extensions.isToday
import org.alertpreparedness.platform.v2.utils.extensions.mapList
import org.alertpreparedness.platform.v2.utils.extensions.withLatestFromPair

interface IHomeViewModel {
    interface Inputs {
        fun onTaskClick(task: Task)
        fun onAlertClick(alert: Alert)
    }

    interface Outputs {
        fun tasks(): Observable<List<Task>>
        fun alerts(): Observable<List<Pair<AlertActionType, Alert>>>

        fun showAlertActivity(): Observable<Alert>
        fun showIndicatorActivity(): Observable<Indicator>
        fun showActionActivity(): Observable<Action>
        fun showReviewPlanDialog(): Observable<ResponsePlan>
        fun toolbarAlertLevel(): Observable<AlertLevel>
    }
}

enum class AlertActionType {
    NORMAL,
    RED_ALERT_REQUESTED,
    RED_ALERT_REQUIRES_ACTION
}

class HomeViewModel : BaseViewModel(), IHomeViewModel.Inputs, IHomeViewModel.Outputs {
    val onAlertClick = PublishSubject.create<Alert>()

    val onTaskClick = PublishSubject.create<Task>()
    override fun alerts(): Observable<List<Pair<AlertActionType, Alert>>> {
        return alertsObservable
                .filterList {
                    (it.level == RED || it.level == AMBER) && it.state != REJECTED
                }
                .combineWithPair(userObservable)
                .map { (alerts, user) ->
                    alerts.map { alert ->
                        if (alert.level == RED && (alert.state == WAITING_RESPONSE || !alert.redAlertApproved)) {
                            if (user.userType == COUNTRY_DIRECTOR) {
                                RED_ALERT_REQUIRES_ACTION
                            } else {
                                RED_ALERT_REQUESTED
                            }
                        } else {
                            NORMAL
                        } to alert

                    }
                }
    }

    override fun tasks(): Observable<List<Task>> {
        val indicators = indicatorsObservable
                .withLatestFromPair(userObservable)
                .map { (list, user) ->
                    list.filter { it.assignee == user.id }
                }

        val actions = actionsObservable
                .withLatestFromPair(userObservable)
                .map { (list, user) ->
                    list.filter { it.assignee == user.id }
                }
                .filterList {
                    !it.isArchived && !it.isComplete
                }


        return combineFlatten<Task>(
                indicators
                        .mapList { IndicatorTask(it) },
                actions
                        .mapList { ActionTask(it) },
                responsePlansObservable
                        .mapList { ApprovalTask(it) }
        )
                .map { list ->
                    list
                            .filter { it.dueDate.isThisWeek() || it.dueDate.isToday() || it.dueDate.hasPassed() }
                            .sortedBy { it.dueDate }
                }
    }

    override fun onTaskClick(task: Task) {
        onTaskClick.onNext(task)
    }

    override fun onAlertClick(alert: Alert) {
        onAlertClick.onNext(alert)
    }

    override fun showAlertActivity(): Observable<Alert> {
        return onAlertClick
    }

    override fun showIndicatorActivity(): Observable<Indicator> {
        return onTaskClick
                .filter {
                    it is IndicatorTask
                }
                .map {
                    (it as IndicatorTask).indicator
                }
    }

    override fun showActionActivity(): Observable<Action> {
        return onTaskClick
                .filter {
                    it is ActionTask
                }
                .map {
                    (it as ActionTask).action
                }
    }

    override fun showReviewPlanDialog(): Observable<ResponsePlan> {
        return onTaskClick
                .filter {
                    it is ApprovalTask
                }
                .map {
                    (it as ApprovalTask).responsePlan
                }
    }

    override fun toolbarAlertLevel(): Observable<AlertLevel> {
        return alerts()
                .map { alerts ->
                    when {
                        alerts.any { (_, alert) -> alert.state == APPROVED && alert.level == RED } -> RED
                        alerts.any { (_, alert) -> alert.level == AMBER || (alert.state == WAITING_RESPONSE && alert.previousIsAmber) } -> AMBER
                        else -> GREEN
                    }
                }
    }
}


