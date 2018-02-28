package org.alertpreparedness.platform.alert.firebase.data_fetchers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseHazardRef;
import org.alertpreparedness.platform.alert.firebase.HazardModel;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.AppUtils;

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

    private HazardFetcherListener hazardFetcherListener;

    public HazardsFetcher(HazardFetcherListener hazardFetcherListener) {
        this.hazardFetcherListener = hazardFetcherListener;
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
                        HazardModel hazardModel =
                                AppUtils.getValueFromDataSnapshot(hazardWithIdSnapshot, HazardModel.class);
                        hazardModel.setId(hazardWithIdSnapshot.getKey());

                        hazardFetcherListener.onHazardFetcherResult(hazardModel);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Timber.e("Error fetching hazards for nerwork / country office: %s", databaseError);
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

    //region HazardFetcherListener
    public interface HazardFetcherListener {
        void onHazardFetcherResult(HazardModel hazardFetcherResult);
    }
    //endregion
}
