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

    public boolean isCanAssignCHS() {
        return canAssignCHS;
    }

    public void setCanAssignCHS(boolean canAssignCHS) {
        this.canAssignCHS = canAssignCHS;
    }

    public boolean isCanCompleteCHS() {
        return canCompleteCHS;
    }

    public void setCanCompleteCHS(boolean canCompleteCHS) {
        this.canCompleteCHS = canCompleteCHS;
    }

    public boolean isCanViewCHS() {
        return canViewCHS;
    }

    public void setCanViewCHS(boolean canViewCHS) {
        this.canViewCHS = canViewCHS;
    }

    public boolean isCanCreateMPA() {
        return canCreateMPA;
    }

    public void setCanCreateMPA(boolean canCreateMPA) {
        this.canCreateMPA = canCreateMPA;
    }

    public boolean isCanEditMPA() {
        return canEditMPA;
    }

    public void setCanEditMPA(boolean canEditMPA) {
        this.canEditMPA = canEditMPA;
    }

    public boolean isCanAssignMPA() {
        return canAssignMPA;
    }

    public void setCanAssignMPA(boolean canAssignMPA) {
        this.canAssignMPA = canAssignMPA;
    }

    public boolean isCanDeleteMPA() {
        return canDeleteMPA;
    }

    public void setCanDeleteMPA(boolean canDeleteMPA) {
        this.canDeleteMPA = canDeleteMPA;
    }

    public boolean isCanCompleteMPA() {
        return canCompleteMPA;
    }

    public void setCanCompleteMPA(boolean canCompleteMPA) {
        this.canCompleteMPA = canCompleteMPA;
    }

    public boolean isCanViewMPA() {
        return canViewMPA;
    }

    public void setCanViewMPA(boolean canViewMPA) {
        this.canViewMPA = canViewMPA;
    }

    public boolean isCanCreateCustomMPA() {
        return canCreateCustomMPA;
    }

    public void setCanCreateCustomMPA(boolean canCreateCustomMPA) {
        this.canCreateCustomMPA = canCreateCustomMPA;
    }

    public boolean isCanEditCustomMPA() {
        return canEditCustomMPA;
    }

    public void setCanEditCustomMPA(boolean canEditCustomMPA) {
        this.canEditCustomMPA = canEditCustomMPA;
    }

    public boolean isCanAssignCustomMPA() {
        return canAssignCustomMPA;
    }

    public void setCanAssignCustomMPA(boolean canAssignCustomMPA) {
        this.canAssignCustomMPA = canAssignCustomMPA;
    }

    public boolean isCanDeleteCustomMPA() {
        return canDeleteCustomMPA;
    }

    public void setCanDeleteCustomMPA(boolean canDeleteCustomMPA) {
        this.canDeleteCustomMPA = canDeleteCustomMPA;
    }

    public boolean isCanCompleteCustomMPA() {
        return canCompleteCustomMPA;
    }

    public void setCanCompleteCustomMPA(boolean canCompleteCustomMPA) {
        this.canCompleteCustomMPA = canCompleteCustomMPA;
    }

    public boolean isCanViewCustomMPA() {
        return canViewCustomMPA;
    }

    public void setCanViewCustomMPA(boolean canViewCustomMPA) {
        this.canViewCustomMPA = canViewCustomMPA;
    }

    public boolean isCanCreateMandatedAPA() {
        return canCreateMandatedAPA;
    }

    public void setCanCreateMandatedAPA(boolean canCreateMandatedAPA) {
        this.canCreateMandatedAPA = canCreateMandatedAPA;
    }

    public boolean isCanEditMandatedAPA() {
        return canEditMandatedAPA;
    }

    public void setCanEditMandatedAPA(boolean canEditMandatedAPA) {
        this.canEditMandatedAPA = canEditMandatedAPA;
    }

    public boolean isCanAssignMandatedAPA() {
        return canAssignMandatedAPA;
    }

    public void setCanAssignMandatedAPA(boolean canAssignMandatedAPA) {
        this.canAssignMandatedAPA = canAssignMandatedAPA;
    }

    public boolean isCanDeleteMandatedAPA() {
        return canDeleteMandatedAPA;
    }

    public void setCanDeleteMandatedAPA(boolean canDeleteMandatedAPA) {
        this.canDeleteMandatedAPA = canDeleteMandatedAPA;
    }

    public boolean isCanCompleteMandatedAPA() {
        return canCompleteMandatedAPA;
    }

    public void setCanCompleteMandatedAPA(boolean canCompleteMandatedAPA) {
        this.canCompleteMandatedAPA = canCompleteMandatedAPA;
    }

    public boolean isCanViewMandatedAPA() {
        return canViewMandatedAPA;
    }

    public void setCanViewMandatedAPA(boolean canViewMandatedAPA) {
        this.canViewMandatedAPA = canViewMandatedAPA;
    }

    public boolean isCanCreateCustomAPA() {
        return canCreateCustomAPA;
    }

    public void setCanCreateCustomAPA(boolean canCreateCustomAPA) {
        this.canCreateCustomAPA = canCreateCustomAPA;
    }

    public boolean isCanEditCustomAPA() {
        return canEditCustomAPA;
    }

    public void setCanEditCustomAPA(boolean canEditCustomAPA) {
        this.canEditCustomAPA = canEditCustomAPA;
    }

    public boolean isCanAssignCustomAPA() {
        return canAssignCustomAPA;
    }

    public void setCanAssignCustomAPA(boolean canAssignCustomAPA) {
        this.canAssignCustomAPA = canAssignCustomAPA;
    }

    public boolean isCanDeleteCustomAPA() {
        return canDeleteCustomAPA;
    }

    public void setCanDeleteCustomAPA(boolean canDeleteCustomAPA) {
        this.canDeleteCustomAPA = canDeleteCustomAPA;
    }

    public boolean isCanCompleteCustomAPA() {
        return canCompleteCustomAPA;
    }

    public void setCanCompleteCustomAPA(boolean canCompleteCustomAPA) {
        this.canCompleteCustomAPA = canCompleteCustomAPA;
    }

    public boolean isCanViewCustomAPA() {
        return canViewCustomAPA;
    }

    public void setCanViewCustomAPA(boolean canViewCustomAPA) {
        this.canViewCustomAPA = canViewCustomAPA;
    }

    public boolean isCanCreateHazard() {
        return canCreateHazard;
    }

    public void setCanCreateHazard(boolean canCreateHazard) {
        this.canCreateHazard = canCreateHazard;
    }

    public boolean isCanEditHazard() {
        return canEditHazard;
    }

    public void setCanEditHazard(boolean canEditHazard) {
        this.canEditHazard = canEditHazard;
    }

    public boolean isCanAssignHazard() {
        return canAssignHazard;
    }

    public void setCanAssignHazard(boolean canAssignHazard) {
        this.canAssignHazard = canAssignHazard;
    }

    public boolean isCanDeleteHazard() {
        return canDeleteHazard;
    }

    public void setCanDeleteHazard(boolean canDeleteHazard) {
        this.canDeleteHazard = canDeleteHazard;
    }

    public boolean isCanArchiveHazard() {
        return canArchiveHazard;
    }

    public void setCanArchiveHazard(boolean canArchiveHazard) {
        this.canArchiveHazard = canArchiveHazard;
    }

    public boolean isCanViewHazard() {
        return canViewHazard;
    }

    public void setCanViewHazard(boolean canViewHazard) {
        this.canViewHazard = canViewHazard;
    }

    public boolean isCanCreateHazardIndicators() {
        return canCreateHazardIndicators;
    }

    public void setCanCreateHazardIndicators(boolean canCreateHazardIndicators) {
        this.canCreateHazardIndicators = canCreateHazardIndicators;
    }

    public boolean isCanEditHazardIndicators() {
        return canEditHazardIndicators;
    }

    public void setCanEditHazardIndicators(boolean canEditHazardIndicators) {
        this.canEditHazardIndicators = canEditHazardIndicators;
    }

    public boolean isCanAssignHazardIndicators() {
        return canAssignHazardIndicators;
    }

    public void setCanAssignHazardIndicators(boolean canAssignHazardIndicators) {
        this.canAssignHazardIndicators = canAssignHazardIndicators;
    }

    public boolean isCanDeleteHazardIndicators() {
        return canDeleteHazardIndicators;
    }

    public void setCanDeleteHazardIndicators(boolean canDeleteHazardIndicators) {
        this.canDeleteHazardIndicators = canDeleteHazardIndicators;
    }

    public boolean isCanArchiveHazardIndicators() {
        return canArchiveHazardIndicators;
    }

    public void setCanArchiveHazardIndicators(boolean canArchiveHazardIndicators) {
        this.canArchiveHazardIndicators = canArchiveHazardIndicators;
    }

    public boolean isCanViewHazardIndicators() {
        return canViewHazardIndicators;
    }

    public void setCanViewHazardIndicators(boolean canViewHazardIndicators) {
        this.canViewHazardIndicators = canViewHazardIndicators;
    }

    public boolean isCanViewAgencyCountryOffices() {
        return canViewAgencyCountryOffices;
    }

    public void setCanViewAgencyCountryOffices(boolean canViewAgencyCountryOffices) {
        this.canViewAgencyCountryOffices = canViewAgencyCountryOffices;
    }

    public boolean isCanCopyAgencyCountryOffices() {
        return canCopyAgencyCountryOffices;
    }

    public void setCanCopyAgencyCountryOffices(boolean canCopyAgencyCountryOffices) {
        this.canCopyAgencyCountryOffices = canCopyAgencyCountryOffices;
    }

    public boolean isCanViewOtherAgencies() {
        return canViewOtherAgencies;
    }

    public void setCanViewOtherAgencies(boolean canViewOtherAgencies) {
        this.canViewOtherAgencies = canViewOtherAgencies;
    }

    public boolean isCanCopyOtherAgencies() {
        return canCopyOtherAgencies;
    }

    public void setCanCopyOtherAgencies(boolean canCopyOtherAgencies) {
        this.canCopyOtherAgencies = canCopyOtherAgencies;
    }

    public boolean isCanCreateCountryContacts() {
        return canCreateCountryContacts;
    }

    public void setCanCreateCountryContacts(boolean canCreateCountryContacts) {
        this.canCreateCountryContacts = canCreateCountryContacts;
    }

    public boolean isCanEditCountryContacts() {
        return canEditCountryContacts;
    }

    public void setCanEditCountryContacts(boolean canEditCountryContacts) {
        this.canEditCountryContacts = canEditCountryContacts;
    }

    public boolean isCanDeleteCountryContacts() {
        return canDeleteCountryContacts;
    }

    public void setCanDeleteCountryContacts(boolean canDeleteCountryContacts) {
        this.canDeleteCountryContacts = canDeleteCountryContacts;
    }

    public boolean isCanDownloadDocuments() {
        return canDownloadDocuments;
    }

    public void setCanDownloadDocuments(boolean canDownloadDocuments) {
        this.canDownloadDocuments = canDownloadDocuments;
    }

    public boolean isCanCreateNotes() {
        return canCreateNotes;
    }

    public void setCanCreateNotes(boolean canCreateNotes) {
        this.canCreateNotes = canCreateNotes;
    }

    public boolean isCanEditNotes() {
        return canEditNotes;
    }

    public void setCanEditNotes(boolean canEditNotes) {
        this.canEditNotes = canEditNotes;
    }

    public boolean isCanDeleteNotes() {
        return canDeleteNotes;
    }

    public void setCanDeleteNotes(boolean canDeleteNotes) {
        this.canDeleteNotes = canDeleteNotes;
    }

    public boolean isCanViewNotes() {
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
