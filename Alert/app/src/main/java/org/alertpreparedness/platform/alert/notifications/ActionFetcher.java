package org.alertpreparedness.platform.alert.notifications;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.alert.firebase.ActionModel;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.SynchronizedCounter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class ActionFetcher implements SynchronizedCounter.SynchronizedCounterListener {

    private ActionFetcherListener listener;

    @Inject
    public User user;

    @Inject
    @BaseActionRef
    public DatabaseReference baseActionRef;

    private boolean failed = false;

    private List<ActionFetcherResult> actions = new ArrayList<>();
    private SynchronizedCounter actionCounter;

    public ActionFetcher(ActionFetcherListener listener) {
        this.listener = listener;
        DependencyInjector.applicationComponent().inject(this);
    }

    public void fetchAll(){
        actions = new ArrayList<>();

        actionCounter = new SynchronizedCounter(3);
        actionCounter.addListener(this);

        baseActionRef.child(user.getCountryID()).orderByChild("asignee").equalTo(user.getUserID()).addListenerForSingleValueEvent(new ActionListener(user.getCountryID(), actions, actionCounter));
        baseActionRef.child(user.getNetworkCountryID()).orderByChild("asignee").equalTo(user.getUserID()).addListenerForSingleValueEvent(new ActionListener(user.getNetworkCountryID(), actions, actionCounter));
        baseActionRef.child(user.getLocalNetworkID()).orderByChild("asignee").equalTo(user.getUserID()).addListenerForSingleValueEvent(new ActionListener(user.getLocalNetworkID(), actions, actionCounter));
    }

    @Override
    public void counterChanged(SynchronizedCounter synchronizedCounter, int amount) {
        if(synchronizedCounter == actionCounter && amount == 0){
            notifySuccess();
        }
    }

    private void notifySuccess() {
        listener.actionFetchSuccess(actions);
    }

    public interface ActionFetcherListener{
        void actionFetchSuccess(List<ActionFetcherResult> models);
        void actionFetchFail();
    }

    private class ActionListener implements ValueEventListener {
        private final String groupId;
        private final List<ActionFetcherResult> actions;
        private final SynchronizedCounter actionCounter;

        public ActionListener(String groupId, List<ActionFetcherResult> actions, SynchronizedCounter actionCounter) {
            this.groupId = groupId;
            this.actions = actions;
            this.actionCounter = actionCounter;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot != null){
                for(DataSnapshot actionSnap : dataSnapshot.getChildren()){
                    Timber.d(dataSnapshot.toString());
                    Timber.d(dataSnapshot.getRef().toString());
                    actions.add(new ActionFetcherResult(actionSnap.getValue(ActionModel.class), groupId, actionSnap.getKey()));
                }
            }
            actionCounter.decrement();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Timber.d("ERROR");
            notifyFailed();
        }
    }

    private void notifyFailed() {
        if(!failed) {
            failed = true;
            listener.actionFetchFail();
        }
    }

    public class ActionFetcherResult{
        private ActionModel action;
        private String groupId;
        private String actionId;

        public ActionFetcherResult(ActionModel action, String groupId, String actionId) {
            this.action = action;
            this.groupId = groupId;
            this.actionId = actionId;
        }

        public ActionModel getAction() {
            return action;
        }

        public String getActionId() {
            return actionId;
        }

        public String getGroupId() {
            return groupId;
        }
    }
}
