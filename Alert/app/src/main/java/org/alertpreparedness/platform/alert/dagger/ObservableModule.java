package org.alertpreparedness.platform.alert.dagger;

import com.google.firebase.database.DataSnapshot;

import org.alertpreparedness.platform.alert.dagger.annotation.AlertObservable;
import org.alertpreparedness.platform.alert.dagger.annotation.HazardObservable;
import org.alertpreparedness.platform.alert.dagger.annotation.IndicatorObservable;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.AlertFetcher;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.HazardsFetcher;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.IndicatorsFetcher;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.NetworkFetcher;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import durdinapps.rxfirebase2.RxFirebaseChildEvent;
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
    public Flowable<RxFirebaseChildEvent<DataSnapshot>> provideAlertFlowable() {
        return new AlertFetcher().rxFetch();
    }

    @Provides
    @Singleton
    @IndicatorObservable
    public Flowable<RxFirebaseChildEvent<DataSnapshot>> provideIndicatorFlowable() {
        return new IndicatorsFetcher().rxFetch();
    }

    @Provides
    @Singleton
    @HazardObservable
    public Flowable<RxFirebaseChildEvent<DataSnapshot>> provideHazardFlowable() {
        return new HazardsFetcher().rxFetch();
    }
}
