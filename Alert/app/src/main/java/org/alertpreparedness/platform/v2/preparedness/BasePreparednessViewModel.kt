package org.alertpreparedness.platform.v2.preparedness

import io.reactivex.Notification
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.alertpreparedness.platform.v2.base.BaseViewModel
import org.alertpreparedness.platform.v2.db
import org.alertpreparedness.platform.v2.models.Action
import org.alertpreparedness.platform.v2.models.User
import org.alertpreparedness.platform.v2.models.enums.AlertApprovalState.APPROVED
import org.alertpreparedness.platform.v2.models.enums.AlertLevel.RED
import org.alertpreparedness.platform.v2.models.enums.HazardScenario
import org.alertpreparedness.platform.v2.preparedness.IBasePreparednessViewModel.Inputs
import org.alertpreparedness.platform.v2.preparedness.IBasePreparednessViewModel.Outputs
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption.ATTACHMENTS
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption.NOTES
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption.UPDATE_DUE_DATE
import org.alertpreparedness.platform.v2.repository.Repository.actionsObservable
import org.alertpreparedness.platform.v2.repository.Repository.alertsObservable
import org.alertpreparedness.platform.v2.repository.Repository.userObservable
import org.alertpreparedness.platform.v2.setValueRx
import org.alertpreparedness.platform.v2.utils.extensions.behavior
import org.alertpreparedness.platform.v2.utils.extensions.combineWithPair
import org.alertpreparedness.platform.v2.utils.extensions.combineWithTriple
import org.alertpreparedness.platform.v2.utils.extensions.failure
import org.alertpreparedness.platform.v2.utils.extensions.filterList
import org.alertpreparedness.platform.v2.utils.extensions.mapList
import org.alertpreparedness.platform.v2.utils.extensions.success
import org.alertpreparedness.platform.v2.utils.extensions.withLatestFromPair
import org.alertpreparedness.platform.v2.utils.extensions.withLatestFromTriple
import org.joda.time.LocalDate
import java.util.Date

interface IBasePreparednessViewModel {
    interface Inputs {
        fun actionClicked(action: Action)
        fun onPreparednessOptionClicked(action: Action, option: PreparednessBottomSheetOption)
        fun onUpdateDueDate(dueDate: LocalDate)
    }

    interface Outputs {
        fun actions(): Observable<List<Action>>
        fun user(): Observable<User>
        fun showBottomSheet(): Observable<Pair<Action, List<PreparednessBottomSheetOption>>>
        fun showDatePicker(): Observable<Unit>
        fun showNotesActivity(): Observable<Pair<Action, String>>
        fun showDocumentsActivity(): Observable<Pair<Action, String>>

        fun updateDueDateSuccess(): Observable<Unit>
        fun updateDueDateFailure(): Observable<Unit>
    }
}

abstract class BasePreparednessViewModel : BaseViewModel(), Inputs, Outputs {

    private val actions: Observable<List<Action>>
    private val updateActionDueDate: Observable<Notification<Unit>>
    private val onActionClicked = PublishSubject.create<Action>()
    private val onPreparednessOptionClicked = PublishSubject.create<Pair<Action, PreparednessBottomSheetOption>>()
    private val onUpdateDueDate = PublishSubject.create<LocalDate>()

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

        updateActionDueDate = onUpdateDueDate
                .withLatestFromTriple(onActionClicked, userObservable)
                .flatMap { (dueDate, action, user) ->
                    val setDueDate = db.child("action")
                            .child(user.countryId)
                            .child(action.id)
                            .child("dueDate")
                            .setValueRx(dueDate.plusDays(1).toDateTimeAtStartOfDay().millis - 1)

                    val setUpdatedAtObs = db.child("action")
                            .child(user.countryId)
                            .child(action.id)
                            .child("updatedAt")
                            .setValueRx(Date().time)

                    setDueDate.combineWithPair(setUpdatedAtObs).map { Unit }
                            .materialize()
                }
                .share()

    }

    abstract fun filterAction(action: Action, user: User, hazards: List<HazardScenario>): Boolean

    abstract fun getSelectOptions(): List<PreparednessBottomSheetOption>

    override fun actionClicked(action: Action) {
        onActionClicked.onNext(action)
    }

    override fun onUpdateDueDate(dueDate: LocalDate) {
        onUpdateDueDate.onNext(dueDate)
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

    override fun showDocumentsActivity(): Observable<Pair<Action, String>> {
        return onPreparednessOptionClicked
                .filter { it.second == ATTACHMENTS }
                .map { it.first }
                .withLatestFromPair(userObservable)
                .map { (action, user) ->
                    Pair(action, user.countryId)
                }
    }

    override fun showNotesActivity(): Observable<Pair<Action, String>> {
        return onPreparednessOptionClicked
                .filter { it.second == NOTES }
                .map { it.first }
                .withLatestFromPair(userObservable)
                .map { (action, user) ->
                    Pair(action, user.countryId)
                }
    }

    override fun showDatePicker(): Observable<Unit> {
        return onPreparednessOptionClicked
                .filter {
                    it.second == UPDATE_DUE_DATE
                }
                .map { Unit }
    }

    override fun updateDueDateFailure(): Observable<Unit> {
        return updateActionDueDate.success()
    }

    override fun updateDueDateSuccess(): Observable<Unit> {
        return updateActionDueDate.failure(Unit)
    }
}
