package org.alertpreparedness.platform.v2.preparedness

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.alertpreparedness.platform.v2.base.BaseViewModel
import org.alertpreparedness.platform.v2.models.Action
import org.alertpreparedness.platform.v2.models.enums.ActionType
import org.alertpreparedness.platform.v2.models.enums.Note
import org.alertpreparedness.platform.v2.preparedness.IPreparednessBottomSheetViewModel.Inputs
import org.alertpreparedness.platform.v2.preparedness.IPreparednessBottomSheetViewModel.Outputs
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption
import org.alertpreparedness.platform.v2.repository.Repository.actionObservable
import org.alertpreparedness.platform.v2.repository.Repository.notes
import org.alertpreparedness.platform.v2.utils.extensions.swap
import org.alertpreparedness.platform.v2.utils.extensions.withLatestFromPair

interface IPreparednessBottomSheetViewModel {
    interface Inputs {
        fun action(id: String, type: ActionType)
        fun onActionOptionClicked(option: PreparednessBottomSheetOption)
    }

    interface Outputs {
        fun attachmentCount(): Observable<Int>
        fun notesCount(): Observable<Int>
        fun actionOptionClicked(): Observable<Pair<Action, PreparednessBottomSheetOption>>
    }
}

class PreparednessBottomSheetViewModel : BaseViewModel(), Inputs, Outputs {
    val actionInput = BehaviorSubject.create<Pair<String, ActionType>>()

    val actionOptionClicked = PublishSubject.create<PreparednessBottomSheetOption>()
    val action: Observable<Action>

    val actionNotes: Observable<List<Note>>

    init {

        action = actionInput.switchMap { (id, type) ->
            actionObservable(id, type)
        }
                .share()

        actionNotes = actionInput.switchMap { (id, _) ->
            notes(id)
        }
    }

    override fun action(id: String, type: ActionType) {
        actionInput.onNext(Pair(id, type))
    }

    override fun onActionOptionClicked(option: PreparednessBottomSheetOption) {
        actionOptionClicked.onNext(option)
    }

    override fun notesCount(): Observable<Int> {
        return actionNotes.map { it.size }
    }

    override fun attachmentCount(): Observable<Int> {
        return action.map { it.documentIds.size }
    }

    override fun actionOptionClicked(): Observable<Pair<Action, PreparednessBottomSheetOption>> {
        return actionOptionClicked.withLatestFromPair(action).swap()
    }
}
