package org.alertpreparedness.platform.v2.dashboard.home

import io.reactivex.Observable
import org.alertpreparedness.platform.v2.base.BaseViewModel
import org.alertpreparedness.platform.v2.models.ActionTask
import org.alertpreparedness.platform.v2.models.ApprovalTask
import org.alertpreparedness.platform.v2.models.IndicatorTask
import org.alertpreparedness.platform.v2.models.Task
import org.alertpreparedness.platform.v2.repository.Repository.actionsObservable
import org.alertpreparedness.platform.v2.repository.Repository.indicatorsObservable
import org.alertpreparedness.platform.v2.repository.Repository.responsePlansObservable
import org.alertpreparedness.platform.v2.repository.Repository.userObservable
import org.alertpreparedness.platform.v2.utils.extensions.combineFlatten
import org.alertpreparedness.platform.v2.utils.extensions.hasPassed
import org.alertpreparedness.platform.v2.utils.extensions.isThisWeek
import org.alertpreparedness.platform.v2.utils.extensions.isToday
import org.alertpreparedness.platform.v2.utils.extensions.withLatestFromPair

interface IHomeViewModel {
    interface Inputs

    interface Outputs {
        fun tasks(): Observable<List<Task>>
    }
}

class HomeViewModel : BaseViewModel(), IHomeViewModel.Inputs, IHomeViewModel.Outputs {

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

        return combineFlatten<Task>(
                indicators
                        .map { list -> list.map { IndicatorTask(it) } },
                actions
                        .map { list -> list.map { ActionTask(it) } },
                responsePlansObservable
                        .map { list -> list.map { ApprovalTask(it) } }
        )
                .map { list -> list
                        .filter { it.dueDate.isThisWeek() || it.dueDate.isToday() || it.dueDate.hasPassed() }
                        .sortedBy { it.dueDate } }
    }
}


