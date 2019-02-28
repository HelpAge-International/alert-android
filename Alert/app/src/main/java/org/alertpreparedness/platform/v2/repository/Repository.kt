package org.alertpreparedness.platform.v2.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.JsonObject
import io.reactivex.Observable
import io.reactivex.rxkotlin.combineLatest
import org.alertpreparedness.platform.v1.BuildConfig
import org.alertpreparedness.platform.v2.FirebaseAuthExtensions
import org.alertpreparedness.platform.v2.asObservable
import org.alertpreparedness.platform.v2.models.Action
import org.alertpreparedness.platform.v2.models.Agency
import org.alertpreparedness.platform.v2.models.Alert
import org.alertpreparedness.platform.v2.models.ClockSetting
import org.alertpreparedness.platform.v2.models.ClockSettingsSource.ACTION
import org.alertpreparedness.platform.v2.models.ClockSettingsSource.COUNTRY
import org.alertpreparedness.platform.v2.models.Hazard
import org.alertpreparedness.platform.v2.models.Indicator
import org.alertpreparedness.platform.v2.models.PrivacySetting
import org.alertpreparedness.platform.v2.models.Programme
import org.alertpreparedness.platform.v2.models.ResponsePlan
import org.alertpreparedness.platform.v2.models.User
import org.alertpreparedness.platform.v2.models.UserPublic
import org.alertpreparedness.platform.v2.models.UserType
import org.alertpreparedness.platform.v2.models.UserType.COUNTRY_ADMIN
import org.alertpreparedness.platform.v2.models.UserType.COUNTRY_DIRECTOR
import org.alertpreparedness.platform.v2.models.UserType.ERT
import org.alertpreparedness.platform.v2.models.UserType.ERT_LEADER
import org.alertpreparedness.platform.v2.models.UserType.PARTNER
import org.alertpreparedness.platform.v2.models.enums.ActionType
import org.alertpreparedness.platform.v2.models.enums.ActionType.CHS
import org.alertpreparedness.platform.v2.models.enums.ActionType.MANDATED
import org.alertpreparedness.platform.v2.models.enums.AlertApprovalState.APPROVED
import org.alertpreparedness.platform.v2.models.enums.AlertApprovalStateSerializer
import org.alertpreparedness.platform.v2.models.enums.AlertLevel.RED
import org.alertpreparedness.platform.v2.models.enums.Country
import org.alertpreparedness.platform.v2.models.enums.CountryOffice
import org.alertpreparedness.platform.v2.models.enums.DurationType.WEEK
import org.alertpreparedness.platform.v2.models.enums.DurationTypeSerializer
import org.alertpreparedness.platform.v2.models.enums.HazardScenario
import org.alertpreparedness.platform.v2.models.enums.Note
import org.alertpreparedness.platform.v2.models.enums.Privacy
import org.alertpreparedness.platform.v2.repository.Repository.PrivacySettingType.OFFICE_PROFILE
import org.alertpreparedness.platform.v2.utils.Nullable
import org.alertpreparedness.platform.v2.utils.ResettableLazyManager
import org.alertpreparedness.platform.v2.utils.extensions.behavior
import org.alertpreparedness.platform.v2.utils.extensions.childKeys
import org.alertpreparedness.platform.v2.utils.extensions.combineFlatten
import org.alertpreparedness.platform.v2.utils.extensions.combineWithPair
import org.alertpreparedness.platform.v2.utils.extensions.combineWithTriple
import org.alertpreparedness.platform.v2.utils.extensions.filterList
import org.alertpreparedness.platform.v2.utils.extensions.firstChild
import org.alertpreparedness.platform.v2.utils.extensions.firstChildKey
import org.alertpreparedness.platform.v2.utils.extensions.get
import org.alertpreparedness.platform.v2.utils.extensions.mapList
import org.alertpreparedness.platform.v2.utils.extensions.mapListNotNull
import org.alertpreparedness.platform.v2.utils.extensions.mergeCombine
import org.alertpreparedness.platform.v2.utils.extensions.print
import org.alertpreparedness.platform.v2.utils.extensions.toMergedModel
import org.alertpreparedness.platform.v2.utils.extensions.toModel
import org.alertpreparedness.platform.v2.utils.extensions.withLatestFromPair
import org.alertpreparedness.platform.v2.utils.filterNull
import org.alertpreparedness.platform.v2.utils.resettableLazy


object Repository {

    private val resettableLazyManager = ResettableLazyManager()

    fun reset() {
        resettableLazyManager.reset()
    }

    val db by resettableLazy(resettableLazyManager) {
        FirebaseDatabase.getInstance().reference.child(BuildConfig.ROOT_NODE)
    }
    val refUserPublic by resettableLazy(resettableLazyManager) { db.child("userPublic") }
    val refCountryAdmin by resettableLazy(resettableLazyManager) { db.child("administratorCountry") }
    val refCountryDirector by resettableLazy(resettableLazyManager) { db.child("countryDirector") }
    val refErt by resettableLazy(resettableLazyManager) { db.child("ert") }
    val refErtLeader by resettableLazy(resettableLazyManager) { db.child("ertLeader") }
    val refPartner by resettableLazy(resettableLazyManager) { db.child("partner") }
    val refPartnerUser by resettableLazy(resettableLazyManager) { db.child("partnerUser") }
    val refPartnerOrganisation by resettableLazy(resettableLazyManager) { db.child("partnerOrganisation") }


    fun userPublic(id: String): Observable<UserPublic> {
        return refUserPublic
                .child(id)
                .asObservable()
                .map { Nullable(it.toModel<UserPublic>()) }
                .filterNull()
    }

    val userObservable: Observable<User> by resettableLazy(resettableLazyManager) {
        FirebaseAuthExtensions.getLoggedInUserId()
                .flatMap { userId ->
                    val userPublic = refUserPublic
                            .child(userId)
                            .asObservable()

                    val countryAdminUser = refCountryAdmin
                            .child(userId)
                            .asObservable()
                            .filter { it.value != null }
                            .combineWithPair(userPublic)
                            .mapNormalUser(COUNTRY_ADMIN)

                    val countryDirectorUser = refCountryDirector
                            .child(userId)
                            .asObservable()
                            .filter { it.value != null }
                            .combineWithPair(userPublic)
                            .mapNormalUser(COUNTRY_DIRECTOR)

                    val ertUser = refErt
                            .child(userId)
                            .asObservable()
                            .filter { it.value != null }
                            .combineWithPair(userPublic)
                            .mapNormalUser(ERT)

                    val ertLeaderUser = refErtLeader
                            .child(userId)
                            .asObservable()
                            .filter { it.value != null }
                            .combineWithPair(userPublic)
                            .mapNormalUser(ERT_LEADER)

                    val partnerUserOrgId = refPartner
                            .child(userId)
                            .asObservable()
                            .filter { it.value != null }
                            .map { snapshot ->
                                snapshot.child("partnerOrganisationId").value as String
                            }
                            .distinctUntilChanged()
                            .share()

                    val partnerOrganisation = partnerUserOrgId
                            .flatMap { partnerOrgId ->
                                refPartnerOrganisation
                                        .child(partnerOrgId)
                                        .asObservable()
                            }

                    val partnerUser = refPartnerUser
                            .child(userId)
                            .asObservable()
                    Observable.merge(
                            listOf(
                                    countryAdminUser,
                                    countryDirectorUser,
                                    ertUser,
                                    ertLeaderUser,
                                    partnerOrganisation.combineWithTriple(userPublic, partnerUser)
                                            .map { (organisationSnap, userPublic, userSnap) ->

                                                Nullable(Pair(userSnap, userPublic).toMergedModel<User> { model, jsonObject ->
                                                    model.userType = PARTNER
                                                    model.agencyAdminId = jsonObject["agencyAdmin"].asJsonObject.firstChildKey()
                                                    model.systemAdminId = jsonObject["systemAdmin"].asJsonObject.firstChildKey()

                                                    model.countryId = organisationSnap.child(
                                                            "countryId").value as String
                                                    model.agencyAdminId = organisationSnap.child(
                                                            "agencyId").value as String
                                                    model.systemAdminId = userSnap.child(
                                                            "systemAdmin").firstChildKey()

                                                })
                                            }
                                            .filterNull()
                            )

                    )
                }
                .behavior()

    }

    private fun Observable<Pair<DataSnapshot, DataSnapshot>>.mapNormalUser(userType: UserType): Observable<User> {
        return map { (userSnapshot, publicUserSnapshot) ->

            Nullable(Pair(userSnapshot, publicUserSnapshot).toMergedModel<User> { model, jsonObject ->
                model.userType = userType
                model.agencyAdminId = jsonObject["agencyAdmin"].asJsonObject.firstChildKey()
                model.systemAdminId = jsonObject["systemAdmin"].asJsonObject.firstChildKey()
            })
        }
                .filterNull()
    }

    val countryOfficeObservable: Observable<CountryOffice> by resettableLazy(resettableLazyManager) {
        userObservable
                .flatMap { user ->
                    countryOfficeObservable(user)
                }
                .share()
                .behavior()
    }

    fun countryOfficeObservable(user: User): Observable<CountryOffice> {
        return db.child("countryOffice")
                .child(user.agencyAdminId)
                .child(user.countryId)
                .asObservable()
                .map {
                    Nullable(it.toModel<CountryOffice> { model, jsonObject ->
                        val clockSettings = jsonObject["clockSettings"].asJsonObject
                        val preparednessClockSettings = clockSettings["preparedness"].asJsonObject
                        val responsePlanClockSettings = clockSettings["responsePlans"].asJsonObject
                        val riskMonitoringClockSettings = clockSettings["riskMonitoring"].asJsonObject

                        model.mPreparednessClockSetting = ClockSetting(
                                COUNTRY,
                                DurationTypeSerializer.deserialize(preparednessClockSettings["durationType"].asInt)
                                        ?: WEEK,
                                preparednessClockSettings["value"].asInt
                        )
                        model.mResponsePlanClockSetting = ClockSetting(
                                COUNTRY,
                                DurationTypeSerializer.deserialize(responsePlanClockSettings["durationType"].asInt)
                                        ?: WEEK,
                                responsePlanClockSettings["value"].asInt
                        )
                    })

                }
                .filterNull()
    }

    val hazardsObservable: Observable<List<Hazard>> by resettableLazy(resettableLazyManager) {
        userObservable
                .print("user")
                .flatMap { user ->
                    db.child("hazard")
                            .child(user.countryId)
                            .asObservable()
                            .map { snapshot ->
                                snapshot.children.map { it.toModel<Hazard>() }.filterNotNull()
                            }
                }
                .print("user, hazards")
                .behavior()
    }

    val redAlertHazardScenariosObservable: Observable<List<HazardScenario>> by resettableLazy(resettableLazyManager) {
        alertsObservable
                .filterList {
                    it.level == RED && it.state == APPROVED
                }
                .mapList { it.hazardScenario }
                .map { it.distinct() }
    }

    val indicatorsObservable: Observable<List<Indicator>> by resettableLazy(resettableLazyManager) {
        hazardsObservable
                .print("hazards")
                .filterList {
                    it.isActive
                }
                .print("hazards, filtered")
                .combineWithPair(userObservable)
                .print("hazards, with user")
                .map { (hazards, user) -> hazards.map { it.id } + user.countryId }
                .print("hazards full list")
                .flatMap { hazardIdList ->
                    println("Inside flatmap")
                    combineFlatten(
                            hazardIdList.map { hazardId ->
                                db.child("indicator")
                                        .child(hazardId)
                                        .asObservable()
                                        .print("GOT HAZARDS")
                                        .map { it.children.toList() }
                            }
                    )
                            .print("flattned")
                            .map { list ->
                                list.map { dataSnapshot ->
                                    dataSnapshot.toModel<Indicator> { model, json ->
                                        model.parentId = dataSnapshot.ref.parent!!.key!!
                                    }
                                }
                                        .filterNotNull()
                            }
                            .print("POST MAP")
                }
                .print("indicators")
                .withLatestFromPair(userObservable)
                .print("indicators with user again..")
                .map { (list, user) ->
                    list.filter { it.assignee == user.id }
                }
                .print("filtered indicators")
                .behavior()
    }

    fun actionObservable(id: String, type: ActionType): Observable<out Action> {
        return userObservable
                .switchMap { user ->
                    val observableList = mutableListOf<Observable<DataSnapshot>>()

                    observableList += db.child("action")
                            .child(user.countryId)
                            .child(id)
                            .asObservable()

                    if (type == MANDATED) {
                        observableList += db.child("actionMandated")
                                .child(user.agencyAdminId)
                                .child(id)
                                .asObservable()
                    } else if (type == CHS) {
                        observableList += db.child("actionCHS")
                                .child(user.systemAdminId)
                                .child(id)
                                .asObservable()
                    }

                    mergeCombine(observableList)
                            .combineWithPair(countryOfficeObservable(user))
                            .map { (actionList, countryOffice) ->
                                Nullable(actionList.toMergedModel<Action> { action, jsonObject ->
                                    setUpAction(action, jsonObject, countryOffice)
                                })
                            }
                            .filterNull()

                }
    }

    val actionsObservable: Observable<List<Action>> by resettableLazy(resettableLazyManager) {
        userObservable
                .switchMap { user ->
                    val baseAction = db.child("action")
                            .child(user.countryId)
                            .asObservable()
                            .map { it.children.toList() }

                    val chsAction = db.child("actionCHS")
                            .child(user.systemAdminId)
                            .asObservable()
                            .map { it.children.toList() }

                    val mandatedAction = db.child("actionMandated")
                            .child(user.agencyAdminId)
                            .asObservable()
                            .map { it.children.toList() }


                    baseAction.combineWithTriple(chsAction, mandatedAction)
                            .combineWithPair(countryOfficeObservable(user))
                            .map { (actionTriple, countryOffice) ->
                                val (actions, chsActions, mandatedActions) = actionTriple

                                (actions + chsActions + mandatedActions)
                                        .groupBy { it.key }
                                        .values
                                        .toList()
                                        .map {
                                            it.toMergedModel<Action> { action, jsonObject ->
                                                setUpAction(action, jsonObject, countryOffice)
                                            }
                                        }
                                        .filterNotNull()
                            }
                }
                .behavior()
    }

    private fun setUpAction(action: Action, json: JsonObject, countryOffice: CountryOffice) {
        var documentIds = emptyList<String>()

        if (json.has("documents")) {
            documentIds = json["documents"].asJsonObject.childKeys()
        }

        action.documentIds = documentIds

        if (json.has("frequencyValue") && json.has("frequencyBase")) {
            action.mClockSetting = ClockSetting(
                    ACTION,
                    DurationTypeSerializer.deserialize(json["frequencyBase"].asInt) ?: WEEK,
                    json["frequencyValue"].asInt
            )
        } else {
            action.mClockSetting = countryOffice.mPreparednessClockSetting
        }
    }

    val responsePlansObservable: Observable<List<ResponsePlan>> by resettableLazy(resettableLazyManager) {
        userObservable
                .filter { user ->
                    user.userType == COUNTRY_DIRECTOR
                }
                .flatMap { user ->
                    db.child("responsePlan")
                            .child(user.countryId)
                            .asObservable()
                            .map { it.children.toList() }
                            .map {list ->
                                list.mapNotNull { it.toModel<ResponsePlan>() }
                            }
                            .filterList {
                                it.approval.countryDirector?.id == user.countryId
                            }
                }
                .startWith(listOf<ResponsePlan>())
                .behavior()
    }

    val alertsObservable by resettableLazy(resettableLazyManager) {
        userObservable.switchMap { user ->
            db.child("alert")
                    .child(user.countryId)
                    .asObservable()
                    .map {
                        it.children.toList()
                    }
                    .mapListNotNull {
                        it.toModel(alertStateHandler)
                    }
        }
                .share()
                .behavior()
    }

    val alertStateHandler: (Alert, JsonObject) -> Unit = { alert, jsonObject ->
        val approvalStateInt = jsonObject["approval"]?.get("countryDirector")?.firstChild()?.asInt
        alert.state = AlertApprovalStateSerializer.deserialize(approvalStateInt)
    }

    fun notes(id: String): Observable<List<Note>> {
        return userObservable
                .flatMap { user ->
                    db.child("note")
                            .child(user.countryId)
                            .child(id)
                            .asObservable()
                            .map {
                                it.children.toList()
                            }
                            .mapListNotNull { it.toModel<Note>() }
                }
    }

    enum class PrivacySettingType(val value: Int) {
        MPA(0),
        APA(1),
        CHS(2),
        RISK_MONITORING(3),
        CONFLICT_INDICATORS(4),
        OFFICE_PROFILE(5),
        RESPONSE_PLAN(6),
    }

    fun privacySettings(id: String, type: PrivacySettingType): Observable<PrivacySetting> {
        return db.child("module").child(id).child(type.value.toString())
                .asObservable()
                .map { it.toModel<PrivacySetting>() }
    }

    fun agency(id: String): Observable<Agency> {
        return db.child("agency")
                .child(id).asObservable()
                .map { it.toModel<Agency>() }
    }

    fun alert(id: String): Observable<Alert> {
        return userObservable.flatMap { user ->
            db.child("alert")
                    .child(user.countryId)
                    .child(id)
                    .asObservable()
                    .map {
                        it.toModel<Alert>(Repository.alertStateHandler)
                    }
        }
    }


    fun searchProgrammes(searchCountry: Country, searchLevel1: Int?,
            searchLevel2: Int?): Observable<Map<Agency, List<Programme>>> {
        //Fetch root countryOfficeProfile/programme node
        return db.child("countryOfficeProfile")
                .child("programme")
                .asObservable()
                //Map to Map<CountryId, List<Programmes>>
                .map { baseSnapshot ->
                    baseSnapshot.children.map { countrySnapshot ->
                        countrySnapshot.key!! to countrySnapshot
                                .child("4WMapping")
                                .children
                                .map { programmeSnapshot ->
                                    programmeSnapshot.toModel<Programme>()
                                }
                                .filterNotNull()
                    }.toMap()
                }
                //Filter based on searchArea
                .map {
                    it.mapValues { (_, programmes) ->
                        programmes.filter { programme ->
                            programme.where == searchCountry &&
                                    (searchLevel1 == null || programme.level1 == searchLevel1) &&
                                    (searchLevel2 == null || programme.level2 == searchLevel2)
                        }
                    }
                }
                //Fetch privacy settings for each country -> Map<CountryPrivacySetting, List<Programme>>
                .flatMap { map ->
                    map.toList()
                            .map { (countryId, programmes) ->
                                privacySettings(countryId, OFFICE_PROFILE)
                                        .map { Pair(it, programmes) }
                            }
                            .combineLatest {
                                it.toMap()
                            }
                }
                //Filter based on country privacy settings
                .map {
                    it.filter { (privacySettings, _) ->
                        privacySettings.privacy == Privacy.PUBLIC && privacySettings.status
                    }
                }
                //Remove privacy settings, flatten list, group by agencyId
                .map { privacySettingsToProgrammes ->
                    privacySettingsToProgrammes
                            .values
                            .flatten()
                            .groupBy {
                                it.agencyId
                            }
                }
                //Grab agency, map to -> Map<Agency, List<Programme>>
                .flatMap { agencyIdsToProgrammes ->
                    val agencyProgramList = agencyIdsToProgrammes.toList()
                            .map { (agencyId, programmes) ->
                                agency(agencyId)
                                        .map { Pair(it, programmes) }
                            }
                    if (agencyProgramList.isEmpty()) {
                        Observable.just(emptyMap())
                    } else {
                        agencyProgramList.combineLatest { it.toMap() }
                    }
                }
    }
}
