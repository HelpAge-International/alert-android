package org.alertpreparedness.platform.alert.responseplan;

import org.alertpreparedness.platform.alert.min_preparedness.model.Note;

import java.util.Date;
import java.util.HashMap;

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

    private HashMap<String, Note> notes = new HashMap<>();

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

    public HashMap<String, Note> getNotes() {
        return notes;
    }

    public void setNotes(HashMap<String, Note> notes) {
        this.notes = notes;
    }

    public void addNote(String key, Note note) {
        this.notes.put(key, note);
    }

    public void removeNote(String key) {
        notes.remove(key);
    }
}
