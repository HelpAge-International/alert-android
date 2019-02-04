package org.alertpreparedness.platform.v2.repository

import com.google.firebase.database.DataSnapshot
import com.google.gson.JsonObject
import io.reactivex.Observable
import org.alertpreparedness.platform.v2.FirebaseAuthExtensions
import org.alertpreparedness.platform.v2.asObservable
import org.alertpreparedness.platform.v2.db
import org.alertpreparedness.platform.v2.models.Action
import org.alertpreparedness.platform.v2.models.ClockSettings
import org.alertpreparedness.platform.v2.models.Hazard
import org.alertpreparedness.platform.v2.models.Indicator
import org.alertpreparedness.platform.v2.models.ResponsePlan
import org.alertpreparedness.platform.v2.models.User
import org.alertpreparedness.platform.v2.models.UserType
import org.alertpreparedness.platform.v2.models.UserType.COUNTRY_ADMIN
import org.alertpreparedness.platform.v2.models.UserType.COUNTRY_DIRECTOR
import org.alertpreparedness.platform.v2.models.UserType.ERT
import org.alertpreparedness.platform.v2.models.UserType.ERT_LEADER
import org.alertpreparedness.platform.v2.models.UserType.PARTNER
import org.alertpreparedness.platform.v2.models.enums.CountryOffice
import org.alertpreparedness.platform.v2.models.enums.DurationType.WEEK
import org.alertpreparedness.platform.v2.models.enums.DurationTypeSerializer
import org.alertpreparedness.platform.v2.utils.extensions.behavior
import org.alertpreparedness.platform.v2.utils.extensions.combineFlatten
import org.alertpreparedness.platform.v2.utils.extensions.combineWithPair
import org.alertpreparedness.platform.v2.utils.extensions.combineWithTriple
import org.alertpreparedness.platform.v2.utils.extensions.filterList
import org.alertpreparedness.platform.v2.utils.extensions.firstChildKey
import org.alertpreparedness.platform.v2.utils.extensions.mapList
import org.alertpreparedness.platform.v2.utils.extensions.toMergedModel
import org.alertpreparedness.platform.v2.utils.extensions.toModel
import org.alertpreparedness.platform.v2.utils.extensions.withLatestFromPair

val refUserPublic by lazy { db.child("userPublic") }
val refCountryAdmin by lazy { db.child("administratorCountry") }
val refCountryDirector by lazy { db.child("countryDirector") }
val refErt by lazy { db.child("ert") }
val refErtLeader by lazy { db.child("ertLeader") }
val refPartner by lazy { db.child("partner") }
val refPartnerUser by lazy { db.child("partnerUser") }
val refPartnerOrganisation by lazy { db.child("partnerOrganisation") }

object Repository {

    val userObservable: Observable<User> by lazy {
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

                                                Pair(userSnap, userPublic).toMergedModel<User> { model, jsonObject ->
                                                    model.userType = PARTNER
                                                    model.agencyAdminId = jsonObject["agencyAdmin"].asJsonObject.firstChildKey()
                                                    model.systemAdminId = jsonObject["systemAdmin"].asJsonObject.firstChildKey()

                                                    model.countryId = organisationSnap.child(
                                                            "countryId").value as String
                                                    model.agencyAdminId = organisationSnap.child(
                                                            "agencyId").value as String
                                                    model.systemAdminId = userSnap.child(
                                                            "systemAdmin").firstChildKey()

                                                }
                                            }
                            )

                    )
                }
                .share()
                .behavior()

    }

    private fun Observable<Pair<DataSnapshot, DataSnapshot>>.mapNormalUser(userType: UserType): Observable<User> {
        return map { (userSnapshot, publicUserSnapshot) ->

            Pair(userSnapshot, publicUserSnapshot).toMergedModel<User> { model, jsonObject ->
                model.userType = userType
                model.agencyAdminId = jsonObject["agencyAdmin"].asJsonObject.firstChildKey()
                model.systemAdminId = jsonObject["systemAdmin"].asJsonObject.firstChildKey()
            }
        }
    }

    val countryOfficeObservable: Observable<CountryOffice> by lazy {
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
                    it.toModel<CountryOffice> { model, jsonObject ->
                        val clockSettings = jsonObject["clockSettings"].asJsonObject
                        val preparednessClockSettings = clockSettings["preparedness"].asJsonObject
                        val responsePlanClockSettings = clockSettings["responsePlans"].asJsonObject
                        val riskMonitoringClockSettings = clockSettings["riskMonitoring"].asJsonObject

                        model.preparednessClockSettings = ClockSettings(
                                DurationTypeSerializer.jsonToEnum(preparednessClockSettings["durationType"].asInt)
                                        ?: WEEK,
                                preparednessClockSettings["value"].asInt
                        )
                        model.responsePlanClockSettings = ClockSettings(
                                DurationTypeSerializer.jsonToEnum(responsePlanClockSettings["durationType"].asInt)
                                        ?: WEEK,
                                responsePlanClockSettings["value"].asInt
                        )
                    }
                }
    }

    val hazardObservable: Observable<List<Hazard>> by lazy {
        userObservable
                .flatMap { user ->
                    db.child("hazard")
                            .child(user.countryId)
                            .asObservable()
                            .map { snapshot ->
                                snapshot.children.map { it.toModel<Hazard>() }
                            }
                }
                .share()
                .behavior()
    }

    val indicatorsObservable: Observable<List<Indicator>> by lazy {
        hazardObservable
                .filterList {
                    it.isActive
                }
                .combineWithPair(userObservable)
                .map { (hazards, user) -> hazards.map { it.id } + user.countryId }
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
                .share()
                .behavior()
    }

    val actionsObservable: Observable<List<Action>> by lazy {
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
                                                setUpActionClockSettings(action, jsonObject, countryOffice)
                                            }
                                        }
                            }
                }
                .share()
                .behavior()
    }

    private fun setUpActionClockSettings(action: Action, json: JsonObject, countryOffice: CountryOffice) {
        if (json.has("frequencyValue") && json.has("frequencyBase")) {
            action.clockSettings = ClockSettings(
                    DurationTypeSerializer.jsonToEnum(json["frequencyBase"].asInt) ?: WEEK,
                    json["frequencyValue"].asInt
            )
        } else {
            action.clockSettings = countryOffice.preparednessClockSettings
        }
    }

    val responsePlansObservable: Observable<List<ResponsePlan>> by lazy {
        userObservable
                .filter { user ->
                    user.userType == COUNTRY_DIRECTOR
                }
                .flatMap { user ->
                    db.child("responsePlan")
                            .child(user.countryId)
                            .asObservable()
                            .map { it.children.toList() }
                            .mapList {
                                it.toModel<ResponsePlan>()
                            }
                            .filterList {
                                it.approval.countryDirector?.id == user.countryId
                            }
                }
                .share()
                .startWith(listOf<ResponsePlan>())
                .behavior()
    }
}

