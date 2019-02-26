package org.alertpreparedness.platform.v2.alert

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.alertpreparedness.platform.v2.alert.IAlertDetailViewModel.Inputs
import org.alertpreparedness.platform.v2.alert.IAlertDetailViewModel.Outputs
import org.alertpreparedness.platform.v2.base.BaseViewModel
import org.alertpreparedness.platform.v2.db
import org.alertpreparedness.platform.v2.models.Alert
import org.alertpreparedness.platform.v2.models.UserPublic
import org.alertpreparedness.platform.v2.models.UserType.COUNTRY_DIRECTOR
import org.alertpreparedness.platform.v2.models.enums.AlertApprovalState.APPROVED
import org.alertpreparedness.platform.v2.models.enums.AlertApprovalState.REJECTED
import org.alertpreparedness.platform.v2.models.enums.AlertApprovalState.WAITING_RESPONSE
import org.alertpreparedness.platform.v2.models.enums.AlertLevel
import org.alertpreparedness.platform.v2.models.enums.AlertLevel.AMBER
import org.alertpreparedness.platform.v2.models.enums.AlertLevel.GREEN
import org.alertpreparedness.platform.v2.models.enums.AlertLevel.RED
import org.alertpreparedness.platform.v2.models.enums.TimeTrackingLevel
import org.alertpreparedness.platform.v2.repository.Repository
import org.alertpreparedness.platform.v2.repository.Repository.userObservable
import org.alertpreparedness.platform.v2.repository.Repository.userPublic
import org.alertpreparedness.platform.v2.updateChildrenRx
import org.alertpreparedness.platform.v2.utils.Nullable
import org.alertpreparedness.platform.v2.utils.extensions.behavior
import org.alertpreparedness.platform.v2.utils.extensions.combineWithPair
import org.alertpreparedness.platform.v2.utils.extensions.isRedAlertRequested
import org.alertpreparedness.platform.v2.utils.extensions.takeWhen
import org.alertpreparedness.platform.v2.utils.extensions.updateTimeTrackingMap
import org.joda.time.DateTime
import java.util.Date

data class RequestBanner(
        val requestee: UserPublic,
        val previousLevel: AlertLevel,
        val requestedLevel: AlertLevel,
        val dateOfRequest: DateTime
)

interface IAlertDetailViewModel {
    interface Inputs {
        fun alertId(alertId: String)
        fun approveClicked()
        fun rejectClicked()
        fun editClicked()
    }

    interface Outputs {
        fun alert(): Observable<Alert>
        fun lastUpdated(): Observable<DateTime>
        fun showApproveReject(): Observable<Boolean>
        fun showRequestBanner(): Observable<Nullable<RequestBanner>>
        fun editAlert(): Observable<Alert>
    }
}

class AlertDetailViewModel : BaseViewModel(), Inputs, Outputs {

    private val alertId = BehaviorSubject.create<String>()
    private val approveClicked = PublishSubject.create<Unit>()
    private val rejectClicked = PublishSubject.create<Unit>()
    private val editClicked = PublishSubject.create<Unit>()

    private val alert: Observable<Alert>
    private var isRedRequested: Observable<Boolean>

    init {
        alert = alertId.flatMap { id ->
            Repository.alert(id)
        }
                .behavior()

        isRedRequested = alert
                .combineWithPair(userObservable)
                .map { (alert, user) ->
                    if (user.userType != COUNTRY_DIRECTOR) {
                        false
                    } else {
                        alert.state == WAITING_RESPONSE && alert.level == RED
                    }
                }

        disposables +=
                alert.combineWithPair(userObservable)
                        .takeWhen(
                                approveClicked
                        )
                        .flatMap { (alert, user) ->
                            db.child("alert")
                                    .child(user.countryId)
                                    .child(alert.id)
                                    .updateChildrenRx(
                                            mapOf(
                                                    "approval" to mapOf(
                                                            "countryDirector" to mapOf(
                                                                    user.countryId to APPROVED.value
                                                            )
                                                    ),
                                                    "timeUpdated" to Date().time,
                                                    "redAlertApproved" to true,
                                                    "reasonForRedAlert" to null,
                                                    "timeTracking" to alert.timeTracking.updateTimeTrackingMap(
                                                            TimeTrackingLevel.RED, true)
                                            )
                                    )
                        }
                        .subscribe()

        disposables +=
                alert.combineWithPair(userObservable)
                        .takeWhen(
                                rejectClicked
                        )
                        .flatMap { (alert, user) ->
                            db.child("alert")
                                    .child(user.countryId)
                                    .child(alert.id)
                                    .updateChildrenRx(
                                            mapOf(
                                                    "approval" to mapOf(
                                                            "countryDirector" to mapOf(
                                                                    user.countryId to REJECTED.value
                                                            )
                                                    ),
                                                    "timeUpdated" to Date().time,
                                                    "redAlertApproved" to false,
                                                    "alertLevel" to if (alert.previousIsAmber) AMBER.value else GREEN.value,
                                                    "reasonForRedAlert" to null
                                            )
                                    )
                        }
                        .subscribe()
    }

    override fun alertId(alertId: String) {
        this.alertId.onNext(alertId)
    }

    override fun alert(): Observable<Alert> {
        return alert
    }

    override fun lastUpdated(): Observable<DateTime> {
        return alert.map { it.updatedAt ?: it.createdAt }
    }

    override fun showApproveReject(): Observable<Boolean> {
        return isRedRequested
    }

    override fun showRequestBanner(): Observable<Nullable<RequestBanner>> {
        return alert
                .flatMap { alert ->
                    if (!alert.isRedAlertRequested()) {
                        Observable.just<Nullable<RequestBanner>>(Nullable())
                    } else {
                        userPublic(alert.updatedBy ?: alert.createdBy)
                                .map {
                                    Nullable(RequestBanner(
                                            it,
                                            if (alert.previousIsAmber) AMBER else GREEN,
                                            RED,
                                            alert.updatedAt ?: alert.createdAt
                                    ))
                                }
                    }
                }
    }

    override fun approveClicked() {
        this.approveClicked.onNext(Unit)
    }

    override fun rejectClicked() {
        this.rejectClicked.onNext(Unit)
    }

    override fun editClicked() {
        this.editClicked.onNext(Unit)
    }

    override fun editAlert(): Observable<Alert> {
        return alert
                .takeWhen(editClicked)
    }
}
