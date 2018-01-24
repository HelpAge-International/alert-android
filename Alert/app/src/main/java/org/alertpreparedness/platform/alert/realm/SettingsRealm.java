package org.alertpreparedness.platform.alert.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Tj on 23/01/2018.
 */

public class SettingsRealm extends RealmObject {

    @PrimaryKey
    private String userId;

    private boolean canAssignCHS;
    private boolean canCompleteCHS;
    private boolean canViewCHS;

    private boolean canCreateMPA;
    private boolean canEditMPA;
    private boolean canAssignMPA;
    private boolean canDeleteMPA;
    private boolean canCompleteMPA;
    private boolean canViewMPA;

    private boolean canCreateCustomMPA;
    private boolean canEditCustomMPA;
    private boolean canAssignCustomMPA;
    private boolean canDeleteCustomMPA;
    private boolean canCompleteCustomMPA;
    private boolean canViewCustomMPA;

    private boolean canCreateMandatedAPA;
    private boolean canEditMandatedAPA;
    private boolean canAssignMandatedAPA;
    private boolean canDeleteMandatedAPA;
    private boolean canCompleteMandatedAPA;
    private boolean canViewMandatedAPA;

    private boolean canCreateCustomAPA;
    private boolean canEditCustomAPA;
    private boolean canAssignCustomAPA;
    private boolean canDeleteCustomAPA;
    private boolean canCompleteCustomAPA;
    private boolean canViewCustomAPA;

    private boolean canCreateHazard;
    private boolean canEditHazard;
    private boolean canAssignHazard;
    private boolean canDeleteHazard;
    private boolean canArchiveHazard;
    private boolean canViewHazard;

    private boolean canCreateHazardIndicators;
    private boolean canEditHazardIndicators;
    private boolean canAssignHazardIndicators;
    private boolean canDeleteHazardIndicators;
    private boolean canArchiveHazardIndicators;
    private boolean canViewHazardIndicators;

    private boolean canCreateNotes;

    private boolean canEditNotes;
    private boolean canDeleteNotes;
    private boolean canViewNotes;

    private boolean canViewAgencyCountryOffices;
    private boolean canCopyAgencyCountryOffices;

    private boolean canViewOtherAgencies;
    private boolean canCopyOtherAgencies;

    private boolean canCreateCountryContacts;
    private boolean canEditCountryContacts;
    private boolean canDeleteCountryContacts;

    public SettingsRealm() {

    }

    public boolean canAssignCHS() {
        return canAssignCHS;
    }

    public void setCanAssignCHS(boolean canAssignCHS) {
        this.canAssignCHS = canAssignCHS;
    }

    public boolean canCompleteCHS() {
        return canCompleteCHS;
    }

    public void setCanCompleteCHS(boolean canCompleteCHS) {
        this.canCompleteCHS = canCompleteCHS;
    }

    public boolean canViewCHS() {
        return canViewCHS;
    }

    public void setCanViewCHS(boolean canViewCHS) {
        this.canViewCHS = canViewCHS;
    }

    public boolean canCreateMPA() {
        return canCreateMPA;
    }

    public void setCanCreateMPA(boolean canCreateMPA) {
        this.canCreateMPA = canCreateMPA;
    }

    public boolean canEditMPA() {
        return canEditMPA;
    }

    public void setCanEditMPA(boolean canEditMPA) {
        this.canEditMPA = canEditMPA;
    }

    public boolean canAssignMPA() {
        return canAssignMPA;
    }

    public void setCanAssignMPA(boolean canAssignMPA) {
        this.canAssignMPA = canAssignMPA;
    }

    public boolean canDeleteMPA() {
        return canDeleteMPA;
    }

    public void setCanDeleteMPA(boolean canDeleteMPA) {
        this.canDeleteMPA = canDeleteMPA;
    }

    public boolean canCompleteMPA() {
        return canCompleteMPA;
    }

    public void setCanCompleteMPA(boolean canCompleteMPA) {
        this.canCompleteMPA = canCompleteMPA;
    }

    public boolean canViewMPA() {
        return canViewMPA;
    }

    public void setCanViewMPA(boolean canViewMPA) {
        this.canViewMPA = canViewMPA;
    }

    public boolean canCreateCustomMPA() {
        return canCreateCustomMPA;
    }

    public void setCanCreateCustomMPA(boolean canCreateCustomMPA) {
        this.canCreateCustomMPA = canCreateCustomMPA;
    }

    public boolean canEditCustomMPA() {
        return canEditCustomMPA;
    }

    public void setCanEditCustomMPA(boolean canEditCustomMPA) {
        this.canEditCustomMPA = canEditCustomMPA;
    }

    public boolean canAssignCustomMPA() {
        return canAssignCustomMPA;
    }

    public void setCanAssignCustomMPA(boolean canAssignCustomMPA) {
        this.canAssignCustomMPA = canAssignCustomMPA;
    }

    public boolean canDeleteCustomMPA() {
        return canDeleteCustomMPA;
    }

    public void setCanDeleteCustomMPA(boolean canDeleteCustomMPA) {
        this.canDeleteCustomMPA = canDeleteCustomMPA;
    }

    public boolean canCompleteCustomMPA() {
        return canCompleteCustomMPA;
    }

    public void setCanCompleteCustomMPA(boolean canCompleteCustomMPA) {
        this.canCompleteCustomMPA = canCompleteCustomMPA;
    }

    public boolean canViewCustomMPA() {
        return canViewCustomMPA;
    }

    public void setCanViewCustomMPA(boolean canViewCustomMPA) {
        this.canViewCustomMPA = canViewCustomMPA;
    }

    public boolean canCreateMandatedAPA() {
        return canCreateMandatedAPA;
    }

    public void setCanCreateMandatedAPA(boolean canCreateMandatedAPA) {
        this.canCreateMandatedAPA = canCreateMandatedAPA;
    }

    public boolean canEditMandatedAPA() {
        return canEditMandatedAPA;
    }

    public void setCanEditMandatedAPA(boolean canEditMandatedAPA) {
        this.canEditMandatedAPA = canEditMandatedAPA;
    }

    public boolean canAssignMandatedAPA() {
        return canAssignMandatedAPA;
    }

    public void setCanAssignMandatedAPA(boolean canAssignMandatedAPA) {
        this.canAssignMandatedAPA = canAssignMandatedAPA;
    }

    public boolean canDeleteMandatedAPA() {
        return canDeleteMandatedAPA;
    }

    public void setCanDeleteMandatedAPA(boolean canDeleteMandatedAPA) {
        this.canDeleteMandatedAPA = canDeleteMandatedAPA;
    }

    public boolean canCompleteMandatedAPA() {
        return canCompleteMandatedAPA;
    }

    public void setCanCompleteMandatedAPA(boolean canCompleteMandatedAPA) {
        this.canCompleteMandatedAPA = canCompleteMandatedAPA;
    }

    public boolean canViewMandatedAPA() {
        return canViewMandatedAPA;
    }

    public void setCanViewMandatedAPA(boolean canViewMandatedAPA) {
        this.canViewMandatedAPA = canViewMandatedAPA;
    }

    public boolean canCreateCustomAPA() {
        return canCreateCustomAPA;
    }

    public void setCanCreateCustomAPA(boolean canCreateCustomAPA) {
        this.canCreateCustomAPA = canCreateCustomAPA;
    }

    public boolean canEditCustomAPA() {
        return canEditCustomAPA;
    }

    public void setCanEditCustomAPA(boolean canEditCustomAPA) {
        this.canEditCustomAPA = canEditCustomAPA;
    }

    public boolean canAssignCustomAPA() {
        return canAssignCustomAPA;
    }

    public void setCanAssignCustomAPA(boolean canAssignCustomAPA) {
        this.canAssignCustomAPA = canAssignCustomAPA;
    }

    public boolean canDeleteCustomAPA() {
        return canDeleteCustomAPA;
    }

    public void setCanDeleteCustomAPA(boolean canDeleteCustomAPA) {
        this.canDeleteCustomAPA = canDeleteCustomAPA;
    }

    public boolean canCompleteCustomAPA() {
        return canCompleteCustomAPA;
    }

    public void setCanCompleteCustomAPA(boolean canCompleteCustomAPA) {
        this.canCompleteCustomAPA = canCompleteCustomAPA;
    }

    public boolean canViewCustomAPA() {
        return canViewCustomAPA;
    }

    public void setCanViewCustomAPA(boolean canViewCustomAPA) {
        this.canViewCustomAPA = canViewCustomAPA;
    }

    public boolean canCreateHazard() {
        return canCreateHazard;
    }

    public void setCanCreateHazard(boolean canCreateHazard) {
        this.canCreateHazard = canCreateHazard;
    }

    public boolean canEditHazard() {
        return canEditHazard;
    }

    public void setCanEditHazard(boolean canEditHazard) {
        this.canEditHazard = canEditHazard;
    }

    public boolean canAssignHazard() {
        return canAssignHazard;
    }

    public void setCanAssignHazard(boolean canAssignHazard) {
        this.canAssignHazard = canAssignHazard;
    }

    public boolean canDeleteHazard() {
        return canDeleteHazard;
    }

    public void setCanDeleteHazard(boolean canDeleteHazard) {
        this.canDeleteHazard = canDeleteHazard;
    }

    public boolean canArchiveHazard() {
        return canArchiveHazard;
    }

    public void setCanArchiveHazard(boolean canArchiveHazard) {
        this.canArchiveHazard = canArchiveHazard;
    }

    public boolean canViewHazard() {
        return canViewHazard;
    }

    public void setCanViewHazard(boolean canViewHazard) {
        this.canViewHazard = canViewHazard;
    }

    public boolean canCreateHazardIndicators() {
        return canCreateHazardIndicators;
    }

    public void setCanCreateHazardIndicators(boolean canCreateHazardIndicators) {
        this.canCreateHazardIndicators = canCreateHazardIndicators;
    }

    public boolean canEditHazardIndicators() {
        return canEditHazardIndicators;
    }

    public void setCanEditHazardIndicators(boolean canEditHazardIndicators) {
        this.canEditHazardIndicators = canEditHazardIndicators;
    }

    public boolean canAssignHazardIndicators() {
        return canAssignHazardIndicators;
    }

    public void setCanAssignHazardIndicators(boolean canAssignHazardIndicators) {
        this.canAssignHazardIndicators = canAssignHazardIndicators;
    }

    public boolean canDeleteHazardIndicators() {
        return canDeleteHazardIndicators;
    }

    public void setCanDeleteHazardIndicators(boolean canDeleteHazardIndicators) {
        this.canDeleteHazardIndicators = canDeleteHazardIndicators;
    }

    public boolean canArchiveHazardIndicators() {
        return canArchiveHazardIndicators;
    }

    public void setCanArchiveHazardIndicators(boolean canArchiveHazardIndicators) {
        this.canArchiveHazardIndicators = canArchiveHazardIndicators;
    }

    public boolean canViewHazardIndicators() {
        return canViewHazardIndicators;
    }

    public void setCanViewHazardIndicators(boolean canViewHazardIndicators) {
        this.canViewHazardIndicators = canViewHazardIndicators;
    }

    public boolean canViewAgencyCountryOffices() {
        return canViewAgencyCountryOffices;
    }

    public void setCanViewAgencyCountryOffices(boolean canViewAgencyCountryOffices) {
        this.canViewAgencyCountryOffices = canViewAgencyCountryOffices;
    }

    public boolean canCopyAgencyCountryOffices() {
        return canCopyAgencyCountryOffices;
    }

    public void setCanCopyAgencyCountryOffices(boolean canCopyAgencyCountryOffices) {
        this.canCopyAgencyCountryOffices = canCopyAgencyCountryOffices;
    }

    public boolean canViewOtherAgencies() {
        return canViewOtherAgencies;
    }

    public void setCanViewOtherAgencies(boolean canViewOtherAgencies) {
        this.canViewOtherAgencies = canViewOtherAgencies;
    }

    public boolean canCopyOtherAgencies() {
        return canCopyOtherAgencies;
    }

    public void setCanCopyOtherAgencies(boolean canCopyOtherAgencies) {
        this.canCopyOtherAgencies = canCopyOtherAgencies;
    }

    public boolean canCreateCountryContacts() {
        return canCreateCountryContacts;
    }

    public void setCanCreateCountryContacts(boolean canCreateCountryContacts) {
        this.canCreateCountryContacts = canCreateCountryContacts;
    }

    public boolean canEditCountryContacts() {
        return canEditCountryContacts;
    }

    public void setCanEditCountryContacts(boolean canEditCountryContacts) {
        this.canEditCountryContacts = canEditCountryContacts;
    }

    public boolean canDeleteCountryContacts() {
        return canDeleteCountryContacts;
    }

    public void setCanDeleteCountryContacts(boolean canDeleteCountryContacts) {
        this.canDeleteCountryContacts = canDeleteCountryContacts;
    }

    public boolean canDownloadDocuments() {
        return canDownloadDocuments;
    }

    public void setCanDownloadDocuments(boolean canDownloadDocuments) {
        this.canDownloadDocuments = canDownloadDocuments;
    }

    public boolean canCreateNotes() {
        return canCreateNotes;
    }

    public void setCanCreateNotes(boolean canCreateNotes) {
        this.canCreateNotes = canCreateNotes;
    }

    public boolean canEditNotes() {
        return canEditNotes;
    }

    public void setCanEditNotes(boolean canEditNotes) {
        this.canEditNotes = canEditNotes;
    }

    public boolean canDeleteNotes() {
        return canDeleteNotes;
    }

    public void setCanDeleteNotes(boolean canDeleteNotes) {
        this.canDeleteNotes = canDeleteNotes;
    }

    public boolean canViewNotes() {
        return canViewNotes;
    }

    public void setCanViewNotes(boolean canViewNotes) {
        this.canViewNotes = canViewNotes;
    }

    private boolean canDownloadDocuments;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
