package org.alertpreparedness.platform.alert.firebase;

public class ClockSettings extends FirebaseModel {

    private ClockSetting preparedness;
    private ClockSetting responsePlans;

    @Override
    public String toString() {
        return "ClockSettings{" +
                "preparedness=" + preparedness +
                ", responsePlans=" + responsePlans +
                '}' +
                super.toString();
    }

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
