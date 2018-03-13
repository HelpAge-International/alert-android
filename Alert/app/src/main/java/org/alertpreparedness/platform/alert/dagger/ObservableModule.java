package org.alertpreparedness.platform.alert.dagger;

import com.google.firebase.database.DataSnapshot;

import org.alertpreparedness.platform.alert.dagger.annotation.ActionGroupObservable;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionObservable;
import org.alertpreparedness.platform.alert.dagger.annotation.AlertGroupObservable;
import org.alertpreparedness.platform.alert.dagger.annotation.AlertObservable;
import org.alertpreparedness.platform.alert.dagger.annotation.HazardGroupObservable;
import org.alertpreparedness.platform.alert.dagger.annotation.HazardObservable;
import org.alertpreparedness.platform.alert.dagger.annotation.IndicatorGroupObservable;
import org.alertpreparedness.platform.alert.dagger.annotation.IndicatorObservable;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.AlertFetcher;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.FetcherResultItem;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.HazardsFetcher;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.IndicatorsFetcher;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.NetworkFetcher;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.TempActionFetcher;
import org.alertpreparedness.platform.alert.firebase.wrappers.ActionItemWrapper;

import java.util.Collection;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Flowable;

/**
 * Created by Tj on 08/03/2018.
 */

@Module
public class ObservableModule {

    @Provides
    @Singleton
    public Flowable<NetworkFetcher.NetworkFetcherResult> provideNetworkResultFlowable() {
        return new NetworkFetcher().rxFetch();
    }

    @Provides
    @Singleton
    @AlertObservable
    public Flowable<FetcherResultItem<DataSnapshot>> provideAlertFlowable() {
        return new AlertFetcher().rxFetch();
    }

    @Provides
    @Singleton
    @AlertGroupObservable
    public Flowable<Collection<DataSnapshot>> provideAlertGroupFlowable() {
        return new AlertFetcher().rxFetchGroup();
    }

    @Provides
    @Singleton
    @ActionObservable
    public Flowable<FetcherResultItem<ActionItemWrapper>> provideActionFlowable() {
        return new TempActionFetcher().rxFetch();
    }

    @Provides
    @Singleton
    @ActionGroupObservable
    public Flowable<Collection<ActionItemWrapper>> provideActionGroupFlowable() {
        return new TempActionFetcher().rxFetchGroup();
    }

    @Provides
    @Singleton
    @IndicatorObservable
    public Flowable<FetcherResultItem<DataSnapshot>> provideIndicatorFlowable() {
        return new IndicatorsFetcher().rxFetch();
    }


    @Provides
    @Singleton
    @IndicatorGroupObservable
    public Flowable<Collection<DataSnapshot>> provideIndicatorGroupFlowable() {
        return new IndicatorsFetcher().rxFetchGroup();
    }

    @Provides
    @Singleton
    @HazardObservable
    public Flowable<FetcherResultItem<DataSnapshot>> provideHazardFlowable() {
        return new HazardsFetcher().rxFetch();
    }

    @Provides
    @Singleton
    @HazardGroupObservable
    public Flowable<Collection<DataSnapshot>> provideHazardGroupFlowable() {
        return new HazardsFetcher().rxFetchGroup();
    }
}
