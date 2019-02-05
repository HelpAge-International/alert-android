package org.alertpreparedness.platform.v2.preparedness

import io.reactivex.Observable
import org.alertpreparedness.platform.v2.base.BaseViewModel
import org.alertpreparedness.platform.v2.models.Action
import org.alertpreparedness.platform.v2.models.User
import org.alertpreparedness.platform.v2.models.enums.AlertApprovalState.APPROVED
import org.alertpreparedness.platform.v2.models.enums.AlertLevel.RED
import org.alertpreparedness.platform.v2.models.enums.HazardScenario
import org.alertpreparedness.platform.v2.preparedness.IBasePreparednessViewModel.Inputs
import org.alertpreparedness.platform.v2.preparedness.IBasePreparednessViewModel.Outputs
import org.alertpreparedness.platform.v2.repository.Repository.actionsObservable
import org.alertpreparedness.platform.v2.repository.Repository.alertsObservable
import org.alertpreparedness.platform.v2.repository.Repository.userObservable
import org.alertpreparedness.platform.v2.utils.extensions.behavior
import org.alertpreparedness.platform.v2.utils.extensions.combineWithTriple
import org.alertpreparedness.platform.v2.utils.extensions.filterList
import org.alertpreparedness.platform.v2.utils.extensions.mapList
import org.alertpreparedness.platform.v2.utils.extensions.print

interface IBasePreparednessViewModel {
    interface Inputs

    interface Outputs {
        fun actions(): Observable<List<Action>>
        fun user(): Observable<User>
    }
}

abstract class BasePreparednessViewModel : BaseViewModel(), Inputs, Outputs {

    private val actions: Observable<List<Action>>

    abstract fun filterAction(action: Action, user: User, hazards: List<HazardScenario>): Boolean

    init {
        actions = actionsObservable
                .combineWithTriple(
                        userObservable,
                        //Red Alert Hazard Scenarios
                        alertsObservable
                                .print("Alerts") { it.size }
                                .filterList {
                                    it.level == RED && it.state == APPROVED
                                }
                                .print("Approved Red Alerts") { it.size }
                                .mapList { it.hazardScenario }
                                .print("Hazards") { it.size }
                                .map { it.distinct() }
                                .print("Distinct Hazards") { it.size }
                )
                .map { (list, user, hazards) -> list.filter { filterAction(it, user, hazards) } }
                .behavior()
    }

    override fun actions(): Observable<List<Action>> {
        return actions
    }

    override fun user(): Observable<User> {
        return userObservable
    }
}