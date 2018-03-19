package org.alertpreparedness.platform.alert.firebase.data_fetchers;

import android.util.Pair;

import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.AlertGroupObservable;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseActionCHSRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseActionMandatedRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.alert.firebase.ActionModel;
import org.alertpreparedness.platform.alert.firebase.AlertModel;
import org.alertpreparedness.platform.alert.firebase.ClockSetting;
import org.alertpreparedness.platform.alert.firebase.wrappers.ActionItemWrapper;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.AppUtils;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.intellij.lang.annotations.Flow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import durdinapps.rxfirebase2.RxFirebaseChildEvent;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import io.reactivex.Flowable;
import io.reactivex.functions.BiFunction;
import io.reactivex.rxkotlin.Flowables;

import static org.alertpreparedness.platform.alert.firebase.data_fetchers.FetcherResultItem.EventType.REMOVED;
import static org.alertpreparedness.platform.alert.firebase.data_fetchers.FetcherResultItem.EventType.UPDATED;

/**
 * Created by Tj on 08/03/2018.
 */

public class TempActionFetcher implements RxFirebaseDataFetcher<ActionItemWrapper> {

    @Inject
    User user;

    @Inject
    @BaseActionRef
    DatabaseReference dbActionBaseRef;

    @Inject
    @BaseActionCHSRef
    DatabaseReference dbBaseActionCHSRef;

    @Inject
    @BaseActionMandatedRef
    DatabaseReference dbBaseActionMandatedRef;

    @Inject
    Flowable<NetworkFetcher.NetworkFetcherResult> networkResultFlowable;

    @Inject
    @AlertGroupObservable
    Flowable<Collection<DataSnapshot>> alertGroupFlowable;

    public TempActionFetcher() {
        DependencyInjector.applicationComponent().inject(this);
    }


    private Flowable<FetcherResultItem<ActionItemWrapper>> rxFetchActions(){

        return networkResultFlowable
                .flatMap(networkFetcherResult -> {
                    Flowable<Pair<ActionItemWrapper.Group, RxFirebaseChildEvent<DataSnapshot>>> flow = RxFirebaseDatabase.observeChildEvent(dbActionBaseRef.child(user.countryID).orderByChild("type").equalTo(2))
                            .map(dataSnapshotRxFirebaseChildEvent -> new Pair<>(ActionItemWrapper.Group.COUNTRY, dataSnapshotRxFirebaseChildEvent));

                    for (String localNetworkId : networkFetcherResult.getLocalNetworks()) {
                        flow = flow.mergeWith(RxFirebaseDatabase.observeChildEvent(dbActionBaseRef.child(localNetworkId).orderByChild("type").equalTo(2))
                                .map(dataSnapshotRxFirebaseChildEvent -> new Pair<>(ActionItemWrapper.Group.LOCAL_NETWORK, dataSnapshotRxFirebaseChildEvent)));
                    }
                    for (String networkCountryId : networkFetcherResult.getLocalNetworks()) {
                        flow = flow.mergeWith(RxFirebaseDatabase.observeChildEvent(dbActionBaseRef.child(networkCountryId).orderByChild("type").equalTo(2))
                                .map(dataSnapshotRxFirebaseChildEvent -> new Pair<>(ActionItemWrapper.Group.NETWORK_COUNTRY, dataSnapshotRxFirebaseChildEvent)));
                    }

                    return flow;
                })
                .map(pair -> {
                    FetcherResultItem.EventType eventType = pair.second.getEventType() == RxFirebaseChildEvent.EventType.REMOVED ? REMOVED : UPDATED;

                    return new FetcherResultItem<>(ActionItemWrapper.createAction(pair.second.getValue(), pair.first), eventType);
                });
//
    }

    public Flowable<FetcherResultItem<ActionItemWrapper>> rxFetchWithClockSettings(Flowable<FetcherResultItem<ActionItemWrapper>> flowable) {
        Flowable<ClockSettingsFetcher.ClockSettingsResult> clockSettingsResultFlowable = new ClockSettingsFetcher().rxFetch(ClockSettingsFetcher.TYPE_PREPAREDNESS);
        return Flowable.combineLatest(clockSettingsResultFlowable, flowable,
                (clockSettingsResult, actionItemWrapperFetcherResultItem) -> {
                    DataSnapshot snapshot = actionItemWrapperFetcherResultItem.getValue().getActionSnapshot();
                    if(snapshot == null) {
                        snapshot = actionItemWrapperFetcherResultItem.getValue().getTypeSnapshot();
                    }
                    actionItemWrapperFetcherResultItem.getValue().setClockSetting(clockSettingsResult.all().get(snapshot.getRef().getParent().getKey()));
                    return actionItemWrapperFetcherResultItem;
                });

    }

    private Flowable<FetcherResultItem<ActionItemWrapper>> rxFetchCHSActions(){
        return networkResultFlowable.flatMap(networkFetcherResult -> {
            Flowable<RxFirebaseChildEvent<DataSnapshot>> flow = RxFirebaseDatabase.observeChildEvent(dbBaseActionCHSRef.child(user.getSystemAdminID()));

            for (String networkId : networkFetcherResult.all()) {
                flow = flow.mergeWith(RxFirebaseDatabase.observeChildEvent(dbBaseActionCHSRef.child(networkId)));
            }

            return flow;
        }).flatMap(chsAction -> {
            return RxFirebaseDatabase.observeValueEvent(dbActionBaseRef.child(user.countryID).child(chsAction.getValue().getKey()))
                    .map(action -> new Pair<>(chsAction, action));
        }).map(pair -> {
            if (pair.second.exists()) {
                //TODO: The group here shouldn't be none, should correspond to the actual action group (See rxFetchActions)
                return new FetcherResultItem<>(ActionItemWrapper.createCHS(pair.first.getValue(), pair.second, ActionItemWrapper.Group.NONE), RxFirebaseChildEvent.EventType.CHANGED);
            }
            else {
                return new FetcherResultItem<>(ActionItemWrapper.createCHS(pair.first.getValue()), pair.first.getEventType() == RxFirebaseChildEvent.EventType.REMOVED ? REMOVED : UPDATED);
            }
        });
    }

    private Flowable<FetcherResultItem<ActionItemWrapper>> rxFetchMandatedActions(){
        return networkResultFlowable.flatMap(networkFetcherResult -> {
            Flowable<RxFirebaseChildEvent<DataSnapshot>> flow = RxFirebaseDatabase.observeChildEvent(dbBaseActionMandatedRef.child(user.agencyAdminID));

            for (String networkId : networkFetcherResult.all()) {
                flow = flow.mergeWith(RxFirebaseDatabase.observeChildEvent(dbBaseActionMandatedRef.child(networkId)));
            }

            return flow;
        }).flatMap(mandatedAction -> {
            String mandatedActionParentId = mandatedAction.getValue().getRef().getParent().getKey();
            if(mandatedActionParentId.equals(user.agencyAdminID)){
                mandatedActionParentId = user.countryID;
            }
            return RxFirebaseDatabase.observeValueEvent(dbActionBaseRef.child(mandatedActionParentId).child(mandatedAction.getValue().getKey()))
                    .map(action -> new Pair<>(mandatedAction, action));
        }).map(pair -> {
            if (pair.second.exists()) {
                //TODO: The group here shouldn't be none, should correspond to the actual action group (See rxFetchActions)
                return new FetcherResultItem<>(ActionItemWrapper.createMandated(pair.first.getValue(), pair.second, ActionItemWrapper.Group.NONE), RxFirebaseChildEvent.EventType.CHANGED);
            } else {
                return new FetcherResultItem<>(ActionItemWrapper.createMandated(pair.first.getValue()), pair.first.getEventType() == RxFirebaseChildEvent.EventType.REMOVED ? REMOVED : UPDATED);
            }
        });
    }

    public Flowable<FetcherResultItem<Collection<ActionItemWrapper>>> rxActiveItems(boolean isActive) {

        Flowable<FetcherResultItem<ArrayList<ActionItemWrapper>>> flowable =
        alertGroupFlowable.map(dataSnapshots -> {
            Set<Integer> hazardTypes = new HashSet<>();

            for (DataSnapshot snapshot : dataSnapshots) {
                AlertModel alertModel = AppUtils.getFirebaseModelFromDataSnapshot(snapshot, AlertModel.class);
                hazardTypes.add(alertModel.getHazardScenario());
            }
            return hazardTypes;
        })
        .distinct()
        .flatMap(hazardTypes -> rxFetchGroup().map(actionItemWrapperFetcherResultItem -> {
            ArrayList<ActionItemWrapper> result = new ArrayList<>();
            for (ActionItemWrapper itemWrapper : actionItemWrapperFetcherResultItem) {
                boolean res = false;

                DataSnapshot snapshot = itemWrapper.getActionSnapshot();
                if (itemWrapper.getActionSnapshot() == null) {
                    snapshot = itemWrapper.getTypeSnapshot();
                }

                if (snapshot != null) {
//
                    ActionModel model = AppUtils.getFirebaseModelFromDataSnapshot(snapshot, ActionModel.class);
                    if (model.getAssignHazard() != null) {
                        for (Integer hazardType : model.getAssignHazard()) {
                            if (hazardTypes.contains(hazardType)) {
                                res = true;
                                break;
                            }
                        }
                    } else if (model.getAssignHazard() == null || model.getAssignHazard().size() == 0) {
                        res = true;
                    }
                    if (res && isActive || !res && !isActive) {
                        result.add(itemWrapper);
                    }
                }
            }
            return new FetcherResultItem<>(result, RxFirebaseChildEvent.EventType.CHANGED);
        }));

        Flowable<ClockSettingsFetcher.ClockSettingsResult> clockSettingsResultFlowable = new ClockSettingsFetcher().rxFetch(ClockSettingsFetcher.TYPE_PREPAREDNESS);

        return Flowable.combineLatest(clockSettingsResultFlowable, flowable, (clockSettingsResult, actionItemWrapperFetcherResultItems) -> {
            ArrayList<ActionItemWrapper> result = new ArrayList<>();
            for(ActionItemWrapper itemWrapper : actionItemWrapperFetcherResultItems.getValue()) {
                DataSnapshot snapshot = itemWrapper.getActionSnapshot();
                if (snapshot == null) {
                    snapshot = itemWrapper.getTypeSnapshot();
                }
                itemWrapper.setClockSetting(clockSettingsResult.all().get(snapshot.getRef().getParent().getKey()));
                result.add(itemWrapper);
            }
            return new FetcherResultItem<Collection<ActionItemWrapper>>(result);
        });

    }

    @Override
    public Flowable<FetcherResultItem<ActionItemWrapper>> rxFetch() {
        return Flowable.merge(rxFetchActions(), rxFetchCHSActions(), rxFetchMandatedActions());
    }

    private Flowable<Collection<ActionItemWrapper>> rxFetchGroupActions() {
        return rxFetchGroupActions(Constants.CUSTOM);
    }

    private Flowable<Collection<ActionItemWrapper>> rxFetchGroupActions(int type){
        return networkResultFlowable.flatMap(networkFetcherResult -> {

            List<Flowable<Collection<ActionItemWrapper>>> flowables = new ArrayList<>();
            flowables.add(
                    RxFirebaseDatabase.observeValueEvent(dbActionBaseRef.child(user.countryID).orderByChild("type").equalTo(type))
                            .map(dataSnapshot -> Lists.newArrayList(dataSnapshot.getChildren()))
                            .map(childrenDataSnapshotList -> Collections2.transform(childrenDataSnapshotList, item -> ActionItemWrapper.createAction(item, ActionItemWrapper.Group.COUNTRY)))
            );

            for (String localNetworkId : networkFetcherResult.getLocalNetworks()) {
                flowables.add(RxFirebaseDatabase.observeValueEvent(dbActionBaseRef.child(localNetworkId).orderByChild("type").equalTo(type))
                        .map(dataSnapshot -> Lists.newArrayList(dataSnapshot.getChildren()))
                        .map(childrenDataSnapshotList -> Collections2.transform(childrenDataSnapshotList, item -> ActionItemWrapper.createAction(item, ActionItemWrapper.Group.LOCAL_NETWORK)))
                );
            }
            for (String networkCountryId : networkFetcherResult.getNetworksCountries()) {
                flowables.add(RxFirebaseDatabase.observeValueEvent(dbActionBaseRef.child(networkCountryId).orderByChild("type").equalTo(type))
                        .map(dataSnapshot -> Lists.newArrayList(dataSnapshot.getChildren()))
                        .map(childrenDataSnapshotList -> Collections2.transform(childrenDataSnapshotList, item -> ActionItemWrapper.createAction(item, ActionItemWrapper.Group.LOCAL_NETWORK)))
                );
            }
            return Flowable.combineLatest(flowables, AppUtils::combineDataSnapshotList);
        });
    }

    private Flowable<Collection<ActionItemWrapper>> rxFetchGroupCHS(){
        return Flowable.combineLatest(
            rxFetchGroupActions(Constants.CHS),
            RxFirebaseDatabase.observeValueEvent(dbBaseActionCHSRef.child(user.getSystemAdminID()))
                    .map(dataSnapshot -> Lists.newArrayList(dataSnapshot.getChildren())),
            (actionItemWrappers, chsSnapshots) -> {
                List<ActionItemWrapper> toReturn = new ArrayList<>();
                for(DataSnapshot chsSnapshot : chsSnapshots){
                    Optional<ActionItemWrapper> actionItemWrapperOptional = Iterables.tryFind(actionItemWrappers, input -> input.getActionSnapshot().getKey().equals(chsSnapshot.getKey()));

                    if(actionItemWrapperOptional.isPresent()) {
                        toReturn.add(ActionItemWrapper.createCHS(chsSnapshot, actionItemWrapperOptional.get().getActionSnapshot(), actionItemWrapperOptional.get().getGroup()));
                    }
                    else{
                        toReturn.add(ActionItemWrapper.createCHS(chsSnapshot));
                    }
                }
                return toReturn;
            }
        );
    }

    private Flowable<Collection<ActionItemWrapper>> rxFetchGroupMandated(){
        Flowable<Collection<DataSnapshot>> mandatedFlow = networkResultFlowable.flatMap(networkFetcherResult -> {
            List<String> networkIds = networkFetcherResult.all();

            List<Flowable<Collection<DataSnapshot>>> flowables = new ArrayList<>();
            flowables.add(
                    RxFirebaseDatabase.observeValueEvent(dbBaseActionMandatedRef.child(user.agencyAdminID))
                            .map(dataSnapshot -> Lists.newArrayList(dataSnapshot.getChildren()))
            );

            for (String networkId : networkIds) {
                flowables.add(RxFirebaseDatabase.observeValueEvent(dbBaseActionMandatedRef.child(networkId))
                        .map(dataSnapshot -> Lists.newArrayList(dataSnapshot.getChildren()))
                );
            }
            return Flowable.<Collection<DataSnapshot>, Collection<DataSnapshot>>combineLatest(flowables, AppUtils::combineDataSnapshotList);
        });
        return Flowable.combineLatest(
            rxFetchGroupActions(Constants.MANDATED),
            mandatedFlow,
            (actionSnapshots, mandatedSnapshots) -> {
                List<ActionItemWrapper> toReturn = new ArrayList<>();
                for(DataSnapshot mandatedSnapshot : mandatedSnapshots){
                    Optional<ActionItemWrapper> actionItemWrapperOptional = Iterables.tryFind(actionSnapshots, input -> input.getActionSnapshot().getKey().equals(mandatedSnapshot.getKey()));

                    if(actionItemWrapperOptional.isPresent()) {
                        toReturn.add(ActionItemWrapper.createMandated(mandatedSnapshot, actionItemWrapperOptional.get().getActionSnapshot(), actionItemWrapperOptional.get().getGroup()));
                    }
                    else{
                        toReturn.add(ActionItemWrapper.createMandated(mandatedSnapshot));
                    }
                }
                return toReturn;
            }
        );
    }

    @Override
    public Flowable<Collection<ActionItemWrapper>> rxFetchGroup() {
        List<Flowable<Collection<ActionItemWrapper>>> flowables = new ArrayList<>();
        flowables.add(rxFetchGroupActions());
        flowables.add(rxFetchGroupCHS());
        flowables.add(rxFetchGroupMandated());
        return Flowable.combineLatest(flowables, AppUtils::combineDataSnapshotList);
    }
}
