package org.alertpreparedness.platform.v2.preparedness.minimum

import org.alertpreparedness.platform.v2.base.BaseViewModel
import org.alertpreparedness.platform.v2.preparedness.minimum.IMinimumPreparednessViewModel.Inputs
import org.alertpreparedness.platform.v2.preparedness.minimum.IMinimumPreparednessViewModel.Outputs

interface IMinimumPreparednessViewModel {
    interface Inputs

    interface Outputs
}

class MinimumPreparednessViewModel : BaseViewModel(), Inputs, Outputs
