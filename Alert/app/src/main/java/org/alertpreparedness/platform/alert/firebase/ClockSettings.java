package org.alertpreparedness.platform.alert.firebase;

public class ClockSettings {
    private ClockSetting preparedness;
    private ClockSetting responsePlans;

    public ClockSetting getPreparedness() {
        return preparedness;
    }

    public void setPreparedness(ClockSetting preparedness) {
        this.preparedness = preparedness;
    }

    public ClockSetting getResponsePlans() {
        return responsePlans;
    }

    public void setResponsePlans(ClockSetting responsePlans) {
        this.responsePlans = responsePlans;
    }
}
