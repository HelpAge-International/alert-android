package org.alertpreparedness.platform.v2.dashboard.home

import io.reactivex.Observable
import org.alertpreparedness.platform.v2.base.BaseViewModel
import org.alertpreparedness.platform.v2.dashboard.home.AlertActionType.NORMAL
import org.alertpreparedness.platform.v2.dashboard.home.AlertActionType.RED_ALERT_REQUESTED
import org.alertpreparedness.platform.v2.dashboard.home.AlertActionType.RED_ALERT_REQUIRES_ACTION
import org.alertpreparedness.platform.v2.models.ActionTask
import org.alertpreparedness.platform.v2.models.Alert
import org.alertpreparedness.platform.v2.models.ApprovalTask
import org.alertpreparedness.platform.v2.models.IndicatorTask
import org.alertpreparedness.platform.v2.models.Task
import org.alertpreparedness.platform.v2.models.UserType.COUNTRY_DIRECTOR
import org.alertpreparedness.platform.v2.models.enums.AlertApprovalState.REJECTED
import org.alertpreparedness.platform.v2.models.enums.AlertApprovalState.WAITING_RESPONSE
import org.alertpreparedness.platform.v2.models.enums.AlertLevel.AMBER
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
    interface Inputs

    interface Outputs {
        fun tasks(): Observable<List<Task>>
        fun alerts(): Observable<List<Pair<AlertActionType, Alert>>>
    }
}

enum class AlertActionType {
    NORMAL,
    RED_ALERT_REQUESTED,
    RED_ALERT_REQUIRES_ACTION
}

class HomeViewModel : BaseViewModel(), IHomeViewModel.Inputs, IHomeViewModel.Outputs {
    override fun alerts(): Observable<List<Pair<AlertActionType, Alert>>> {
        return alertsObservable
                .filterList {
                    (it.level == RED || it.level == AMBER) && it.state != REJECTED
                }
                .combineWithPair(userObservable)
                .map { (alerts, user) ->
                    alerts.map { alert ->
                        if (alert.level == RED && alert.state == WAITING_RESPONSE) {
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
}


