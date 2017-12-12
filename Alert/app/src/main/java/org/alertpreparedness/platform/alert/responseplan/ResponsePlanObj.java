package org.alertpreparedness.platform.alert.responseplan;

import java.util.Date;

/**
 * Created by Tj on 12/12/2017.
 */

public class ResponsePlanObj {

    public final String hazardType;
    public String completePercentage;
    public final String description;
    public final int status;
    public final Date lastUpdated;

    public ResponsePlanObj(
            String hazardType,
            String completePercentage,
            String description,
            int status,
            Date lastUpdated
    ) {
        this.hazardType = hazardType;
        this.completePercentage = completePercentage;
        this.description = description;
        this.status = status;
        this.lastUpdated = lastUpdated;
    }

}
