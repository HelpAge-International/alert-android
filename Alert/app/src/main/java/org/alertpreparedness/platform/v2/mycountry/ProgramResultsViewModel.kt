package org.alertpreparedness.platform.v2.mycountry

import io.reactivex.Observable
import io.reactivex.rxkotlin.combineLatest
import io.reactivex.subjects.BehaviorSubject
import org.alertpreparedness.platform.v2.asObservable
import org.alertpreparedness.platform.v2.base.BaseViewModel
import org.alertpreparedness.platform.v2.db
import org.alertpreparedness.platform.v2.models.Agency
import org.alertpreparedness.platform.v2.models.Programme
import org.alertpreparedness.platform.v2.models.enums.Country
import org.alertpreparedness.platform.v2.models.enums.Privacy.PUBLIC
import org.alertpreparedness.platform.v2.mycountry.IProgramResultsViewModel.Inputs
import org.alertpreparedness.platform.v2.mycountry.IProgramResultsViewModel.Outputs
import org.alertpreparedness.platform.v2.repository.Repository.PrivacySettingType.OFFICE_PROFILE
import org.alertpreparedness.platform.v2.repository.Repository.agency
import org.alertpreparedness.platform.v2.repository.Repository.privacySettings
import org.alertpreparedness.platform.v2.utils.extensions.toModel

interface IProgramResultsViewModel {
    interface Inputs {
        fun searchArea(country: Country, level1: Int?, level2: Int?)
    }

    interface Outputs {
        fun results(): Observable<Map<Agency, List<Programme>>>
    }
}

class ProgramResultsViewModel : BaseViewModel(), Inputs, Outputs {
    override fun results(): Observable<Map<Agency, List<Programme>>> {
        return searchArea
                .switchMap { (searchCountry, searchLevel1, searchLevel2) ->
                    //Fetch root countryOfficeProfile/programme node
                    db.child("countryOfficeProfile")
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
                                    privacySettings.privacy == PUBLIC && privacySettings.status
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
                                agencyIdsToProgrammes.toList()
                                        .map { (agencyId, programmes) ->
                                            agency(agencyId)
                                                    .map { Pair(it, programmes) }
                                        }
                                        .combineLatest {
                                            it.toMap()
                                        }
                            }

                }
    }

    private val searchArea = BehaviorSubject.create<Triple<Country, Int?, Int?>>()

    override fun searchArea(country: Country, level1: Int?, level2: Int?) {
        searchArea.onNext(Triple(country, level1, level2))
    }
}
