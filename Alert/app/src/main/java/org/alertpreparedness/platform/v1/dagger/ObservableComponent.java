package org.alertpreparedness.platform.v1.dagger;

import org.alertpreparedness.platform.v1.MainDrawer;
import org.alertpreparedness.platform.v1.adv_preparedness.activity.CreateAPAActivity;
import org.alertpreparedness.platform.v1.adv_preparedness.adapter.APActionAdapter;
import org.alertpreparedness.platform.v1.adv_preparedness.fragment.APAArchivedFragment;
import org.alertpreparedness.platform.v1.adv_preparedness.fragment.APACompletedFragment;
import org.alertpreparedness.platform.v1.adv_preparedness.fragment.APAExpiredFragment;
import org.alertpreparedness.platform.v1.adv_preparedness.fragment.APAInProgressFragment;
import org.alertpreparedness.platform.v1.adv_preparedness.fragment.APAInactiveFragment;
import org.alertpreparedness.platform.v1.adv_preparedness.fragment.APAUnassignedFragment;
import org.alertpreparedness.platform.v1.adv_preparedness.fragment.AdvPreparednessFragment;
import org.alertpreparedness.platform.v1.adv_preparedness.fragment.UsersListDialogFragment;
import org.alertpreparedness.platform.v1.dashboard.activity.AlertDetailActivity;
import org.alertpreparedness.platform.v1.dashboard.activity.CreateAlertActivity;
import org.alertpreparedness.platform.v1.dashboard.activity.UpdateAlertActivity;
import org.alertpreparedness.platform.v1.dashboard.adapter.AlertAdapter;
import org.alertpreparedness.platform.v1.dashboard.adapter.TaskAdapter;
import org.alertpreparedness.platform.v1.dashboard.fragment.HomeFragment;
import org.alertpreparedness.platform.v1.firebase.data_fetchers.AgencyFetcher;
import org.alertpreparedness.platform.v1.firebase.data_fetchers.AlertFetcher;
import org.alertpreparedness.platform.v1.firebase.data_fetchers.ClockSettingsFetcher;
import org.alertpreparedness.platform.v1.firebase.data_fetchers.HazardsFetcher;
import org.alertpreparedness.platform.v1.firebase.data_fetchers.IndicatorsFetcher;
import org.alertpreparedness.platform.v1.firebase.data_fetchers.NetworkFetcher;
import org.alertpreparedness.platform.v1.firebase.data_fetchers.ActionFetcher;
import org.alertpreparedness.platform.v1.firebase.wrappers.ResponsePlanResultItem;
import org.alertpreparedness.platform.v1.helper.DataHandler;
import org.alertpreparedness.platform.v1.helper.UserInfo;
import org.alertpreparedness.platform.v1.login.activity.LoginScreen;
import org.alertpreparedness.platform.v1.login.activity.SplashActivity;
import org.alertpreparedness.platform.v1.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.v1.min_preparedness.activity.CompleteActionActivity;
import org.alertpreparedness.platform.v1.min_preparedness.activity.ViewAttachmentAdapter;
import org.alertpreparedness.platform.v1.min_preparedness.activity.ViewAttachmentsActivity;
import org.alertpreparedness.platform.v1.min_preparedness.adapter.ActionAdapter;
import org.alertpreparedness.platform.v1.min_preparedness.fragment.ActionArchivedFragment;
import org.alertpreparedness.platform.v1.min_preparedness.fragment.ActionCompletedFragment;
import org.alertpreparedness.platform.v1.min_preparedness.fragment.ActionExpiredFragment;
import org.alertpreparedness.platform.v1.min_preparedness.fragment.ActionUnassignedFragment;
import org.alertpreparedness.platform.v1.min_preparedness.fragment.InProgressFragment;
import org.alertpreparedness.platform.v1.min_preparedness.fragment.MinPreparednessFragment;
import org.alertpreparedness.platform.v1.mycountry.MyCountryFragment;
import org.alertpreparedness.platform.v1.mycountry.ProgramResultsActivity;
import org.alertpreparedness.platform.v1.mycountry.ProgrammesAdapter;
import org.alertpreparedness.platform.v1.notifications.ActionNotificationService;
import org.alertpreparedness.platform.v1.notifications.ActionUpdateNotificationHandler;
import org.alertpreparedness.platform.v1.notifications.AlertFirebaseInstanceIDService;
import org.alertpreparedness.platform.v1.notifications.IndicatorFetcher;
import org.alertpreparedness.platform.v1.notifications.IndicatorNotificationService;
import org.alertpreparedness.platform.v1.notifications.IndicatorUpdateNotificationHandler;
import org.alertpreparedness.platform.v1.notifications.NotificationIdHandler;
import org.alertpreparedness.platform.v1.notifications.ResponsePlanFetcher;
import org.alertpreparedness.platform.v1.notifications.ResponsePlanNotificationService;
import org.alertpreparedness.platform.v1.notifications.ResponsePlanUpdateNotificationHandler;
import org.alertpreparedness.platform.v1.offline.OfflineSyncHandler;
import org.alertpreparedness.platform.v1.responseplan.ActiveFragment;
import org.alertpreparedness.platform.v1.responseplan.ApprovalNotesAdapter;
import org.alertpreparedness.platform.v1.responseplan.ArchivedFragment;
import org.alertpreparedness.platform.v1.responseplan.ResponsePlanFragment;
import org.alertpreparedness.platform.v1.risk_monitoring.dialog.BottomSheetDialog;
import org.alertpreparedness.platform.v1.risk_monitoring.view.IndicatorLogActivityFragment;
import org.alertpreparedness.platform.v1.risk_monitoring.view.RiskFragment;
import org.alertpreparedness.platform.v1.settings.ChangeEmailActivity;
import org.alertpreparedness.platform.v1.settings.ChangePasswordActivity;
import org.alertpreparedness.platform.v1.settings.SettingsFragment;
import org.alertpreparedness.platform.v1.utils.PermissionsHelper;
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
    void inject(org.alertpreparedness.platform.v1.notifications.ActionFetcher actionFetcher);
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

    void inject(org.alertpreparedness.platform.v1.firebase.data_fetchers.ResponsePlanFetcher responsePlanFetcher);

    void inject(ResponsePlanResultItem responsePlanResultItem);

    void inject(ApprovalNotesAdapter approvalNotesAdapter);

    void inject(AgencyFetcher agencyFetcher);

    void inject(@NotNull BottomSheetDialog bottomSheetDialog);

    void inject(@NotNull IndicatorLogActivityFragment indicatorLogActivityFragment);

    @Subcomponent.Builder
    interface Builder {
        ObservableComponent.Builder sessionModule(ObservableModule userModule);
        ObservableComponent build();
    }

}
