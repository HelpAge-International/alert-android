package org.alertpreparedness.platform.alert.mycountry;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import org.alertpreparedness.platform.alert.firebase.AgencyModel;
import org.alertpreparedness.platform.alert.firebase.ProgrammeModel;

import java.util.List;

/**
 * Created by Tj on 02/01/2018.
 */

public class ProgrammeInfo extends ExpandableGroup<ProgrammeModel> {

    private AgencyModel agency;

    public ProgrammeInfo(AgencyModel agency, List<ProgrammeModel> items) {
        super(agency.getName(), items);
        this.agency = agency;
    }

    public AgencyModel getAgency() {
        return agency;
    }
}
