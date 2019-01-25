package org.alertpreparedness.platform.v2.repository

import com.google.firebase.database.DataSnapshot
import io.reactivex.Observable
import org.alertpreparedness.platform.v2.FirebaseAuthExtensions
import org.alertpreparedness.platform.v2.asObservable
import org.alertpreparedness.platform.v2.db
import org.alertpreparedness.platform.v2.models.User
import org.alertpreparedness.platform.v2.models.UserType
import org.alertpreparedness.platform.v2.models.UserType.COUNTRY_ADMIN
import org.alertpreparedness.platform.v2.models.UserType.COUNTRY_DIRECTOR
import org.alertpreparedness.platform.v2.models.UserType.ERT
import org.alertpreparedness.platform.v2.models.UserType.ERT_LEADER
import org.alertpreparedness.platform.v2.models.UserType.PARTNER
import org.alertpreparedness.platform.v2.utils.extensions.combineWithPair
import org.alertpreparedness.platform.v2.utils.extensions.firstChildKey

val refCountryAdmin by lazy { db.child("administratorCountry") }
val refCountryDirector by lazy { db.child("countryDirector") }
val refErt by lazy { db.child("ert") }
val refErtLeader by lazy { db.child("ertLeader") }
val refPartner by lazy { db.child("partner") }
val refPartnerUser by lazy { db.child("partnerUser") }
val refPartnerOrganisation by lazy { db.child("partnerOrganisation") }

object UserRepository {

    val userObservable by lazy {
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
                            .cache()

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

