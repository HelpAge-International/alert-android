package org.alertpreparedness.platform.v2.repository

import com.google.firebase.database.DataSnapshot
import io.reactivex.Observable
import org.alertpreparedness.platform.v2.FirebaseAuthExtensions
import org.alertpreparedness.platform.v2.asObservable
import org.alertpreparedness.platform.v2.db
import org.alertpreparedness.platform.v2.models.Action
import org.alertpreparedness.platform.v2.models.Indicator
import org.alertpreparedness.platform.v2.models.ResponsePlan
import org.alertpreparedness.platform.v2.models.User
import org.alertpreparedness.platform.v2.models.UserType
import org.alertpreparedness.platform.v2.models.UserType.COUNTRY_ADMIN
import org.alertpreparedness.platform.v2.models.UserType.COUNTRY_DIRECTOR
import org.alertpreparedness.platform.v2.models.UserType.ERT
import org.alertpreparedness.platform.v2.models.UserType.ERT_LEADER
import org.alertpreparedness.platform.v2.models.UserType.PARTNER
import org.alertpreparedness.platform.v2.models.enums.ActionType.CHS
import org.alertpreparedness.platform.v2.models.enums.ActionType.CUSTOM
import org.alertpreparedness.platform.v2.models.enums.ActionType.MANDATED
import org.alertpreparedness.platform.v2.models.enums.ActionTypeSerializer
import org.alertpreparedness.platform.v2.utils.extensions.childKeys
import org.alertpreparedness.platform.v2.utils.extensions.combineFlatten
import org.alertpreparedness.platform.v2.utils.extensions.combineToList
import org.alertpreparedness.platform.v2.utils.extensions.combineWithPair
import org.alertpreparedness.platform.v2.utils.extensions.firstChildKey
import org.alertpreparedness.platform.v2.utils.extensions.toMergedModel
import org.alertpreparedness.platform.v2.utils.extensions.toModel
import org.alertpreparedness.platform.v2.utils.extensions.withLatestFromPair

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
                    val countryAdminUser = refCountryAdmin
                            .child(userId)
                            .asObservable()
                            .filter { it.value != null }
                            .mapNormalUser(COUNTRY_ADMIN)

                    val countryDirectorUser = refCountryDirector
                            .child(userId)
                            .asObservable()
                            .filter { it.value != null }
                            .mapNormalUser(COUNTRY_DIRECTOR)

                    val ertUser = refErt
                            .child(userId)
                            .asObservable()
                            .filter { it.value != null }
                            .mapNormalUser(ERT)

                    val ertLeaderUser = refErtLeader
                            .child(userId)
                            .asObservable()
                            .filter { it.value != null }
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
                                    partnerOrganisation.combineWithPair(partnerUser)
                                            .map { (organisationSnap, userSnap) ->
                                                val countryId = organisationSnap.child(
                                                        "countryId").value as String
                                                val agencyId = organisationSnap.child("agencyId").value as String
                                                val systemAdminId = userSnap.child("systemAdmin").firstChildKey()

                                                User(PARTNER, countryId, agencyId, systemAdminId, userSnap.key!!)
                                            }
                            )

                    )
                }
                .share()

    }

    val hazardIdsObservable: Observable<List<String>> by lazy {
        userObservable
                .flatMap { user ->
                    db.child("hazard")
                            .child(user.countryId)
                            .asObservable()
                            .map {
                                it.childKeys()
                            }
                }
                .share()
    }

    val indicatorsObservable: Observable<List<Indicator>> by lazy {
        hazardIdsObservable
                .combineWithPair(userObservable)
                .map { (hazardIds, user) -> hazardIds + user.countryId }
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
    }

    val actionsObservable: Observable<List<Action>> by lazy {
        userObservable
                .flatMap { user ->
                    db.child("action")
                            .child(user.countryId)
                            .asObservable()
                            .map { it.children.toList() }
                            .switchMap { snapshots ->
                                combineToList(
                                        snapshots.map { snapshot ->
                                            val typeInt = snapshot.child("type").getValue(Long::class.java)!!.toInt()
                                            val type = ActionTypeSerializer.jsonToEnum(typeInt) ?: CUSTOM

                                            when (type) {
                                                CHS ->
                                                    db.child("actionCHS")
                                                            .child(user.systemAdminId)
                                                            .child(snapshot.key!!)
                                                            .asObservable()
                                                            .map { Pair(snapshot, it).toMergedModel<Action>() }
                                                MANDATED -> {
                                                    db.child("actionMandated")
                                                            .child(user.agencyAdminId)
                                                            .child(snapshot.key!!)
                                                            .asObservable()
                                                            .map { Pair(snapshot, it).toMergedModel<Action>() }
                                                }
                                                CUSTOM -> Observable.just(snapshot.toModel())
                                            }
                                        }
                                )
                            }
                }
                .share()
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
                            .map { list ->
                                list.map {
                                    it.toModel<ResponsePlan>()
                                }
                                        .filter {
                                            it.approval.countryDirector?.id == user.countryId
                                        }
                            }
                }
                .share()
                .startWith(listOf<ResponsePlan>())
    }
}

private fun Observable<DataSnapshot>.mapNormalUser(userType: UserType): Observable<User> {
    return map { snapshot ->
        val countryId = snapshot.child("countryId").value as String
        val agencyAdminId = snapshot.child("agencyAdmin").firstChildKey()
        val systemAdminId = snapshot.child("systemAdmin").firstChildKey()

        User(userType, countryId, agencyAdminId, systemAdminId, snapshot.ref.key!!)
    }
}

