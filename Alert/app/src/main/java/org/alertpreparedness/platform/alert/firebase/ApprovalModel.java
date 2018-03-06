package org.alertpreparedness.platform.alert.firebase;

import java.io.Serializable;
import java.util.HashMap;

public class ApprovalModel extends FirebaseModel implements Serializable {

    private HashMap<String, Integer> countryDirector;

    public ApprovalModel() {
    }

    @Override
    public String toString() {
        return "ApprovalModel{" +
                "countryDirector=" + countryDirector +
                '}' +
                super.toString();
    }

    public HashMap<String, Integer> getCountryDirector() {
        return countryDirector;
    }

    public void setCountryDirector(HashMap<String, Integer> countryDirector) {
        this.countryDirector = countryDirector;
    }
}
