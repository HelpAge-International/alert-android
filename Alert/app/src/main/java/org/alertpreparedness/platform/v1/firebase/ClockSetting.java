package org.alertpreparedness.platform.v1.firebase;

public class ClockSetting extends FirebaseModel {

    private int durationType;
    private int value;

    @Override
    public String toString() {
        return "ClockSetting{" +
                "durationType=" + durationType +
                ", value=" + value +
                '}' +
                super.toString();
    }

    public int getDurationType() {
        return durationType;
    }

    public void setDurationType(int durationType) {
        this.durationType = durationType;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
