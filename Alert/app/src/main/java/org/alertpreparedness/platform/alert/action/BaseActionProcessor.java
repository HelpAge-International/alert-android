package org.alertpreparedness.platform.alert.action;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.alert.action.ActionProcessor;
import org.alertpreparedness.platform.alert.action.ActionProcessorListener;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionCHSRef;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionMandatedRef;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseCountryOfficeRef;
import org.alertpreparedness.platform.alert.dagger.annotation.NetworkRef;
import org.alertpreparedness.platform.alert.dagger.annotation.UserPublicRef;
import org.alertpreparedness.platform.alert.min_preparedness.model.DataModel;
import org.alertpreparedness.platform.alert.model.User;

import javax.inject.Inject;

/**
 * Created by Tj on 28/02/2018.
 */

public abstract class BaseActionProcessor implements ActionProcessor {
    @Inject
    @ActionCHSRef
    public DatabaseReference dbCHSRef;

    @Inject
    @ActionMandatedRef
    public DatabaseReference dbMandatedRef;

    @Inject
    @BaseActionRef
    public DatabaseReference dbActionBaseRef;

    @Inject
    @UserPublicRef
    public DatabaseReference dbUserPublicRef;

    @Inject
    @BaseCountryOfficeRef
    public DatabaseReference countryOffice;

    @Inject
    @NetworkRef
    public DatabaseReference dbNetworkRef;

    @Inject
    @ActionRef
    public DatabaseReference dbActionRef;

    @Inject
    @AgencyRef
    public DatabaseReference dbAgencyRef;

    @Inject
    User user;

    protected int type;
    protected DataSnapshot snapshot;
    protected final DataModel model;
    protected final String actionId;
    protected final String parentId;
    protected ActionProcessorListener listener;
    protected Boolean isCHS = false;
    protected Boolean isCHSAssigned = false;
    protected Boolean isMandated = false;
    protected Boolean isMandatedAssigned = false;
    protected Boolean isInProgress = false;
    protected int freqBase = 0;
    protected int freqValue = 0;

    public BaseActionProcessor(int type, DataSnapshot snapshot, DataModel model, String id, String parentId, ActionProcessorListener listener) {
        this.type = type;
        this.snapshot = snapshot;
        this.model = model;
        this.actionId = id;
        this.parentId = parentId;
        this.listener = listener;
        DependencyInjector.applicationComponent().inject(this);
    }
}
