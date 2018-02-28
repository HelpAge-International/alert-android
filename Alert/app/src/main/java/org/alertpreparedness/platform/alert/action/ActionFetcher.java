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
import org.alertpreparedness.platform.alert.utils.NetworkFetcher;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Tj on 28/02/2018.
 */

public class ActionFetcher implements ActionProcessorListener {

    private int type;
    private ACTION_STATE state;
    private ActionRetrievalListener listener;
    private List<Integer> alertHazardTypes;

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
        ARCHIVED
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

    public ActionFetcher(int type, ACTION_STATE state, ActionRetrievalListener listener, List<Integer> alertHazardTypes) {
        this.type = type;
        this.state = state;
        this.listener = listener;
        this.alertHazardTypes = alertHazardTypes;
        DependencyInjector.applicationComponent().inject(this);
    }

    public void fetchWithIds(List<String> ids, ActionFetcher.IdFetcherListener networkFetcherListener) {
        networkFetcherListener.onIdResult(ids);
        for (String id : ids) {
            dbActionBaseRef.child(id).addChildEventListener(new ActionListener(id));
        }
    }

    public void fetch(ActionFetcher.IdFetcherListener networkFetcherListener) {
        new NetworkFetcher((n) -> {
            List<String> ids = n.all();
            ids.add(user.countryID);
            networkFetcherListener.onIdResult(ids);
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
                return new ActionAPAExpiredProcessor(type, snapshot, model, actionId, parentId, this, alertHazardTypes);
            case APA_IN_PROGRESS:
                return new ActionAPAInProgressProcessor(type, snapshot, model, actionId, parentId, this, alertHazardTypes);
            case ARCHIVED:
                return new ActionArchivedProcessor(type, snapshot, model, actionId, parentId, this);
            case COMPLETED:
                return new ActionCompletedProcessor(type, snapshot, model, actionId, parentId, this);
            case UNASSIGNED:
                return new ActionUnassignedProcessor(type, snapshot, model, actionId, parentId, this);
            default:
                return new ActionInProgressProcessor(type, snapshot, model, actionId, parentId, this);
        }
    }

    public interface IdFetcherListener {
        void onIdResult(List<String> ids);
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
                if (dataSnapshot.child("frequencyBase").getValue() != null) {
                    model.setFrequencyBase(dataSnapshot.child("frequencyBase").getValue().toString());
                }
                if (dataSnapshot.child("frequencyValue").getValue() != null) {
                    model.setFrequencyValue(dataSnapshot.child("frequencyValue").getValue().toString());
                }

                ActionProcessor processor = makeProcessor(dataSnapshot, model, actionID, parentId);

                if (model.getType() != null && model.getType() == 0) {
                    processor.getCHS();
                }
                else if (model.getType() != null && model.getType() == 1) {
                    processor.getMandated();
                }
                else if (model.getType() != null && model.getType() == 2) {
                    processor.getCustom();
                }
                else if (state == ACTION_STATE.UNASSIGNED){
                    if (dataSnapshot.getValue() == null) {
                        ((ActionUnassignedProcessor)processor).getCHSForNewUser();
                        ((ActionUnassignedProcessor)processor).getMandatedForNewUser();
                    }
                    if (model.getType() != null && model.getType() == 2) {
                        processor.getCustom();
                    }
                    processor.getCHS();
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
    public void onAddAction(String key, Action action) {
        listener.onActionRetrieved(key, action);
    }

    @Override
    public void tryRemoveAction(String key) {
        listener.onActionRemoved(key);
    }

    public interface ActionRetrievalListener {
        void onActionRetrieved(String key, Action action);
//        void onActionChanged(Action action);
        void onActionRemoved(String key);
    }

}
