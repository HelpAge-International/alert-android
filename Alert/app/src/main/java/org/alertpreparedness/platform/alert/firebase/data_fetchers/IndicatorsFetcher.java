package org.alertpreparedness.platform.alert.firebase.data_fetchers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.dagger.annotation.BaseIndicatorRef;
import org.alertpreparedness.platform.alert.model.User;

import javax.inject.Inject;

import timber.log.Timber;

public class IndicatorsFetcher implements FirebaseDataFetcher {

    @Inject
    @BaseIndicatorRef
    DatabaseReference baseIndicatorRef;

    @Inject
    User user;

    private IndicatorsFetcherListener indicatorsFetcherListener;

    public IndicatorsFetcher(IndicatorsFetcherListener indicatorsFetcherListener) {
        this.indicatorsFetcherListener = indicatorsFetcherListener;
    }

    //region FirebaseDataFetcher
    @Override
    public void fetch() {
        ValueEventListener indicatorsValueEventListener =
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot indicatorsSnapshot) {
                        for (DataSnapshot indicatorSnapshot : indicatorsSnapshot.getChildren()) {
                            indicatorsFetcherListener.onIndicatorsFetcherResult(indicatorSnapshot);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Timber.e("Error fetching hazards for network / country office: %s",
                                databaseError.getMessage());
                    }
                };

        new HazardsFetcher(hazardSnapshot -> {
            String hazardId = hazardSnapshot.getKey();
            baseIndicatorRef.child(hazardId).addValueEventListener(indicatorsValueEventListener);
        });

        new NetworkFetcher(networkFetcherResult -> {
            for (String networkId : networkFetcherResult.all()) {
                baseIndicatorRef
                        .child(networkId)
                        .addValueEventListener(indicatorsValueEventListener);
            }
        });

        String countryId = user.countryID;
        baseIndicatorRef
                .child(countryId)
                .addValueEventListener(indicatorsValueEventListener);
    }
    //endregion

    //region IndicatorsFetcherListener
    public interface IndicatorsFetcherListener {
        void onIndicatorsFetcherResult(DataSnapshot indicatorSnapshot);
    }
    //endregion
}
