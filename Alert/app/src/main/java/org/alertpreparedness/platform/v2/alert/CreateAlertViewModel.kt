package org.alertpreparedness.platform.v2.alert

import io.reactivex.Observable
import io.reactivex.functions.Function7
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
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
import org.alertpreparedness.platform.v2.db
import org.alertpreparedness.platform.v2.models.Alert
import org.alertpreparedness.platform.v2.models.Area
import org.alertpreparedness.platform.v2.models.UserType.COUNTRY_DIRECTOR
import org.alertpreparedness.platform.v2.models.enums.AlertApprovalState.APPROVED
import org.alertpreparedness.platform.v2.models.enums.AlertApprovalState.WAITING_RESPONSE
import org.alertpreparedness.platform.v2.models.enums.AlertLevel
import org.alertpreparedness.platform.v2.models.enums.AlertLevel.RED
import org.alertpreparedness.platform.v2.models.enums.HazardScenario
import org.alertpreparedness.platform.v2.repository.Repository.alert
import org.alertpreparedness.platform.v2.repository.Repository.userObservable
import org.alertpreparedness.platform.v2.setValueRx
import org.alertpreparedness.platform.v2.updateChildrenRx
import org.alertpreparedness.platform.v2.utils.Add
import org.alertpreparedness.platform.v2.utils.Nullable
import org.alertpreparedness.platform.v2.utils.Remove
import org.alertpreparedness.platform.v2.utils.extensions.behavior
import org.alertpreparedness.platform.v2.utils.extensions.combineWithPair
import org.alertpreparedness.platform.v2.utils.extensions.isRedAlertRequested
import org.alertpreparedness.platform.v2.utils.extensions.takeWhen
import org.alertpreparedness.platform.v2.utils.filterNull
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
    }
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

abstract class CreateAlertViewModel : BaseViewModel(), Inputs, Outputs {

    private val baseAlertId = BehaviorSubject.create<Nullable<String>>()
    private val inputHazardScenario = BehaviorSubject.createDefault(Nullable<HazardScenario>())
    private val inputAlertLevel = BehaviorSubject.createDefault(Nullable<AlertLevel>())
    private val inputRedAlertReason = BehaviorSubject.createDefault(Nullable<String>())
    private val inputPopulationAffected = BehaviorSubject.createDefault(Nullable<Long>())
    private val affectedAreaAdded = PublishSubject.create<Area>()
    private val affectedAreaRemoved = PublishSubject.create<Area>()
    private val inputInformationSources = BehaviorSubject.createDefault(Nullable<String>())
    private val onSaveClicked = PublishSubject.create<Unit>()
    private val onAddAreaClicked = PublishSubject.create<Unit>()

    private val baseAlert: Observable<Nullable<Alert>>
    private val inputAffectedAreas: Observable<Nullable<MutableList<Area>>>
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

        inputAffectedAreas = Observable.merge(
                affectedAreaAdded.map { Add(it) },
                affectedAreaRemoved.map { Remove(it) }
        )
                .scan(Nullable()) { nullableList, newItem ->
                    val list = nullableList.value ?: mutableListOf()

                    when (newItem) {
                        is Add -> list += newItem.value
                        is Remove -> list -= newItem.value
                    }

                    Nullable(list)
                }

        saveState = Observable.combineLatest<Nullable<Alert>, Nullable<HazardScenario>, Nullable<AlertLevel>, Nullable<String>, Nullable<Long>, Nullable<MutableList<Area>>, Nullable<String>, AlertSaveModel>(
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
                            inputAffectedAreasNullable.value,
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
                                "hazardScenario" to saveModel.hazardScenario,
                                "alertLevel" to saveModel.alertLevel,
                                "reasonForRedAlert" to saveModel.redAlertReason,
                                "estimatedPopulation" to saveModel.populationAffected,
                                "affectedAreas" to generateAffectedAreasMap(saveModel.affectedAreas),
                                "infoNotes" to saveModel.infoNotes,
                                "updatedBy" to user.id
                        )

                        if (saveModel.baseAlert.level != saveModel.alertLevel) {
                            var newState = APPROVED
                            if (saveModel.alertLevel == RED) {
                                if (user.userType != COUNTRY_DIRECTOR) {
                                    newState = WAITING_RESPONSE
                                } else {
                                    updateMap["redAlertApproved"] = true
                                }
                            }
                            updateMap["approval"] = mapOf(
                                    "countryDirector" to mapOf(
                                            user.countryId to newState
                                    )
                            )
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
                                "hazardScenario" to saveModel.hazardScenario,
                                "alertLevel" to saveModel.alertLevel,
                                "reasonForRedAlert" to saveModel.redAlertReason,
                                "estimatedPopulation" to saveModel.populationAffected,
                                "affectedAreas" to generateAffectedAreasMap(saveModel.affectedAreas),
                                "infoNotes" to saveModel.infoNotes,
                                "createdBy" to user.id,
                                "timeCreated" to Date().time
                        )

                        var approvalState = APPROVED
                        if (saveModel.alertLevel == RED) {
                            if (user.userType != COUNTRY_DIRECTOR) {
                                approvalState = WAITING_RESPONSE
                            } else {
                                createMap["redAlertApproved"] = true
                            }
                        }
                        createMap["approval"] = mapOf(
                                "countryDirector" to mapOf(
                                        user.countryId to approvalState
                                )
                        )

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
                .behavior()
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

    override fun hazardScenario(): Observable<HazardScenario> {
        return getCurrentValueObservable(inputHazardScenario) {
            it?.hazardScenario
        }
    }

    override fun alertLevel(): Observable<AlertLevel> {
        return getCurrentValueObservable(inputAlertLevel) {
            it?.level
        }
    }

    override fun showRedAlertReason(): Observable<Boolean> {
        return baseAlert
                .combineWithPair(inputAlertLevel)
                .map { (baseAlert, alertLevel) ->
                    baseAlert.isNull() || !baseAlert.value!!.isRedAlertRequested() && alertLevel.value == RED
                }
    }

    override fun populationAffected(): Observable<Long> {
        return getCurrentValueObservable(inputPopulationAffected) {
            it?.estimatedPopulation
        }
    }

    override fun affectedAreas(): Observable<List<Area>> {
        return getCurrentValueObservable(inputAffectedAreas.map { Nullable(it.value?.toList()) }) {
            it?.affectedAreas
        }
    }

    override fun informationSources(): Observable<String> {
        return getCurrentValueObservable(inputInformationSources) {
            it?.infoNotes
        }
    }

    private fun <T> getCurrentValueObservable(inputObservable: Observable<Nullable<T>>,
            valueFromBase: (Alert?) -> T?): Observable<T> {
        return baseAlert
                .combineWithPair(inputObservable)
                .map { (baseAlert, inputValue) ->
                    Nullable(inputValue.value ?: valueFromBase(baseAlert.value))
                }
                .take(1)
                .filterNull()
    }

    override fun onSaveClicked() {
        onSaveClicked.onNext(Unit)
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

    override fun onAddAreaClicked() {
        onAddAreaClicked.onNext(Unit)
    }
}

