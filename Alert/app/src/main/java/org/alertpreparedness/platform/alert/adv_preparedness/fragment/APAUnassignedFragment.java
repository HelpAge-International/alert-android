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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.action.ActionFetcher;
import org.alertpreparedness.platform.alert.adv_preparedness.activity.EditAPAActivity;
import org.alertpreparedness.platform.alert.adv_preparedness.adapter.APActionAdapter;
import org.alertpreparedness.platform.alert.adv_preparedness.model.UserModel;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseAlertRef;
import org.alertpreparedness.platform.alert.firebase.AlertModel;
import org.alertpreparedness.platform.alert.min_preparedness.fragment.BaseAPAFragment;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.NetworkFetcher;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.whalemare.sheetmenu.SheetMenu;

/**
 * Created by faizmohideen on 06/01/2018.
 */

public class APAUnassignedFragment extends BaseAPAFragment implements APActionAdapter.APAAdapterListener, UsersListDialogFragment.ItemSelectedListener, ActionFetcher.ActionRetrievalListener, NetworkFetcher.NetworkFetcherListener {

    private ArrayList<Integer> networkAlertHazardTypes = new ArrayList<>();
    private ArrayList<Integer> alertHazardTypes = new ArrayList<>();
    private List<String> networkIds = new ArrayList<>();
    private AlertListener alertListener = new AlertListener();

    public APAUnassignedFragment() {
        // Required empty public constructor
    }

    @Inject
    @AlertRef
    DatabaseReference alertRef;

    @Inject
    @BaseAlertRef
    DatabaseReference baseAlertRef;


    @BindView(R.id.rvAdvAction)
    RecyclerView mAdvActionRV;

    @BindView(R.id.imgStatus)
    ImageView imgActionUnassigned;

    @BindView(R.id.tvStatus)
    TextView tvActionUnassigned;

    @BindView(R.id.tvAPANoAction)
    TextView txtNoAction;

    @Inject
    @ActionRef
    DatabaseReference dbActionRef;

    @Inject
    User user;

    private APActionAdapter mAPAdapter;
    private UsersListDialogFragment dialog = new UsersListDialogFragment();
    private String actionID;
    private int freqValue = 0;

    @Nullable
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
        assert imgActionUnassigned != null;
        imgActionUnassigned.setImageResource(R.drawable.ic_close_round);
        assert tvActionUnassigned != null;
        tvActionUnassigned.setText(R.string.unassigned_title);
        tvActionUnassigned.setTextColor(getResources().getColor(R.color.alertRed));
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
    public void onNetworkFetcherResult(NetworkFetcher.NetworkFetcherResult networkFetcherResult) {
        List<String> ids = networkFetcherResult.all();
        this.networkIds = ids;
        ids.add(user.countryID);
        alertRef.addValueEventListener(alertListener);
        for (String id : networkIds) {
            baseAlertRef.child(id).addValueEventListener(alertListener);
        }

    }

    private class AlertListener implements ValueEventListener {

        private void process(DataSnapshot dataSnapshot) {

            final GsonBuilder gsonBuilder = new GsonBuilder();
            final Gson gson = gsonBuilder.create();

            JsonReader reader = new JsonReader(new StringReader(gson.toJson(dataSnapshot.getValue()).trim()));
            reader.setLenient(true);
            AlertModel model = gson.fromJson(reader, AlertModel.class);

            assert model != null;
            model.setKey(dataSnapshot.getKey());
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

            new ActionFetcher(Constants.APA, ActionFetcher.ACTION_STATE.APA_UNASSIGNED, APAUnassignedFragment.this, alertHazardTypes, networkAlertHazardTypes).fetchWithIds(networkIds, (ids -> {

            }));
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }

    }

    @Override
    public void onActionItemSelected(int pos, String key, String parentId) {
        this.actionID = key;
        SheetMenu.with(getContext()).setMenu(R.menu.menu_unassigned_apa).setClick(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.assign_action:
                    dialog.show(getActivity().getFragmentManager(), "users_list");
                    break;
                case R.id.edit_action:
                    Intent i = new Intent(getContext(), EditAPAActivity.class);
                    i.putExtra(EditAPAActivity.APA_ID, key);
                    startActivity(i);
                    break;
            }
            return false;
        }).show();
    }

    @Override
    public void onAdapterItemRemoved(String key) {

    }

    @Override
    public void onItemSelected(UserModel userModel) {
        long millis = System.currentTimeMillis();
        dbActionRef.child(actionID).child("asignee").setValue(userModel.getUserID());
        dbActionRef.child(actionID).child("updatedAt").setValue(millis);
        mAPAdapter.removeItem(actionID);
        mAPAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActionRetrieved(DataSnapshot snapshot, Action action) {
        txtNoAction.setVisibility(View.GONE);
        mAPAdapter.addItems(snapshot.getKey(), action);
    }

    @Override
    public void onActionRemoved(DataSnapshot snapshot) {
        mAPAdapter.removeItem(snapshot.getKey());
    }

    @Override
    protected RecyclerView getListView() {
        return mAdvActionRV;
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