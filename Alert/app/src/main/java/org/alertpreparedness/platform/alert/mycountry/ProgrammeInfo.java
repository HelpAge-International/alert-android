package org.alertpreparedness.platform.alert.mycountry;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

/**
 * Created by Tj on 02/01/2018.
 */

public class ProgrammeInfo extends ExpandableGroup<Programme> {

    public ProgrammeInfo(String title, List<Programme> items) {
        super(title, items);
    }
}
