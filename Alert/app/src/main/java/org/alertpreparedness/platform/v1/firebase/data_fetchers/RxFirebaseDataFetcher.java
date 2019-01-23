package org.alertpreparedness.platform.v1.firebase.data_fetchers;

import java.util.Collection;

import io.reactivex.Flowable;

/**
 * Created by Tj on 08/03/2018.
 */

public interface RxFirebaseDataFetcher<FetchResult> {
    Flowable<FetcherResultItem<FetchResult>> rxFetch();
    Flowable<Collection<FetchResult>> rxFetchGroup();
}
