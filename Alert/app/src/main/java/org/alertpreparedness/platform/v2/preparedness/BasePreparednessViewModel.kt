package org.alertpreparedness.platform.v2.preparedness

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.alertpreparedness.platform.v2.base.BaseViewModel
import org.alertpreparedness.platform.v2.models.Action
import org.alertpreparedness.platform.v2.models.User
import org.alertpreparedness.platform.v2.models.enums.AlertApprovalState.APPROVED
import org.alertpreparedness.platform.v2.models.enums.AlertLevel.RED
import org.alertpreparedness.platform.v2.models.enums.HazardScenario
import org.alertpreparedness.platform.v2.preparedness.IBasePreparednessViewModel.Inputs
import org.alertpreparedness.platform.v2.preparedness.IBasePreparednessViewModel.Outputs
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption
import org.alertpreparedness.platform.v2.repository.Repository.actionsObservable
import org.alertpreparedness.platform.v2.repository.Repository.alertsObservable
import org.alertpreparedness.platform.v2.repository.Repository.userObservable
import org.alertpreparedness.platform.v2.utils.extensions.behavior
import org.alertpreparedness.platform.v2.utils.extensions.combineWithTriple
import org.alertpreparedness.platform.v2.utils.extensions.filterList
import org.alertpreparedness.platform.v2.utils.extensions.mapList

interface IBasePreparednessViewModel {
    interface Inputs {
        fun actionClicked(action: Action)
        fun onPreparednessOptionClicked(action: Action, option: PreparednessBottomSheetOption)
    }

    interface Outputs {
        fun actions(): Observable<List<Action>>
        fun user(): Observable<User>
        fun showBottomSheet(): Observable<Pair<Action, List<PreparednessBottomSheetOption>>>
        fun showNotesActivity(action: Action, countryId: String)
        fun showDocumentsActivity(action: Action, countryId: String)
    }
}

abstract class BasePreparednessViewModel : BaseViewModel(), Inputs, Outputs {

    private val actions: Observable<List<Action>>
    private val onActionClicked = PublishSubject.create<Action>()
    private val onPreparednessOptionClicked = PublishSubject.create<Pair<Action, PreparednessBottomSheetOption>>()

    init {
        actions = actionsObservable
                .combineWithTriple(
                        userObservable,
                        //Red Alert Hazard Scenarios
                        alertsObservable
                                .filterList {
                                    it.level == RED && it.state == APPROVED
                                }
                                .mapList { it.hazardScenario }
                                .map { it.distinct() }
                )
                .map { (list, user, hazards) -> list.filter { filterAction(it, user, hazards) } }
                .behavior()
    }

    abstract fun filterAction(action: Action, user: User, hazards: List<HazardScenario>): Boolean

    abstract fun getSelectOptions(): List<PreparednessBottomSheetOption>

    override fun actionClicked(action: Action) {
        onActionClicked.onNext(action)
    }

    override fun actions(): Observable<List<Action>> {
        return actions
    }

    override fun user(): Observable<User> {
        return userObservable
    }

    override fun showBottomSheet(): Observable<Pair<Action, List<PreparednessBottomSheetOption>>> {
        return onActionClicked.map { Pair(it, getSelectOptions()) }
    }

    override fun onPreparednessOptionClicked(action: Action, option: PreparednessBottomSheetOption) {
        onPreparednessOptionClicked.onNext(Pair(action, option))
    }
}