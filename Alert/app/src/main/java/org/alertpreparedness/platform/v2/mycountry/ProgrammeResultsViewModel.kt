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
import org.alertpreparedness.platform.v2.printRef
import org.alertpreparedness.platform.v2.repository.Repository.PrivacySettingType.OFFICE_PROFILE
import org.alertpreparedness.platform.v2.repository.Repository.agency
import org.alertpreparedness.platform.v2.repository.Repository.privacySettings
import org.alertpreparedness.platform.v2.repository.Repository.searchProgrammes
import org.alertpreparedness.platform.v2.utils.extensions.print
import org.alertpreparedness.platform.v2.utils.extensions.toModel

interface IProgramResultsViewModel {
    interface Inputs {
        fun searchArea(country: Country, level1: Int?, level2: Int?)
    }

    interface Outputs {
        fun results(): Observable<Map<Agency, List<Programme>>>
    }
}

class ProgrammeResultsViewModel : BaseViewModel(), Inputs, Outputs {
    private val searchArea = BehaviorSubject.create<Triple<Country, Int?, Int?>>()

    override fun results(): Observable<Map<Agency, List<Programme>>> {
        return searchArea
                .print("Search Area")
                .switchMap { (searchCountry, searchLevel1, searchLevel2) ->
                    //Fetch root countryOfficeProfile/programme node
                    searchProgrammes(searchCountry, searchLevel1, searchLevel2)

                }
    }

    override fun searchArea(country: Country, level1: Int?, level2: Int?) {
        searchArea.onNext(Triple(country, level1, level2))
    }
}
