package org.alertpreparedness.platform.v2.preparedness

import io.reactivex.Notification
import io.reactivex.Observable
import io.reactivex.rxkotlin.combineLatest
import io.reactivex.subjects.PublishSubject
import org.alertpreparedness.platform.v1.utils.PermissionsHelper
import org.alertpreparedness.platform.v2.base.BaseViewModel
import org.alertpreparedness.platform.v2.db
import org.alertpreparedness.platform.v2.models.Action
import org.alertpreparedness.platform.v2.models.User
import org.alertpreparedness.platform.v2.models.enums.HazardScenario
import org.alertpreparedness.platform.v2.preparedness.IBasePreparednessViewModel.Inputs
import org.alertpreparedness.platform.v2.preparedness.IBasePreparednessViewModel.Outputs
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption.ASSIGN
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption.ATTACHMENTS
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption.COMPLETE
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption.EDIT
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption.NOTES
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption.REASSIGN
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption.UPDATE_DUE_DATE
import org.alertpreparedness.platform.v2.repository.Repository.actionsObservable
import org.alertpreparedness.platform.v2.repository.Repository.redAlertHazardScenariosObservable
import org.alertpreparedness.platform.v2.repository.Repository.userObservable
import org.alertpreparedness.platform.v2.setValueRx
import org.alertpreparedness.platform.v2.utils.Nullable
import org.alertpreparedness.platform.v2.utils.extensions.behavior
import org.alertpreparedness.platform.v2.utils.extensions.canBeAssigned
import org.alertpreparedness.platform.v2.utils.extensions.combineWithTriple
import org.alertpreparedness.platform.v2.utils.extensions.failure
import org.alertpreparedness.platform.v2.utils.extensions.getNewTimeTrackingLevel
import org.alertpreparedness.platform.v2.utils.extensions.success
import org.alertpreparedness.platform.v2.utils.extensions.updateTimeTracking
import org.alertpreparedness.platform.v2.utils.extensions.withLatestFromPair
import org.alertpreparedness.platform.v2.utils.extensions.withLatestFromTriple
import org.joda.time.DateTime
import org.joda.time.LocalDate
import java.util.Date

interface IBasePreparednessViewModel {
    interface Inputs {
        fun actionClicked(action: Action)
        fun onPreparednessOptionClicked(action: Action, option: PreparednessBottomSheetOption)
        fun onUpdateDueDate(dueDate: LocalDate)
        fun onAssignUser(userId: Nullable<String>)
    }

    interface Outputs {
        fun actions(): Observable<List<Action>>
        fun user(): Observable<User>
        fun showBottomSheet(): Observable<Pair<Action, List<PreparednessBottomSheetOption>>>
        fun showDatePicker(): Observable<Unit>
        fun showNotesActivity(): Observable<Pair<Action, String>>
        fun showDocumentsActivity(): Observable<Pair<Action, String>>
        fun showEditActivity(): Observable<Pair<Action, String>>
        fun showCompleteActivity(): Observable<Pair<Action, String>>
        fun completeActionPermissionError(): Observable<Unit>
        fun updateDueDateSuccess(): Observable<Unit>
        fun genericError(): Observable<Unit>
        fun assignAction(): Observable<Unit>
        fun assignActionSuccess(): Observable<Unit>
        fun assignActionPermissionError(): Observable<Unit>
        fun assignActionIncompleteError(): Observable<Unit>
    }
}

abstract class BasePreparednessViewModel : BaseViewModel(), Inputs, Outputs {

    private val actions: Observable<List<Action>>
    private val updateActionDueDate: Observable<Notification<Unit>>
    private val onActionClicked = PublishSubject.create<Action>()
    private val onPreparednessOptionClicked = PublishSubject.create<Pair<Action, PreparednessBottomSheetOption>>()
    private val onUpdateDueDate = PublishSubject.create<LocalDate>()
    private val onAssignUserId = PublishSubject.create<Nullable<String>>()
    private val permissions: Observable<PermissionsHelper>

    private val onAssignClickedWithPermissions: Observable<Pair<Action, PermissionsHelper>>

    private var userAssigned: Observable<Notification<Unit>>

    private var onCompleteClickedWithPermissions: Observable<Pair<Action, PermissionsHelper>>

    init {
        actions = actionsObservable
                .combineWithTriple(
                        userObservable,
                        redAlertHazardScenariosObservable
                )
                .map { (list, user, hazards) -> list.filter { filterAction(it, user, hazards) } }
                .behavior()


        permissions = userObservable
                .map {
                    PermissionsHelper(it.id)
                }
                .share()
                .behavior()

        onAssignClickedWithPermissions = onPreparednessOptionClicked
                .filter {
                    it.second == ASSIGN || it.second == REASSIGN
                }
                .map { it.first }
                .withLatestFromPair(permissions)
                .share()

        onCompleteClickedWithPermissions = onPreparednessOptionClicked
                .filter { it.second == COMPLETE }
                .map { it.first }
                .withLatestFromPair(permissions)
                .share()

        updateActionDueDate = onUpdateDueDate
                .withLatestFromPair(onActionClicked)
                .withLatestFromTriple(redAlertHazardScenariosObservable, userObservable)
                .flatMap { (dueDateAction, hazards, user) ->
                    val (dueDate, action) = dueDateAction
                    listOf(
                            action.updateTimeTracking(user.countryId,
                                    action.getNewTimeTrackingLevel(hazards, updatedAt = DateTime.now())),
                            db.child("action")
                                    .child(user.countryId)
                                    .child(action.id)
                                    .child("dueDate")
                                    .setValueRx(dueDate.plusDays(1).toDateTimeAtStartOfDay().millis - 1),
                            db.child("action")
                                    .child(user.countryId)
                                    .child(action.id)
                                    .child("updatedAt")
                                    .setValueRx(Date().time)

                    )
                            .combineLatest { Unit }
                            .materialize()
                }
                .share()
        userAssigned = onAssignUserId
                .withLatestFromPair(onActionClicked)
                .withLatestFromTriple(redAlertHazardScenariosObservable, userObservable)
                .flatMap { (assigneeAction, hazards, user) ->
                    val (assigneeId, action) = assigneeAction
                    listOf(
                            action.updateTimeTracking(user.countryId,
                                    action.getNewTimeTrackingLevel(hazards, updatedAt = DateTime.now(),
                                            assignee = assigneeId.value)),
                            db.child("action")
                                    .child(user.countryId)
                                    .child(action.id)
                                    .child("asignee")
                                    .setValueRx(assigneeId.value),

                            db.child("action")
                                    .child(user.countryId)
                                    .child(action.id)
                                    .child("updatedAt")
                                    .setValueRx(Date().time)
                    )
                            .combineLatest { Unit }
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

    override fun updateDueDateSuccess(): Observable<Unit> {
        return updateActionDueDate.success()
    }

    override fun assignAction(): Observable<Unit> {
        return onAssignClickedWithPermissions
                .filter { (action, perms) ->
                    perms.checkAssign(action) && action.canBeAssigned()
                }
                .map { Unit }
    }

    override fun assignActionPermissionError(): Observable<Unit> {
        return onAssignClickedWithPermissions
                .filter { (action, perms) ->
                    !perms.checkAssign(action)
                }
                .map { Unit }
    }

    override fun assignActionIncompleteError(): Observable<Unit> {
        return onAssignClickedWithPermissions
                .filter { (action, perms) ->
                    perms.checkAssign(action) && !action.canBeAssigned()
                }
                .map { Unit }
    }

    override fun assignActionSuccess(): Observable<Unit> {
        return userAssigned.success()
    }

    override fun onAssignUser(userId: Nullable<String>) {
        onAssignUserId.onNext(userId)
    }

    override fun genericError(): Observable<Unit> {
        return Observable.merge(
                updateActionDueDate.failure(Unit),
                userAssigned.failure(Unit)
        )
    }

    override fun showCompleteActivity(): Observable<Pair<Action, String>> {
        return onCompleteClickedWithPermissions
                .filter { (action, perms) ->
                    perms.checkCompleteAction(action)
                }
                .map { it.first }
                .withLatestFromPair(userObservable.map { it.countryId })
    }

    override fun completeActionPermissionError(): Observable<Unit> {
        return onCompleteClickedWithPermissions
                .filter { (action, perms) ->
                    !perms.checkCompleteAction(action)
                }
                .map { Unit }
    }

    override fun showEditActivity(): Observable<Pair<Action, String>> {
        return onPreparednessOptionClicked
                .filter { it.second == EDIT }
                .map { it.first }
                .withLatestFromPair(userObservable)
                .map { (action, user) -> Pair(action, user.countryId) }
    }
}
