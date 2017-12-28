package org.alertpreparedness.platform.alert.firebase;

import java.io.Serializable;

/**
 * Created by Tj on 20/12/2017.
 */

public class ApprovalModel implements Serializable {

    private Object countryDirector;

    public ApprovalModel() {

    }

    public Object getCountryDirector() {
        return countryDirector;
    }

    public void setCountryDirector(Object countryDirector) {
        this.countryDirector = countryDirector;
    }
}
