package org.alertpreparedness.platform.alert.firebase.data_fetchers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseHazardRef;
import org.alertpreparedness.platform.alert.model.User;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Fetches all hazard IDs that should be visible to the current user.
 */
public class HazardsFetcher implements FirebaseDataFetcher {

    @Inject
    @BaseHazardRef
    DatabaseReference baseHazardRef;

    @Inject
    User user;

    private HazardsFetcherListener hazardsFetcherListener;

    public HazardsFetcher(HazardsFetcherListener hazardsFetcherListener) {
        this.hazardsFetcherListener = hazardsFetcherListener;
        DependencyInjector.applicationComponent().inject(this);
    }

    //region FirebaseDataFetcher
    @Override
    public void fetch() {
        // Fetching the network IDs IDs
        new NetworkFetcher(networkFetcherResult -> {

            // Parsing hazard data snapshots
            ValueEventListener hazardsValueListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot hazardsSnapshot) {
                    for (DataSnapshot hazardWithIdSnapshot : hazardsSnapshot.getChildren()) {
                        hazardsFetcherListener.onHazardsFetcherResult(hazardWithIdSnapshot);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Timber.e("Error fetching hazards for network / country office: %s",
                            databaseError.getMessage());
                }
            };

            // Fetching all hazards for networks the the user is part of
            for (String networkId : networkFetcherResult.all()) {
                baseHazardRef
                        .child(networkId)
                        .addValueEventListener(hazardsValueListener);
            }

            // Fetching hazards for the country office
            baseHazardRef
                    .child(user.countryID)
                    .addValueEventListener(hazardsValueListener);
        });
    }
    //endregion

    //region HazardsFetcherListener
    public interface HazardsFetcherListener {
        void onHazardsFetcherResult(DataSnapshot hazardSnapshot);
    }
    //endregion
}
