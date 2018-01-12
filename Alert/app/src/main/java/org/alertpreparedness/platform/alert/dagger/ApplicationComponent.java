package org.alertpreparedness.platform.alert.dagger;


import org.alertpreparedness.platform.alert.MainDrawer;
import org.alertpreparedness.platform.alert.adv_preparedness.adapter.APActionAdapter;
import org.alertpreparedness.platform.alert.adv_preparedness.fragment.APAArchivedFragment;
import org.alertpreparedness.platform.alert.adv_preparedness.fragment.APACompletedFragment;
import org.alertpreparedness.platform.alert.adv_preparedness.fragment.APAExpiredFragment;
import org.alertpreparedness.platform.alert.adv_preparedness.fragment.APAInProgressFragment;
import org.alertpreparedness.platform.alert.adv_preparedness.fragment.APAInactiveFragment;
import org.alertpreparedness.platform.alert.adv_preparedness.fragment.APAUnassignedFragment;
import org.alertpreparedness.platform.alert.adv_preparedness.fragment.AdvPreparednessFragment;
import org.alertpreparedness.platform.alert.adv_preparedness.fragment.UsersListDialogFragment;
import org.alertpreparedness.platform.alert.dashboard.activity.CreateAlertActivity;
import org.alertpreparedness.platform.alert.dashboard.adapter.AlertAdapter;
import org.alertpreparedness.platform.alert.dashboard.adapter.TaskAdapter;
import org.alertpreparedness.platform.alert.dashboard.fragment.HomeFragment;
import org.alertpreparedness.platform.alert.helper.DataHandler;
import org.alertpreparedness.platform.alert.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.CompleteActionActivity;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.login.activity.LoginScreen;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.ActionAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.fragment.InProgressFragment;
import org.alertpreparedness.platform.alert.mycountry.MyCountryFragment;
import org.alertpreparedness.platform.alert.mycountry.ProgramResultsActivity;
import org.alertpreparedness.platform.alert.responseplan.ActiveFragment;
import org.alertpreparedness.platform.alert.responseplan.ArchivedFragment;
import org.alertpreparedness.platform.alert.dashboard.activity.AlertDetailActivity;
import org.alertpreparedness.platform.alert.dashboard.activity.UpdateAlertActivity;
import org.alertpreparedness.platform.alert.helper.DataHandler;
import org.alertpreparedness.platform.alert.min_preparedness.fragment.MinPreparednessFragment;
import org.alertpreparedness.platform.alert.responseplan.ResponsePlanFragment;
import org.alertpreparedness.platform.alert.settings.ChangeEmailActivity;
import org.alertpreparedness.platform.alert.settings.ChangePasswordActivity;
import org.alertpreparedness.platform.alert.settings.SettingsFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Tj on 13/12/2017.
 */

@Singleton
@Component(modules = {ApplicationModule.class, FirebaseModule.class})
public interface ApplicationComponent {
    void inject(ResponsePlanFragment fragment);
    void inject(HomeFragment fragment);
    void inject(CreateAlertActivity activity);
    void inject(DataHandler dataHandler);
    void inject(MyCountryFragment fragment);
    void inject(ActiveFragment fragment);
    void inject(ArchivedFragment fragment);
    void inject(UpdateAlertActivity activity);
    void inject(AlertDetailActivity activity);
    void inject(MinPreparednessFragment fragment);
    void inject(TaskAdapter adapter);
    void inject(SettingsFragment frag);
    void inject(ChangeEmailActivity a);
    void inject(ChangePasswordActivity a);
    void inject(InProgressFragment fragment);
    void inject(ActionAdapter adapter);
    void inject(CompleteActionActivity activity);
    void inject(AddNotesActivity activity);
    void inject(MainDrawer drawer);
    void inject(UserInfo userInfo);
    void inject(AlertAdapter alertAdapter);
    void inject(LoginScreen loginScreen);
    void inject(ProgramResultsActivity programResultsActivity);
    void inject(AdvPreparednessFragment fragment);
    void inject(APAInProgressFragment fragment);
    void inject(APAExpiredFragment fragment);
    void inject(APAUnassignedFragment fragment);
    void inject(APACompletedFragment fragment);
    void inject(APAArchivedFragment fragment);
    void inject(APAInactiveFragment fragment);
    void inject(APActionAdapter adapter);
    void inject(UsersListDialogFragment fragment);

}
