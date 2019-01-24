package org.alertpreparedness.platform.v2.dashboard.home

import io.reactivex.Observable
import org.alertpreparedness.platform.v1.dashboard.model.Task
import org.alertpreparedness.platform.v2.FirebaseExtensions
import org.alertpreparedness.platform.v2.base.BaseViewModel
import org.alertpreparedness.platform.v2.toObservable

interface IHomeViewModel {
    interface Inputs {
    }

    interface Outputs {
        fun tasks(): Observable<List<Task>>
    }
}

class HomeViewModel: BaseViewModel(), IHomeViewModel.Inputs, IHomeViewModel.Outputs {
    override fun tasks(): Observable<List<Task>> {
    }
}