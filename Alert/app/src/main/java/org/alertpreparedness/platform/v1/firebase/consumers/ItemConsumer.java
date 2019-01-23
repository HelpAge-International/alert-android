package org.alertpreparedness.platform.v1.firebase.consumers;

import org.alertpreparedness.platform.v1.firebase.data_fetchers.FetcherResultItem;

/**
 * Created by Tj on 13/03/2018.
 */

public class ItemConsumer<T> extends FirebaseConsumer<T> {
    public ItemConsumer(OnChangedRemovedListener<T> onChangedRemovedListener) {
        super(onChangedRemovedListener);
    }

    public ItemConsumer(OnChangedListener<T> onChangedListener) {
        super(onChangedListener);
    }

    public ItemConsumer(OnRemovedListener<T> onRemovedListener) {
        super(onRemovedListener);
    }

    public ItemConsumer(OnChangedListener<T> onChangedListener, OnRemovedListener<T> onRemovedListener) {
        super(onChangedListener, onRemovedListener);
    }

    @Override
    public void accept(FetcherResultItem<T> fetcherResultItemFetcherResultItem) throws Exception {
        switch (fetcherResultItemFetcherResultItem.getEventType()) {
            case UPDATED:
                onChanged(fetcherResultItemFetcherResultItem.getValue());
                break;
            case REMOVED:
                onRemoved(fetcherResultItemFetcherResultItem.getValue());
                break;
        }
    }
}
