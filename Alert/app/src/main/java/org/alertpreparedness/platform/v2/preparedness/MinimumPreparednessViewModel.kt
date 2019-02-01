package org.alertpreparedness.platform.v2.preparedness

import org.alertpreparedness.platform.v2.base.BaseViewModel
import org.alertpreparedness.platform.v2.preparedness.IMinimumPreparednessViewModel.Inputs
import org.alertpreparedness.platform.v2.preparedness.IMinimumPreparednessViewModel.Outputs

interface IMinimumPreparednessViewModel {
    interface Inputs

    interface Outputs
}

class MinimumPreparednessViewModel : BaseViewModel(), Inputs, Outputs
