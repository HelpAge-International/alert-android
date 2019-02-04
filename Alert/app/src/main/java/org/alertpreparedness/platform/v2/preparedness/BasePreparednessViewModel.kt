package org.alertpreparedness.platform.v2.preparedness

import io.reactivex.Observable
import org.alertpreparedness.platform.v2.base.BaseViewModel
import org.alertpreparedness.platform.v2.models.Action
import org.alertpreparedness.platform.v2.models.User
import org.alertpreparedness.platform.v2.preparedness.IBasePreparednessViewModel.Inputs
import org.alertpreparedness.platform.v2.preparedness.IBasePreparednessViewModel.Outputs
import org.alertpreparedness.platform.v2.repository.Repository.actionsObservable
import org.alertpreparedness.platform.v2.repository.Repository.userObservable
import org.alertpreparedness.platform.v2.utils.extensions.behavior
import org.alertpreparedness.platform.v2.utils.extensions.combineWithPair

interface IBasePreparednessViewModel {
    interface Inputs

    interface Outputs {
        fun actions(): Observable<List<Action>>
        fun user(): Observable<User>
    }
}

abstract class BasePreparednessViewModel : BaseViewModel(), Inputs, Outputs {

    private val actions: Observable<List<Action>>

    abstract fun filterAction(action: Action, user: User): Boolean

    init {
        actions = actionsObservable
                .combineWithPair(userObservable)
                .map { (list, user) -> list.filter { filterAction(it, user) } }
                .behavior()
    }

    override fun actions(): Observable<List<Action>> {
        return actions
    }

    override fun user(): Observable<User> {
        return userObservable
    }
}