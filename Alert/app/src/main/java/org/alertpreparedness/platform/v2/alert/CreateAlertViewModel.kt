package org.alertpreparedness.platform.v2.alert

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function7
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.alertpreparedness.platform.v2.alert.ButtonState.CONFIRM_LEVEL
import org.alertpreparedness.platform.v2.alert.ButtonState.CONFIRM_RED_LEVEL
import org.alertpreparedness.platform.v2.alert.ButtonState.REQUEST_RED_LEVEL
import org.alertpreparedness.platform.v2.alert.ButtonState.SAVE_CHANGES
import org.alertpreparedness.platform.v2.alert.ICreateAlertViewModel.Inputs
import org.alertpreparedness.platform.v2.alert.ICreateAlertViewModel.Outputs
import org.alertpreparedness.platform.v2.alert.SaveState.MISSING_AREAS
import org.alertpreparedness.platform.v2.alert.SaveState.MISSING_HAZARD
import org.alertpreparedness.platform.v2.alert.SaveState.MISSING_INFORMATION
import org.alertpreparedness.platform.v2.alert.SaveState.MISSING_LEVEL
import org.alertpreparedness.platform.v2.alert.SaveState.MISSING_POPULATION
import org.alertpreparedness.platform.v2.alert.SaveState.MISSING_RED_ALERT_REASON
import org.alertpreparedness.platform.v2.alert.SaveState.SUCCESS
import org.alertpreparedness.platform.v2.base.BaseViewModel
import org.alertpreparedness.platform.v2.models.Alert
import org.alertpreparedness.platform.v2.models.Area
import org.alertpreparedness.platform.v2.models.UserType.COUNTRY_DIRECTOR
import org.alertpreparedness.platform.v2.models.enums.AlertApprovalState.APPROVED
import org.alertpreparedness.platform.v2.models.enums.AlertApprovalState.WAITING_RESPONSE
import org.alertpreparedness.platform.v2.models.enums.AlertLevel
import org.alertpreparedness.platform.v2.models.enums.AlertLevel.AMBER
import org.alertpreparedness.platform.v2.models.enums.AlertLevel.RED
import org.alertpreparedness.platform.v2.models.enums.HazardScenario
import org.alertpreparedness.platform.v2.models.enums.TimeTrackingLevel
import org.alertpreparedness.platform.v2.repository.Repository.alert
import org.alertpreparedness.platform.v2.repository.Repository.db
import org.alertpreparedness.platform.v2.repository.Repository.userObservable
import org.alertpreparedness.platform.v2.setValueRx
import org.alertpreparedness.platform.v2.updateChildrenRx
import org.alertpreparedness.platform.v2.utils.Nullable
import org.alertpreparedness.platform.v2.utils.extensions.combineWithPair
import org.alertpreparedness.platform.v2.utils.extensions.combineWithTriple
import org.alertpreparedness.platform.v2.utils.extensions.isRedAlertApproved
import org.alertpreparedness.platform.v2.utils.extensions.isRedAlertRequested
import org.alertpreparedness.platform.v2.utils.extensions.newTimeTracking
import org.alertpreparedness.platform.v2.utils.extensions.takeWhen
import org.alertpreparedness.platform.v2.utils.extensions.toTimeTrackingLevel
import org.alertpreparedness.platform.v2.utils.extensions.updateTimeTrackingMap
import org.alertpreparedness.platform.v2.utils.filterNull
import org.joda.time.DateTime
import java.util.Date

interface ICreateAlertViewModel {
    interface Inputs {
        fun baseAlert(alertId: String? = null)
        fun onHazardScenarioUpdate(hazardScenario: HazardScenario)
        fun onAlertLevelUpdate(alertLevel: AlertLevel)
        fun onPopulationAffectedUpdate(population: Long)
        fun onAreaAdded(area: Area)
        fun onAreaRemoved(area: Area)
        fun onRedAlertReasonUpdate(reason: String)
        fun onInformationSourcesUpdate(information: String)
        fun onSaveClicked()
        fun onAddAreaClicked()
        fun onHazardScenarioClicked()
        fun onAlertLevelClicked()
    }

    interface Outputs {
        fun hazardScenario(): Observable<HazardScenario>
        fun alertLevel(): Observable<AlertLevel>
        fun populationAffected(): Observable<Long>
        fun affectedAreas(): Observable<List<Area>>
        fun informationSources(): Observable<String>
        fun redAlertReason(): Observable<String>
        fun showRedAlertReason(): Observable<Boolean>
        fun saveState(): Observable<SaveState>
        fun addArea(): Observable<Unit>
        fun selectAlertLevel(): Observable<Unit>
        fun selectHazardScenario(): Observable<Unit>
        fun buttonState(): Observable<ButtonState>
        fun baseAlert(): Observable<Nullable<Alert>>
    }
}

enum class ButtonState {
    CONFIRM_LEVEL,
    CONFIRM_RED_LEVEL,
    SAVE_CHANGES,
    REQUEST_RED_LEVEL
}

enum class SaveState {
    SUCCESS,
    MISSING_HAZARD,
    MISSING_LEVEL,
    MISSING_RED_ALERT_REASON,
    MISSING_POPULATION,
    MISSING_AREAS,
    MISSING_INFORMATION,
}

class CreateAlertViewModel : BaseViewModel(), Inputs, Outputs {
    private val baseAlertId = BehaviorSubject.create<Nullable<String>>()
    private val inputHazardScenario = BehaviorSubject.createDefault(Nullable<HazardScenario>())
    private val inputAlertLevel = BehaviorSubject.createDefault(Nullable<AlertLevel>())
    private val inputRedAlertReason = BehaviorSubject.createDefault(Nullable<String>())
    private val inputPopulationAffected = BehaviorSubject.createDefault(Nullable<Long>())
    private val affectedAreaAdded = BehaviorSubject.create<Area>()
    private val inputAffectedAreas: Observable<Nullable<Set<Area>>>
    private val affectedAreaRemoved = BehaviorSubject.create<Area>()
    private val inputInformationSources = BehaviorSubject.createDefault(Nullable<String>())
    private val onSaveClicked = PublishSubject.create<Unit>()
    private val onAddAreaClicked = PublishSubject.create<Unit>()
    private val onHazardScenarioClicked = PublishSubject.create<Unit>()
    private val onAlertLevelClicked = PublishSubject.create<Unit>()

    private val baseAlert: Observable<Nullable<Alert>>
    private val saveState: Observable<SaveState>

    data class AlertSaveModel(
            val baseAlert: Alert?,
            val hazardScenario: HazardScenario?,
            val alertLevel: AlertLevel?,
            val redAlertReason: String?,
            val populationAffected: Long?,
            val affectedAreas: List<Area>?,
            val infoNotes: String?
    )

    init {
        baseAlert = baseAlertId
                .concatMap {
                    val id = it.value
                    if (id == null) {
                        Observable.just(Nullable())
                    } else {
                        alert(id)
                                .map { alert -> Nullable(alert) }
                    }
                }

        inputAffectedAreas = baseAlert
                .filterNull()
                .map { it.affectedAreas.toSet() }
                .accumulateWithNullable(affectedAreaAdded, affectedAreaRemoved)


        saveState = Observable.combineLatest<Nullable<Alert>, Nullable<HazardScenario>, Nullable<AlertLevel>, Nullable<String>, Nullable<Long>, Nullable<Set<Area>>, Nullable<String>, AlertSaveModel>(
                baseAlert,
                inputHazardScenario,
                inputAlertLevel,
                inputRedAlertReason,
                inputPopulationAffected,
                inputAffectedAreas,
                inputInformationSources,
                Function7 { baseAlertNullable, inputHazardScenarioNullable, inputAlertLevelNullable, inputRedAlertReasonNullable, inputPopulationAffectedNullable, inputAffectedAreasNullable, inputInformationSourcesNullable ->
                    val baseAlert = baseAlertNullable.value
                    AlertSaveModel(
                            baseAlert,
                            inputHazardScenarioNullable.value,
                            inputAlertLevelNullable.value,
                            inputRedAlertReasonNullable.value,
                            inputPopulationAffectedNullable.value,
                            inputAffectedAreasNullable.value?.toList(),
                            inputInformationSourcesNullable.value
                    )
                }
        )
                .combineWithPair(userObservable)
                .takeWhen(onSaveClicked)
                .switchMap { (saveModel, user) ->
                    if (saveModel.redAlertReason == null && saveModel.alertLevel == RED && saveModel.baseAlert?.level != RED) {
                        Observable.just(MISSING_RED_ALERT_REASON)
                    }
                    //SAVE EDITS
                    else if (saveModel.baseAlert != null) {
                        val updateMap = mutableMapOf(
                                "hazardScenario" to saveModel.hazardScenario?.value,
                                "alertLevel" to saveModel.alertLevel?.value,
                                "reasonForRedAlert" to saveModel.redAlertReason,
                                "estimatedPopulation" to saveModel.populationAffected,
                                "affectedAreas" to generateAffectedAreasMap(saveModel.affectedAreas),
                                "infoNotes" to saveModel.infoNotes,
                                "updatedBy" to user.id,
                                "timeUpdated" to DateTime().millis
                        )
                                .filterValues { it != null }
                                .toMutableMap()

                        //If we've updated the alert level
                        if (saveModel.baseAlert.level != saveModel.alertLevel && saveModel.alertLevel != null) {
                            var timeTrackingLevel = saveModel.alertLevel.toTimeTrackingLevel()
                            var newState = APPROVED

                            //If we've updated the alert level to red
                            if (saveModel.alertLevel == RED) {
                                //Don't set state to waiting response if the current user isn't a country director
                                if (user.userType != COUNTRY_DIRECTOR) {
                                    newState = WAITING_RESPONSE
                                    //We're waiting for a response from a Country Director. Time tracking level shouldn't change while we wait.
                                    timeTrackingLevel = if (saveModel.baseAlert.level == AMBER) TimeTrackingLevel.AMBER else TimeTrackingLevel.GREEN
                                    //If we're moving away from amber, we need to store this in a flag
                                    if (saveModel.baseAlert.level == AMBER) {
                                        updateMap["previousIsAmber"] = true
                                    }
                                } else {
                                    updateMap["redAlertApproved"] = true
                                }
                            }
                            //If we were in red and we're updating away from red, delete reason for red alert
                            else if (saveModel.baseAlert.level != RED) {
                                updateMap["reasonForRedAlert"] = null
                            }

                            updateMap["approval"] = mapOf(
                                    "countryDirector" to mapOf(
                                            user.countryId to newState.value
                                    )
                            )

                            updateMap["timeTracking"] = saveModel.baseAlert.timeTracking.updateTimeTrackingMap(
                                    timeTrackingLevel, true)
                        }

                        db.child("alert")
                                .child(user.countryId)
                                .child(saveModel.baseAlert.id)
                                .updateChildrenRx(
                                        updateMap.filterValues { it != null }
                                )
                                .map {
                                    SUCCESS
                                }
                    } else if (saveModel.hazardScenario == null) {
                        Observable.just(MISSING_HAZARD)
                    } else if (saveModel.alertLevel == null) {
                        Observable.just(MISSING_LEVEL)
                    } else if (saveModel.populationAffected == null) {
                        Observable.just(MISSING_POPULATION)
                    } else if (saveModel.affectedAreas == null) {
                        Observable.just(MISSING_AREAS)
                    } else if (saveModel.infoNotes == null) {
                        Observable.just(MISSING_INFORMATION)
                    }
                    //SAVE NEW ALERT
                    else {
                        val createMap = mutableMapOf(
                                "hazardScenario" to saveModel.hazardScenario.value,
                                "alertLevel" to saveModel.alertLevel.value,
                                "reasonForRedAlert" to saveModel.redAlertReason,
                                "estimatedPopulation" to saveModel.populationAffected,
                                "affectedAreas" to generateAffectedAreasMap(saveModel.affectedAreas),
                                "infoNotes" to saveModel.infoNotes,
                                "createdBy" to user.id,
                                "timeCreated" to Date().time
                        )

                        var approvalState = APPROVED
                        var timeTrackingLevel = saveModel.alertLevel.toTimeTrackingLevel()
                        if (saveModel.alertLevel == RED) {
                            if (user.userType != COUNTRY_DIRECTOR) {
                                timeTrackingLevel = TimeTrackingLevel.GREEN
                                approvalState = WAITING_RESPONSE
                            } else {
                                createMap["redAlertApproved"] = true
                            }
                        }
                        createMap["approval"] = mapOf(
                                "countryDirector" to mapOf(
                                        user.countryId to approvalState.value
                                )
                        )

                        createMap["timeTracking"] = newTimeTracking(timeTrackingLevel, true)

                        db.child("alert")
                                .child(user.countryId)
                                .push()
                                .setValueRx(
                                        createMap.filterValues { it != null }
                                )
                                .map {
                                    SUCCESS
                                }
                    }
                }
    }

    private fun generateAffectedAreasMap(affectedAreas: List<Area>?): List<Map<String, Any>>? {
        return affectedAreas?.map {
            mapOf(
                    "country" to it.country.value,
                    "level1" to (it.level1 ?: -1),
                    "level2" to (it.level2 ?: -1)
            )
        }
    }

    override fun baseAlert(alertId: String?) {
        baseAlertId.onNext(Nullable(alertId))
    }

    override fun onHazardScenarioUpdate(hazardScenario: HazardScenario) {
        inputHazardScenario.onNext(Nullable(hazardScenario))
    }

    override fun onSaveClicked() {
        onSaveClicked.onNext(Unit)
    }

    override fun onAlertLevelUpdate(alertLevel: AlertLevel) {
        inputAlertLevel.onNext(Nullable(alertLevel))
    }

    override fun onPopulationAffectedUpdate(population: Long) {
        inputPopulationAffected.onNext(Nullable(population))
    }

    override fun onAreaAdded(area: Area) {
        affectedAreaAdded.onNext(area)
    }

    override fun onAreaRemoved(area: Area) {
        affectedAreaRemoved.onNext(area)
    }

    override fun onInformationSourcesUpdate(information: String) {
        inputInformationSources.onNext(Nullable(information))
    }

    override fun onRedAlertReasonUpdate(reason: String) {
        inputRedAlertReason.onNext(Nullable(reason))
    }

    override fun onAddAreaClicked() {
        onAddAreaClicked.onNext(Unit)
    }

    override fun onHazardScenarioClicked() {
        onHazardScenarioClicked.onNext(Unit)
    }

    override fun onAlertLevelClicked() {
        onAlertLevelClicked.onNext(Unit)
    }

    override fun hazardScenario(): Observable<HazardScenario> {
        return getCurrentValueObservable(inputHazardScenario, false) {
            it?.hazardScenario
        }
    }

    override fun alertLevel(): Observable<AlertLevel> {
        return getCurrentValueObservable(inputAlertLevel, false) {
            it?.level
        }
    }

    override fun showRedAlertReason(): Observable<Boolean> {
        return baseAlert
                .combineWithPair(inputAlertLevel)
                .map { (baseAlert, alertLevel) ->
                    (baseAlert.isNotNull() && !baseAlert.value!!.isRedAlertRequested() && alertLevel.value == RED) ||
                            baseAlert.isNull() && alertLevel.value == RED
                }
    }

    override fun populationAffected(): Observable<Long> {
        return getCurrentValueObservable(inputPopulationAffected) {
            it?.estimatedPopulation
        }
    }

    override fun affectedAreas(): Observable<List<Area>> {
        return getCurrentValueObservable(inputAffectedAreas.map { Nullable(it.value?.toList()) }, false) {
            it?.affectedAreas
        }
    }

    override fun informationSources(): Observable<String> {
        return getCurrentValueObservable(inputInformationSources) {
            it?.infoNotes
        }
    }

    private fun <T> getCurrentValueObservable(inputObservable: Observable<Nullable<T>>, onlyTakeOne: Boolean = true,
            valueFromBase: (Alert?) -> T?): Observable<T> {
        var observable = baseAlert
                .combineWithPair(inputObservable)
                .map { (baseAlert, inputValue) ->
                    Nullable(inputValue.value ?: valueFromBase(baseAlert.value))
                }

        if (onlyTakeOne) {
            observable = observable.take(1)
        }
        return observable.filterNull()
    }

    override fun redAlertReason(): Observable<String> {
        return getCurrentValueObservable(inputRedAlertReason) {
            it?.reasonForRedAlert
        }
    }

    override fun saveState(): Observable<SaveState> {
        return saveState
    }

    override fun addArea(): Observable<Unit> {
        return onAddAreaClicked
    }

    override fun selectAlertLevel(): Observable<Unit> {
        return onAlertLevelClicked
    }

    override fun selectHazardScenario(): Observable<Unit> {
        return onHazardScenarioClicked
    }

    override fun buttonState(): Observable<ButtonState> {
        return baseAlert
                .combineWithTriple(inputAlertLevel, userObservable)
                .map { (baseAlert, inputAlertLevel, user) ->
                    if (baseAlert.isNull()) {
                        if (inputAlertLevel.value == RED && user.userType != COUNTRY_DIRECTOR) {
                            CONFIRM_RED_LEVEL
                        } else {
                            CONFIRM_LEVEL
                        }
                    } else {
                        val baseAlertValue = baseAlert.value!!
                        if (!(baseAlertValue.isRedAlertApproved() || baseAlert.value.isRedAlertRequested()) && inputAlertLevel.value == RED && user.userType != COUNTRY_DIRECTOR) {
                            REQUEST_RED_LEVEL
                        } else {
                            SAVE_CHANGES
                        }
                    }
                }
    }

    override fun baseAlert(): Observable<Nullable<Alert>> {
        return baseAlert
    }
}

fun <T> Observable<Set<T>>.accumulateWithNullable(add: Observable<T>,
        remove: Observable<T>): Observable<Nullable<Set<T>>> {
    return BehaviorAccumulatorObservable(this, add, remove)
}

class BehaviorAccumulatorObservable<T>(val source: Observable<Set<T>>, val add: Observable<T>,
        val remove: Observable<T>) : Observable<Nullable<Set<T>>>(), Observer<Set<T>> {

    var cached: Nullable<Set<T>> = Nullable()
    val observers = mutableListOf<Observer<in Nullable<Set<T>>>>()
    var sourceDisposable: Disposable? = null
    val disposables = mutableListOf<Disposable>()
    var locked = false

    override fun subscribeActual(observer: Observer<in Nullable<Set<T>>>) {
        observer.onNext(cached)

        observer.onSubscribe(ChildDisposable(observer))
        observers.add(observer)

        if (observers.count() == 1) {
            source.subscribe(this)
            disposables += add.subscribe {
                onAdd(it)
            }
            disposables += remove.subscribe {
                onRemove(it)
            }
        }
    }

    private fun onRemove(t: T) {
        cached = Nullable((cached.value ?: mutableSetOf()) - t)
        observers.forEach {
            it.onNext(cached)
        }
    }

    private fun onAdd(t: T) {
        cached = Nullable((cached.value ?: mutableSetOf()) + t)
        observers.forEach {
            it.onNext(cached)
        }
    }

    private fun dispose(observer: Observer<in Nullable<Set<T>>>) {
        observers.remove(observer)
        if (observers.size == 0) {
            sourceDisposable?.dispose()
        }
    }

    inner class ChildDisposable(val observer: Observer<in Nullable<Set<T>>>) : Disposable {

        override fun isDisposed(): Boolean {
            return observers.contains(observer)
        }

        override fun dispose() {
            dispose(observer)
        }
    }

    //region Observer
    override fun onSubscribe(d: Disposable) {
        sourceDisposable = d
    }

    override fun onNext(t: Set<T>) {
        if (!locked) {
            cached = Nullable((cached.value ?: mutableSetOf()) + t)
            observers.forEach {
                it.onNext(cached)
            }
        }
        locked = true
    }

    override fun onComplete() {
//        observers.forEach { it.onComplete() }
//        observers.clear()
    }

    override fun onError(e: Throwable) {
        observers.forEach { it.onError(e) }
        observers.clear()
    }
    //endregion
}

//        emitter.onNext(Nullable())
//
//        val disposables = mutableListOf<Disposable>()
//        var locked = false
//
//
//        disposables += add.subscribe {
//            locked = true
//            list.add(it)
//            emitter.onNext(Nullable(list))
//        }
//
//        disposables += remove.subscribe {
//            locked = true
//            list.remove(it)
//            emitter.onNext(Nullable(list))
//        }
//
//        emitter.onDispose {
//            disposables.forEach { it.dispose() }
//        }
