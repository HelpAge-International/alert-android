package org.alertpreparedness.platform.alert.dagger;

import org.alertpreparedness.platform.alert.MainDrawer;
import org.alertpreparedness.platform.alert.adv_preparedness.activity.CreateAPAActivity;
import org.alertpreparedness.platform.alert.adv_preparedness.adapter.APActionAdapter;
import org.alertpreparedness.platform.alert.adv_preparedness.fragment.APAArchivedFragment;
import org.alertpreparedness.platform.alert.adv_preparedness.fragment.APACompletedFragment;
import org.alertpreparedness.platform.alert.adv_preparedness.fragment.APAExpiredFragment;
import org.alertpreparedness.platform.alert.adv_preparedness.fragment.APAInProgressFragment;
import org.alertpreparedness.platform.alert.adv_preparedness.fragment.APAInactiveFragment;
import org.alertpreparedness.platform.alert.adv_preparedness.fragment.APAUnassignedFragment;
import org.alertpreparedness.platform.alert.adv_preparedness.fragment.AdvPreparednessFragment;
import org.alertpreparedness.platform.alert.adv_preparedness.fragment.UsersListDialogFragment;
import org.alertpreparedness.platform.alert.dashboard.activity.AlertDetailActivity;
import org.alertpreparedness.platform.alert.dashboard.activity.CreateAlertActivity;
import org.alertpreparedness.platform.alert.dashboard.activity.UpdateAlertActivity;
import org.alertpreparedness.platform.alert.dashboard.adapter.AlertAdapter;
import org.alertpreparedness.platform.alert.dashboard.adapter.TaskAdapter;
import org.alertpreparedness.platform.alert.dashboard.fragment.HomeFragment;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.AgencyFetcher;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.AlertFetcher;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.ClockSettingsFetcher;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.HazardsFetcher;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.IndicatorsFetcher;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.NetworkFetcher;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.ActionFetcher;
import org.alertpreparedness.platform.alert.firebase.wrappers.ResponsePlanResultItem;
import org.alertpreparedness.platform.alert.helper.DataHandler;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.login.activity.LoginScreen;
import org.alertpreparedness.platform.alert.login.activity.SplashActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.CompleteActionActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.ViewAttachmentAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.activity.ViewAttachmentsActivity;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.ActionAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.fragment.ActionArchivedFragment;
import org.alertpreparedness.platform.alert.min_preparedness.fragment.ActionCompletedFragment;
import org.alertpreparedness.platform.alert.min_preparedness.fragment.ActionExpiredFragment;
import org.alertpreparedness.platform.alert.min_preparedness.fragment.ActionUnassignedFragment;
import org.alertpreparedness.platform.alert.min_preparedness.fragment.InProgressFragment;
import org.alertpreparedness.platform.alert.min_preparedness.fragment.MinPreparednessFragment;
import org.alertpreparedness.platform.alert.mycountry.MyCountryFragment;
import org.alertpreparedness.platform.alert.mycountry.ProgramResultsActivity;
import org.alertpreparedness.platform.alert.mycountry.ProgrammesAdapter;
import org.alertpreparedness.platform.alert.notifications.ActionNotificationService;
import org.alertpreparedness.platform.alert.notifications.ActionUpdateNotificationHandler;
import org.alertpreparedness.platform.alert.notifications.AlertFirebaseInstanceIDService;
import org.alertpreparedness.platform.alert.notifications.IndicatorFetcher;
import org.alertpreparedness.platform.alert.notifications.IndicatorNotificationService;
import org.alertpreparedness.platform.alert.notifications.IndicatorUpdateNotificationHandler;
import org.alertpreparedness.platform.alert.notifications.NotificationIdHandler;
import org.alertpreparedness.platform.alert.notifications.ResponsePlanFetcher;
import org.alertpreparedness.platform.alert.notifications.ResponsePlanNotificationService;
import org.alertpreparedness.platform.alert.notifications.ResponsePlanUpdateNotificationHandler;
import org.alertpreparedness.platform.alert.offline.OfflineSyncHandler;
import org.alertpreparedness.platform.alert.responseplan.ActiveFragment;
import org.alertpreparedness.platform.alert.responseplan.ApprovalNotesAdapter;
import org.alertpreparedness.platform.alert.responseplan.ArchivedFragment;
import org.alertpreparedness.platform.alert.responseplan.ResponsePlanFragment;
import org.alertpreparedness.platform.alert.risk_monitoring.dialog.BottomSheetDialog;
import org.alertpreparedness.platform.alert.risk_monitoring.view.RiskFragment;
import org.alertpreparedness.platform.alert.settings.ChangeEmailActivity;
import org.alertpreparedness.platform.alert.settings.ChangePasswordActivity;
import org.alertpreparedness.platform.alert.settings.SettingsFragment;
import org.alertpreparedness.platform.alert.utils.PermissionsHelper;
import org.jetbrains.annotations.NotNull;

import dagger.Subcomponent;

/**
 * Created by Tj on 21/03/2018.
 */

@UserScope
@Subcomponent(modules = {ObservableModule.class})
public interface ObservableComponent {

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
    void inject(SplashActivity splashActivity);
    void inject(CreateAPAActivity activity);
    void inject(OfflineSyncHandler offlineSyncHandler);
    void inject(IndicatorUpdateNotificationHandler indicatorUpdateNotificationHandler);
    void inject(IndicatorFetcher indicatorFetcher);
    void inject(HazardsFetcher hazardsFetcher);
    void inject(IndicatorsFetcher indicatorsFetcher);
    void inject(IndicatorNotificationService indicatorNotificationService);
    void inject(org.alertpreparedness.platform.alert.notifications.ActionFetcher actionFetcher);
    void inject(ActionNotificationService actionNotificationService);
    void inject(ActionUpdateNotificationHandler actionUpdateNotificationHandler);
    void inject(ActionArchivedFragment actionArchivedFragment);
    void inject(ActionCompletedFragment actionCompletedFragment);
    void inject(ActionExpiredFragment actionExpiredFragment);
    void inject(ActionUnassignedFragment actionUnassignedFragment);
    void inject(ViewAttachmentsActivity viewAttachmentsActivity);
    void inject(ViewAttachmentAdapter viewAttachmentAdapter);
    void inject(AlertFirebaseInstanceIDService alertFirebaseInstanceIDService);
    void inject(NetworkFetcher networkFetcher);
    void inject(ResponsePlanFetcher responsePlanFetcher);
    void inject(ResponsePlanNotificationService responsePlanNotificationService);
    void inject(ResponsePlanUpdateNotificationHandler responsePlanUpdateNotificationHandler);

    void inject(AlertFetcher alertFetcher);

    void inject(ProgrammesAdapter programmesAdapter);

    void inject(ClockSettingsFetcher clockSettingsFetcher);

    void inject(NotificationIdHandler notificationIdHandler);

    void inject(PermissionsHelper permissionsHelper);

    void inject(@NotNull RiskFragment riskFragment);

    void inject(ActionFetcher tempActionFetcher);

    void inject(org.alertpreparedness.platform.alert.firebase.data_fetchers.ResponsePlanFetcher responsePlanFetcher);

    void inject(ResponsePlanResultItem responsePlanResultItem);

    void inject(ApprovalNotesAdapter approvalNotesAdapter);

    void inject(AgencyFetcher agencyFetcher);

    void inject(@NotNull BottomSheetDialog bottomSheetDialog);

    @Subcomponent.Builder
    interface Builder {
        ObservableComponent.Builder sessionModule(ObservableModule userModule);
        ObservableComponent build();
    }

}