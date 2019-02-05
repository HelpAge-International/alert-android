package org.alertpreparedness.platform.v2.preparedness.advanced

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import org.alertpreparedness.platform.v2.base.BaseViewModel
import org.alertpreparedness.platform.v2.preparedness.advanced.IAdvancedPreparednessViewModel.Inputs
import org.alertpreparedness.platform.v2.preparedness.advanced.IAdvancedPreparednessViewModel.Outputs

interface IAdvancedPreparednessViewModel {
    interface Inputs {
        fun addButtonClicked()
    }

    interface Outputs {
        fun addAction(): Observable<Unit>
    }
}

class AdvancedPreparednessViewModel : BaseViewModel(), Inputs, Outputs {

    private val onAddClicked = BehaviorSubject.create<Unit>()

    override fun addButtonClicked() {
        onAddClicked.onNext(Unit)
    }

    override fun addAction(): Observable<Unit> {
        return onAddClicked
    }
}
