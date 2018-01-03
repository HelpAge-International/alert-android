package org.alertpreparedness.platform.alert.firebase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Tj on 20/12/2017.
 */

public class ApprovalModel implements Serializable {

    private HashMap<String, Integer> countryDirector;

    public ApprovalModel() {

    }

    @Override
    public String toString() {
        return "ApprovalModel{" +
                "countryDirector=" + countryDirector +
                '}';
    }

    public HashMap<String, Integer> getCountryDirector() {
        return countryDirector;
    }

    public void setCountryDirector(HashMap<String, Integer> countryDirector) {
        this.countryDirector = countryDirector;
    }
}
