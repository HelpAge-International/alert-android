package org.alertpreparedness.platform.alert.firebase.data_fetchers;

import com.google.firebase.database.DataSnapshot;

import java.util.Collection;
import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by Tj on 08/03/2018.
 */

public interface RxFirebaseDataFetcher<FetchResult> {
    Flowable<FetcherResultItem<FetchResult>> rxFetch();
    Flowable<Collection<FetchResult>> rxFetchGroup();
}
