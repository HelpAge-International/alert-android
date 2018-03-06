package org.alertpreparedness.platform.alert.action;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.min_preparedness.model.DataModel;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.NetworkFetcher;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import durdinapps.rxfirebase2.RxFirebaseDatabase;

/**
 * Created by Tj on 28/02/2018.
 */

public class ActionFetcher implements ActionProcessorListener {

    private int type;
    private ACTION_STATE state;
    private ActionRetrievalListener listener;
    private List<Integer> alertHazardTypes;
    private List<Integer> networkHazardTypes;

    @Inject
    User user;

    @Inject
    @BaseActionRef
    public DatabaseReference dbActionBaseRef;

    public enum ACTION_STATE {
        IN_PROGRESS,
        UNASSIGNED,
        COMPLETED,
        INACTIVE,
        EXPIRED,
        APA_EXPIRED,
        APA_IN_PROGRESS,
        ARCHIVED,
        APA_UNASSIGNED
    }

    /**
     *
     * @param type MPA or APA
     * @param listener
     */
    public ActionFetcher(int type, ACTION_STATE state, ActionRetrievalListener listener) {
        this.type = type;
        this.state = state;
        this.listener = listener;
        DependencyInjector.applicationComponent().inject(this);
    }

    public ActionFetcher(int type, ACTION_STATE state, ActionRetrievalListener listener, List<Integer> alertHazardTypes, List<Integer> networkHazardTypes) {
        this.type = type;
        this.state = state;
        this.listener = listener;
        this.alertHazardTypes = alertHazardTypes;
        this.networkHazardTypes = networkHazardTypes;
        DependencyInjector.applicationComponent().inject(this);
    }

    public void fetchWithIds(List<String> ids, IdFetcherListener idFetcherListener) {
        ids.add(user.countryID);
        idFetcherListener.onIdResult(ids);
        for (String id : ids) {
            dbActionBaseRef.child(id).addChildEventListener(new ActionListener(id));
        }
    }

    public void fetch(IdFetcherListener idFetcherListener) {
        new NetworkFetcher((n) -> {
            List<String> ids = n.all();
            ids.add(user.countryID);
            idFetcherListener.onIdResult(ids);
            for (String id : ids) {
                dbActionBaseRef.child(id).addChildEventListener(new ActionListener(id));
            }
        }).fetch();


    }

    private ActionProcessor makeProcessor(DataSnapshot snapshot, DataModel model, String actionId, String parentId) {
        switch (state) {
            case IN_PROGRESS:
                return new ActionInProgressProcessor(type, snapshot, model, actionId, parentId, this);
            case EXPIRED:
                return new ActionExpiredProcessor(type, snapshot, model, actionId, parentId, this);
            case APA_EXPIRED:
                return new ActionAPAExpiredProcessor(type, snapshot, model, actionId, parentId, this, alertHazardTypes, networkHazardTypes);
            case APA_IN_PROGRESS:
                return new ActionAPAInProgressProcessor(type, snapshot, model, actionId, parentId, this, alertHazardTypes, networkHazardTypes);
            case ARCHIVED:
                return new ActionArchivedProcessor(type, snapshot, model, actionId, parentId, this);
            case COMPLETED:
                return new ActionCompletedProcessor(type, snapshot, model, actionId, parentId, this);
            case UNASSIGNED:
                return new ActionUnassignedProcessor(type, snapshot, model, actionId, parentId, this);
            case APA_UNASSIGNED:
                return new ActionAPAUnassignedProcessor(type, snapshot, model, actionId, parentId, this, alertHazardTypes, networkHazardTypes);
            default:
                return new ActionInProgressProcessor(type, snapshot, model, actionId, parentId, this);
        }
    }

    protected class ActionListener implements ChildEventListener {
        private String parentId;

        public ActionListener(String id) {
            this.parentId = id;
        }


        protected void process(DataSnapshot dataSnapshot) {


            String actionID = dataSnapshot.getKey();

            DataModel model = dataSnapshot.getValue(DataModel.class);

            if(model != null) {
                boolean isNetwork = !dataSnapshot.getRef().getParent().getKey().equals(user.countryID);
                model.setIsNetworkLevel(isNetwork);
                if (dataSnapshot.child("frequencyBase").getValue() != null) {
                    model.setFrequencyBase(dataSnapshot.child("frequencyBase").getValue().toString());
                }
                if (dataSnapshot.child("frequencyValue").getValue() != null) {
                    model.setFrequencyValue(dataSnapshot.child("frequencyValue").getValue().toString());
                }

                ActionProcessor processor = makeProcessor(dataSnapshot, model, actionID, parentId);

                if (model.getType() != null && model.getType() == 0 && type == Constants.MPA && state != ACTION_STATE.UNASSIGNED) {
                    processor.getCHS();
                }
                else if (model.getType() != null && model.getType() == 1 && state != ACTION_STATE.UNASSIGNED) {
                    processor.getMandated();
                }
                else if (model.getType() != null && model.getType() == 2 && state != ACTION_STATE.UNASSIGNED) {
                    processor.getCustom();
                }
                else if (state == ACTION_STATE.UNASSIGNED){
                    if (dataSnapshot.getValue() == null) {
                        if (type != Constants.APA) {
                            ((ActionUnassignedProcessor) processor).getCHSForNewUser();
                        }
                        ((ActionUnassignedProcessor)processor).getMandatedForNewUser();
                    }
                    if (model.getType() != null && model.getType() == 2) {
                        processor.getCustom();
                    }
                    if(type != Constants.APA) {
                        System.out.println("fetchingCHS");
                        processor.getCHS();
                    }
                    processor.getMandated();
                }
            }
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            process(dataSnapshot);

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            process(dataSnapshot);

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    @Override
    public void onAddAction(DataSnapshot snapshot, Action action) {
        listener.onActionRetrieved(snapshot, action);
    }

    @Override
    public void tryRemoveAction(DataSnapshot snapshot) {
        listener.onActionRemoved(snapshot);
    }

    public interface ActionRetrievalListener {
        void onActionRetrieved(DataSnapshot snapshot, Action action);
        void onActionRemoved(DataSnapshot snapshot);
    }

}
