package org.alertpreparedness.platform.v1.dagger;

import com.google.firebase.database.DataSnapshot;

import org.alertpreparedness.platform.v1.dagger.annotation.ActionGroupObservable;
import org.alertpreparedness.platform.v1.dagger.annotation.ActionObservable;
import org.alertpreparedness.platform.v1.dagger.annotation.ActiveActionObservable;
import org.alertpreparedness.platform.v1.dagger.annotation.AgencyObservable;
import org.alertpreparedness.platform.v1.dagger.annotation.AlertGroupObservable;
import org.alertpreparedness.platform.v1.dagger.annotation.AlertObservable;
import org.alertpreparedness.platform.v1.dagger.annotation.ClockSettingsActionObservable;
import org.alertpreparedness.platform.v1.dagger.annotation.HazardGroupObservable;
import org.alertpreparedness.platform.v1.dagger.annotation.HazardObservable;
import org.alertpreparedness.platform.v1.dagger.annotation.InActiveActionObservable;
import org.alertpreparedness.platform.v1.dagger.annotation.IndicatorGroupObservable;
import org.alertpreparedness.platform.v1.dagger.annotation.IndicatorObservable;
import org.alertpreparedness.platform.v1.dagger.annotation.ResponsePlanObservable;
import org.alertpreparedness.platform.v1.firebase.data_fetchers.ActionFetcher;
import org.alertpreparedness.platform.v1.firebase.data_fetchers.AgencyFetcher;
import org.alertpreparedness.platform.v1.firebase.data_fetchers.AlertFetcher;
import org.alertpreparedness.platform.v1.firebase.data_fetchers.ClockSettingsFetcher;
import org.alertpreparedness.platform.v1.firebase.data_fetchers.FetcherResultItem;
import org.alertpreparedness.platform.v1.firebase.data_fetchers.HazardsFetcher;
import org.alertpreparedness.platform.v1.firebase.data_fetchers.IndicatorsFetcher;
import org.alertpreparedness.platform.v1.firebase.data_fetchers.NetworkFetcher;
import org.alertpreparedness.platform.v1.firebase.data_fetchers.ResponsePlanFetcher;
import org.alertpreparedness.platform.v1.firebase.wrappers.AlertResultWrapper;
import org.alertpreparedness.platform.v1.firebase.wrappers.ResponsePlanResultItem;
import org.alertpreparedness.platform.v1.firebase.wrappers.ActionItemWrapper;

import java.util.Collection;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Flowable;

/**
 * Created by Tj on 08/03/2018.
 */

@Module
public class ObservableModule {

    @Provides
    @UserScope
    public Flowable<NetworkFetcher.NetworkFetcherResult> provideNetworkResultFlowable() {
        return new NetworkFetcher().rxFetch();
    }

    @Provides
    @AlertObservable
    @UserScope
    public Flowable<FetcherResultItem<DataSnapshot>> provideAlertFlowable() {
        return new AlertFetcher().rxFetch();
    }

    @Provides
    @AlertGroupObservable
    @UserScope
    public Flowable<Collection<DataSnapshot>> provideAlertGroupFlowable() {
        return new AlertFetcher().rxFetchGroup();
    }

    @Provides
    @ActionObservable
    @UserScope
    public Flowable<FetcherResultItem<ActionItemWrapper>> provideActionFlowable() {
        return new ActionFetcher().rxFetch();
    }

    @Provides
    @ActiveActionObservable
    @UserScope
    public Flowable<FetcherResultItem<Collection<ActionItemWrapper>>> provideActiveActionFlowable() {
        return new ActionFetcher().rxActiveItems(true);
    }

    @Provides
    @InActiveActionObservable
    @UserScope
    public Flowable<FetcherResultItem<Collection<ActionItemWrapper>>> provideInActiveActionFlowable() {
        return new ActionFetcher().rxActiveItems(false);
    }


    @Provides
    @ActionGroupObservable
    @UserScope
    public Flowable<Collection<ActionItemWrapper>> provideActionGroupFlowable() {
        return new ActionFetcher().rxFetchGroup();
    }

    @Provides

    @ClockSettingsActionObservable
    @UserScope
    public Flowable<Collection<ActionItemWrapper>> provideClockSettingsActionGroupFlowable(@ActionGroupObservable Flowable<Collection<ActionItemWrapper>> flowable) {
        return new ActionFetcher().rxFetchWithClockSettings(flowable);
    }

    @Provides
    @IndicatorObservable
    @UserScope
    public Flowable<FetcherResultItem<DataSnapshot>> provideIndicatorFlowable() {
        return new IndicatorsFetcher().rxFetch();
    }


    @Provides
    @IndicatorGroupObservable
    @UserScope
    public Flowable<Collection<DataSnapshot>> provideIndicatorGroupFlowable() {
        return new IndicatorsFetcher().rxFetchGroup();
    }

    @Provides
    @HazardObservable
    @UserScope
    public Flowable<FetcherResultItem<DataSnapshot>> provideHazardFlowable() {
        return new HazardsFetcher().rxFetch();
    }

    @Provides
    @HazardGroupObservable
    @UserScope
    public Flowable<Collection<DataSnapshot>> provideHazardGroupFlowable() {
        return new HazardsFetcher().rxFetchGroup();
    }

    @Provides
    @HazardGroupObservable
    @UserScope
    public Flowable<Collection<ResponsePlanResultItem>> provideResponsePlanGroupFlowable() {
        return new ResponsePlanFetcher().rxFetchGroup();
    }

    @Provides
    @ResponsePlanObservable
    @UserScope
    public Flowable<FetcherResultItem<ResponsePlanResultItem>> provideResponsePlans() {
        return new ResponsePlanFetcher().rxFetch();
    }

    @Provides
    @AgencyObservable
    @UserScope
    public Flowable<FetcherResultItem<DataSnapshot>> provideAgency() {
        return new AgencyFetcher().rxFetch();
    }

    @Provides
    @UserScope
    public Flowable<FetcherResultItem<AlertResultWrapper>> provideAlertsExtraFlowable() {
        return new AlertFetcher().fetchWithExtra();
    }

    @Provides
    @PreparednessClockSettingsFlowable
    @UserScope
    public Flowable<ClockSettingsFetcher.ClockSettingsResult> providePreparednessClockSettingsFlowable(){
        return new ClockSettingsFetcher().rxFetch(ClockSettingsFetcher.TYPE_PREPAREDNESS);
    }


}
