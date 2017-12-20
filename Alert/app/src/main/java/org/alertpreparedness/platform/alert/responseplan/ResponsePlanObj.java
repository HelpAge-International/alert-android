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
    public int regionalApproval;
    public int countryApproval;
    public int globalApproval;

    public ResponsePlanObj(
            String hazardType,
            String completePercentage,
            String description,
            int status,
            Date lastUpdated,
            int regionalApproval,
            int countryApproval,
            int globalApproval
    ) {
        this.hazardType = hazardType;
        this.completePercentage = completePercentage;
        this.description = description;
        this.status = status;
        this.lastUpdated = lastUpdated;
        this.regionalApproval = regionalApproval;
        this.countryApproval = countryApproval;
        this.globalApproval = globalApproval;
    }

}
