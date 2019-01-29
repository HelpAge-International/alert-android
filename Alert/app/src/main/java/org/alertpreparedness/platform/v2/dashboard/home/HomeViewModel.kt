package org.alertpreparedness.platform.v2.dashboard.home

import io.reactivex.Observable
import org.alertpreparedness.platform.v2.asObservable
import org.alertpreparedness.platform.v2.base.BaseViewModel
import org.alertpreparedness.platform.v2.db
import org.alertpreparedness.platform.v2.models.Action
import org.alertpreparedness.platform.v2.models.ActionTask
import org.alertpreparedness.platform.v2.models.ApprovalTask
import org.alertpreparedness.platform.v2.models.Indicator
import org.alertpreparedness.platform.v2.models.IndicatorTask
import org.alertpreparedness.platform.v2.models.ResponsePlan
import org.alertpreparedness.platform.v2.models.Task
import org.alertpreparedness.platform.v2.models.UserType.COUNTRY_DIRECTOR
import org.alertpreparedness.platform.v2.repository.UserRepository.userObservable
import org.alertpreparedness.platform.v2.utils.extensions.childKeys
import org.alertpreparedness.platform.v2.utils.extensions.combineFlatten
import org.alertpreparedness.platform.v2.utils.extensions.hasPassed
import org.alertpreparedness.platform.v2.utils.extensions.isThisWeek
import org.alertpreparedness.platform.v2.utils.extensions.isToday
import org.alertpreparedness.platform.v2.utils.extensions.print
import org.alertpreparedness.platform.v2.utils.extensions.toModel
import org.alertpreparedness.platform.v2.utils.extensions.withLatestFromPair

interface IHomeViewModel {
    interface Inputs {
    }

    interface Outputs {
        fun tasks(): Observable<List<Task>>
    }
}

class HomeViewModel : BaseViewModel(), IHomeViewModel.Inputs, IHomeViewModel.Outputs {

    override fun tasks(): Observable<List<Task>> {
        val hazardIds = userObservable
                .flatMap { user ->
                    db.child("hazard")
                            .child(user.countryId)
                            .asObservable()
                            .map { it.childKeys() + listOf(user.countryId) }
                }

        val indicators = hazardIds
                .flatMap { hazardIdList ->
                    combineFlatten(
                            hazardIdList.map { hazardId ->
                                db.child("indicator")
                                        .child(hazardId)
                                        .asObservable()
                                        .map { it.children.toList() }
                            }
                    )
                            .map { list -> list.map { it.toModel<Indicator>() } }
                }
                .withLatestFromPair(userObservable)
                .map { (list, user) ->
                    list.filter { it.assignee == user.id }
                }
                .print("indicators")

        val actions = userObservable
                .flatMap { user ->
                    db.child("action")
                            .child(user.countryId)
                            .asObservable()
                            .print("A")
                            .map { it.children.toList() }
                            .print("B")
                            .map { list -> list.map { it.toModel<Action>() } }
                }
                .withLatestFromPair(userObservable)
                .map { (list, user) ->
                    list.filter { it.assignee == user.id }
                }
                .print("actions")

        val responsePlanApprovals = userObservable
                .filter { user ->
                    user.userType == COUNTRY_DIRECTOR
                }
                .flatMap { user ->
                    db.child("responsePlan")
                            .child(user.countryId)
                            .asObservable()
                            .map { it.children.toList() }
                            .map { list ->
                                list.map {
                                    it.toModel<ResponsePlan>()
                                }
                                .filter {
                                    it.approval.countryDirector?.id == user.countryId
                                }
                            }
                }
                .startWith(listOf<ResponsePlan>())
                .print("responsePlans")

        return combineFlatten<Task>(
                indicators
                        .map { list -> list.map { IndicatorTask(it) } },
                actions
                        .map { list -> list.map { ActionTask(it) } },
                responsePlanApprovals
                        .map { list -> list.map { ApprovalTask(it) } }
        )
                .map { list -> list
                        .filter { it.dueDate.isThisWeek() || it.dueDate.isToday() || it.dueDate.hasPassed() }
                        .sortedByDescending { it.dueDate } }
    }
}


