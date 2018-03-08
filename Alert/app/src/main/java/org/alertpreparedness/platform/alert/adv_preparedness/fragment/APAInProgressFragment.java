package org.alertpreparedness.platform.alert.adv_preparedness.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.ActionFetcher;
import org.alertpreparedness.platform.alert.adv_preparedness.activity.EditAPAActivity;
import org.alertpreparedness.platform.alert.adv_preparedness.adapter.APActionAdapter;
import org.alertpreparedness.platform.alert.adv_preparedness.model.UserModel;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseAlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.NetworkRef;
import org.alertpreparedness.platform.alert.firebase.AlertModel;
import org.alertpreparedness.platform.alert.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.CompleteActionActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.ViewAttachmentsActivity;
import org.alertpreparedness.platform.alert.min_preparedness.fragment.BaseAPAFragment;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.NetworkFetcher;
import org.alertpreparedness.platform.alert.utils.PermissionsHelper;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.whalemare.sheetmenu.SheetMenu;

/**
 * Created by faizmohideen on 05/01/2018.
 */

public class APAInProgressFragment extends BaseAPAFragment implements APActionAdapter.APAAdapterListener, UsersListDialogFragment.ItemSelectedListener, NetworkFetcher.NetworkFetcherListener, ActionFetcher.ActionRetrievalListener {

    private ArrayList<Integer> alertHazardTypes = new ArrayList<>();
    private String actionID;
    private List<String> networkIds;
    private ArrayList<Integer> networkAlertHazardTypes = new ArrayList<>();

    public APAInProgressFragment() {
        // Required empty public constructor
    }

    @BindView(R.id.rvAdvAction)
    RecyclerView mAdvActionRV;

    @BindView(R.id.tvAPANoAction)
    TextView txtNoAction;

    @Inject
    @NetworkRef
    DatabaseReference dbNetworkRef;

    @Inject
    @NetworkRef
    DatabaseReference networkRef;

    @Inject
    @AgencyRef
    DatabaseReference agencyRef;

    @Inject
    @AlertRef
    DatabaseReference alertRef;

    @Inject
    @BaseAlertRef
    DatabaseReference baseAlertRef;

    @Inject
    @ActionRef
    DatabaseReference dbActionRef;

    @Inject
    User user;

    @Inject
    PermissionsHelper permissions;

    private APActionAdapter mAPAdapter;
    private AlertListener alertListener = new AlertListener();
    private UsersListDialogFragment dialog = new UsersListDialogFragment();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_advanced, container, false);

        ButterKnife.bind(this, v);
        DependencyInjector.applicationComponent().inject(this);

        initViews();

        dialog.setListener(this);

        return v;
    }

    private void initViews() {
        mAPAdapter = new APActionAdapter(getContext(), this);
        assert mAdvActionRV != null;
        mAdvActionRV.setAdapter(mAPAdapter);
        mAdvActionRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdvActionRV.setItemAnimator(new DefaultItemAnimator());
        mAdvActionRV.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        new NetworkFetcher(this).fetch();

        handleAdvFab();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActionItemSelected(int pos, String key, String parentId) {
        this.actionID = key;
        SheetMenu.with(getContext()).setMenu(R.menu.menu_in_progress).setClick(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.edit:
                    if(permissions.checkEditAPA(mAPAdapter.getItem(pos), getActivity())) {
                        Intent i = new Intent(getContext(), EditAPAActivity.class);
                        i.putExtra(EditAPAActivity.APA_ID, key);
                        startActivity(i);
                    }
                    break;
                case R.id.complete_action:
                    if(permissions.checkCompleteAPAAction(mAPAdapter.getItem(pos), getActivity())) {
                        Intent intent = new Intent(getActivity(), CompleteActionActivity.class);
                        intent.putExtra(CompleteActionActivity.REQUIRE_DOC, mAPAdapter.getItem(pos).getRequireDoc());
                        intent.putExtra(CompleteActionActivity.ACTION_KEY, key);
                        intent.putExtra(CompleteActionActivity.PARENT_KEY, parentId);
                        startActivity(intent);
                    }
                    break;
                case R.id.reassign_action:
                    if(permissions.checkAssignAPA(mAPAdapter.getItem(pos), getActivity())) {
                        dialog.show(getActivity().getFragmentManager(), "users_list");
                    }
                    break;
                case R.id.action_notes:
                    Intent intent3 = new Intent(getActivity(), AddNotesActivity.class);
                    intent3.putExtra(AddNotesActivity.PARENT_ACTION_ID, mAPAdapter.getItem(pos).getId());
                    intent3.putExtra(AddNotesActivity.ACTION_ID, key);
                    startActivity(intent3);
                    break;
                case R.id.attachments:
                    Intent intent2 = new Intent(getActivity(), ViewAttachmentsActivity.class);
                    intent2.putExtra(ViewAttachmentsActivity.PARENT_ACTION_ID, mAPAdapter.getItem(pos).getId());
                    intent2.putExtra(ViewAttachmentsActivity.ACTION_ID, key);
                    startActivity(intent2);
            }
            return false;
        }).show();
    }

    @Override
    public void onAdapterItemRemoved(String key) {
        if (mAPAdapter.getItemCount() == 0) {
            txtNoAction.setVisibility(View.VISIBLE);
        }
    }

    //region UsersListDialogFragment.ActionAdapterListener
    @Override
    public void onItemSelected(UserModel model) {
        long millis = System.currentTimeMillis();
        dbActionRef.child(actionID).child("asignee").setValue(model.getUserID());
        dbActionRef.child(actionID).child("updatedAt").setValue(millis);
        mAPAdapter.notifyDataSetChanged();
    }
    //endregion

    @Override
    public void onNetworkFetcherResult(NetworkFetcher.NetworkFetcherResult networkFetcherResult) {
        this.networkIds = networkFetcherResult.all();
        alertRef.addValueEventListener(alertListener);
        for (String id : networkFetcherResult.all()) {
            baseAlertRef.child(id).addValueEventListener(alertListener);
        }
    }

    @Override
    public void onActionRetrieved(DataSnapshot snapshot, Action action) {
        if(permissions.checkCanViewAPA(action)) {
            txtNoAction.setVisibility(View.GONE);
            mAPAdapter.addItems(snapshot.getKey(), action);
        }
    }

    @Override
    public void onActionRemoved(DataSnapshot snapshot) {
        mAPAdapter.removeItem(snapshot.getKey());
    }

    @Override
    protected RecyclerView getListView() {
        return mAdvActionRV;
    }

    private class AlertListener implements ValueEventListener{

        private void process(DataSnapshot dataSnapshot) {

            System.out.println("AlertListenerdataSnapshot = " + dataSnapshot.getRef());

            final GsonBuilder gsonBuilder = new GsonBuilder();
            final Gson gson = gsonBuilder.create();

            JsonReader reader = new JsonReader(new StringReader(gson.toJson(dataSnapshot.getValue()).trim()));
            reader.setLenient(true);
            AlertModel model = gson.fromJson(reader, AlertModel.class);

            assert model != null;
            model.setId(dataSnapshot.getKey());
            model.setParentKey(dataSnapshot.getRef().getParent().getKey());

            if (model.getAlertLevel() == Constants.TRIGGER_RED && model.getHazardScenario() != null) {
                update(!(dataSnapshot.getRef().getParent().getKey().equals(user.countryID)), model);
            }

        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for(DataSnapshot child : dataSnapshot.getChildren()) {
                process(child);
            }
            try {
                dbActionRef.removeEventListener(this);
            }
            catch (Exception e) {}


//            System.out.println("alertHazardTypes = " + alertHazardTypes);
            new ActionFetcher(Constants.APA, ActionFetcher.ACTION_STATE.APA_IN_PROGRESS, APAInProgressFragment.this, alertHazardTypes, networkAlertHazardTypes).fetchWithIds(networkIds, (ids -> {
            }));

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    private void update(boolean isNetwork, AlertModel model) {
        if(!isNetwork) {
            alertHazardTypes.add(model.getHazardScenario());
        }
        else {
            networkAlertHazardTypes.add(model.getHazardScenario());
        }
    }

}
