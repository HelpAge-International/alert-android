package org.alertpreparedness.platform.v1.firebase;

public class CountryOfficeModel extends FirebaseModel {

    private ClockSettings clockSettings;

    @Override
    public String toString() {
        return "CountryOfficeModel{" +
                "clockSettings=" + clockSettings +
                '}' +
                super.toString();
    }

    public ClockSettings getClockSettings() {
        return clockSettings;
    }

    public void setClockSettings(ClockSettings clockSettings) {
        this.clockSettings = clockSettings;
    }
}
