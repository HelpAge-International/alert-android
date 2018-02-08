package org.alertpreparedness.platform.alert.adv_preparedness.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
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
import org.alertpreparedness.platform.alert.adv_preparedness.adapter.APActionAdapter;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseAlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.NetworkRef;
import org.alertpreparedness.platform.alert.firebase.AlertModel;
import org.alertpreparedness.platform.alert.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.PreparednessAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.fragment.BaseExpiredFragment;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.min_preparedness.model.DataModel;
import org.alertpreparedness.platform.alert.utils.Constants;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.whalemare.sheetmenu.SheetMenu;

/**
 * Created by faizmohideen on 06/01/2018.
 */

public class APAExpiredFragment extends BaseExpiredFragment implements APActionAdapter.ItemSelectedListener {

    public APAExpiredFragment() {
        // Required empty public constructor
    }

    @Nullable
    @BindView(R.id.rvAdvAction)
    RecyclerView mAdvActionRV;

    @Nullable
    @BindView(R.id.imgStatus)
    ImageView imgActionExpired;

    @Nullable
    @BindView(R.id.tvStatus)
    TextView tvActionExpired;

    @Nullable
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

    private APActionAdapter mAPAdapter;
    private Boolean isCHS = false;
    private Boolean isCHSAssigned = false;
    private Boolean isMandated = false;
    private Boolean isMandatedAssigned = false;
    private Boolean isInProgress = false;
    private int freqBase = 0;
    private int freqValue = 0;

    private AgencyListener agencyListener = new AgencyListener();
    private AlertListener alertListener = new AlertListener();
    private NetworkListener networkListener = new NetworkListener();
    private ArrayList<Integer> alertHazardTypes = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_advanced, container, false);

        ButterKnife.bind(this, v);
        DependencyInjector.applicationComponent().inject(this);

        initViews();

        return v;
    }

    private void initViews() {
        assert imgActionExpired != null;
        imgActionExpired.setImageResource(R.drawable.ic_close_round);
        assert tvActionExpired != null;
        tvActionExpired.setText("Expired");
        tvActionExpired.setTextColor(getResources().getColor(R.color.alertRed));
        mAPAdapter = getAPAdapter();
        assert mAdvActionRV != null;
        mAdvActionRV.setAdapter(mAPAdapter);

        mAdvActionRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdvActionRV.setItemAnimator(new DefaultItemAnimator());
        mAdvActionRV.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        networkRef.addValueEventListener(networkListener);
    }

    protected APActionAdapter getAPAdapter() {
        return new APActionAdapter(getContext(), dbActionRef, this);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActionItemSelected(int pos, String key) {
        SheetMenu.with(getContext()).setMenu(R.menu.menu_expired).setClick(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.update_date:
                    showDatePicker(key);
                    break;
                case R.id.reassign_action:
                    Snackbar.make(getActivity().findViewById(R.id.cl_in_progress), "Reassigned Clicked", Snackbar.LENGTH_LONG).show();
                    break;
                case R.id.action_notes:
                    Intent intent = new Intent(getActivity(), AddNotesActivity.class);
                    intent.putExtra("ACTION_KEY", key);
                    startActivity(intent);
                    break;
                case R.id.attachments:
                    Snackbar.make(getActivity().findViewById(R.id.cl_in_progress), "Attached Clicked", Snackbar.LENGTH_LONG).show();
                    break;
            }
            return false;
        }).show();
    }

    private void showDatePicker(String key) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                String givenDateString = i2 + " " + i1 + " " + i + " 23:59:00";//due the end of the day.
                SimpleDateFormat sdf = new SimpleDateFormat("dd mm yyyy HH:mm:ss", Locale.getDefault());
                try {
                    Date mDate = sdf.parse(givenDateString);
                    long timeInMilliseconds = mDate.getTime();
                    long millis = System.currentTimeMillis();

                    dbActionRef.child(key).child("dueDate").setValue(timeInMilliseconds);//save due date in milliSec.
                    dbActionRef.child(key).child("updatedAt").setValue(millis);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, year, month, day);
        pickerDialog.show();
    }

    @Override
    protected int getType() {
        return Constants.APA;
    }

    @Override
    protected PreparednessAdapter getAdapter() {
        return mAPAdapter;
    }

    @Override
    protected TextView getNoActionView() {
        return txtNoAction;
    }

    private class NetworkListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            onNetworkRetrieved(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    private void onNetworkRetrieved(DataSnapshot snapshot) {
        agencyRef.addListenerForSingleValueEvent(agencyListener);
        alertRef.addValueEventListener(alertListener);
    }

    private class AgencyListener implements ValueEventListener {
        @SuppressWarnings("unchecked")
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            HashMap<String, Boolean> networks = (HashMap<String, Boolean>) dataSnapshot.child("networks").getValue();

            if (networks != null) {
                for (String id : networks.keySet()) {
                    DatabaseReference ref = baseAlertRef.child(id);
                    ref.addValueEventListener(alertListener);
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    private class AlertListener implements ValueEventListener{

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
                update(model);
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
            ids = new String[]{user.getCountryID(), user.getNetworkID(), user.getLocalNetworkID(), user.getNetworkCountryID()};

            for (String id : ids) {
                if(id != null) {
                    dbActionBaseRef.child(id).addChildEventListener(new ExpiredChildListener(id));
                }

            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    @Override
    protected void addObjects(String name, String department, Long createdAt, Long level,
                            DataModel model, DataSnapshot getChild, String id, String actionIDs, Boolean isCHS, Boolean isCHSAssigned, Boolean isMandated, Boolean isMandatedAssigned) {

        if (user.getUserID().equals(model.getAsignee()) //MPA CUSTOM assigned and EXPIRED for logged in user.
                && model.getLevel() != null
                && model.getLevel() == getType()
                && model.getDueDate() != null
                && model.getTask() != null
                || (user.getUserID().equals(model.getAsignee()) //MPA CHS assigned and EXPIRED for logged in user.
                && isCHSAssigned && isCHS
                && model.getLevel() != null
                && model.getLevel() == getType()
                && model.getDueDate() != null
                && model.getTask() != null)
                || (user.getUserID().equals(model.getAsignee()) //MPA Mandated assigned and EXPIRED for logged in user.
                && isMandatedAssigned && isMandated
                && model.getLevel() != null
                && model.getLevel() == getType()
                && model.getDueDate() != null
                && model.getTask() != null)) {

            if(model.getAssignHazard() != null
                    && alertHazardTypes.indexOf(model.getAssignHazard().get(0)) != -1) {

                getNoActionView().setVisibility(View.GONE);
                System.out.println("ID = " + id + " actionIDs = " + actionIDs);
                getAdapter().addItems(getChild.getKey(), new Action(
                        id,
                        name,
                        department,
                        model.getAsignee(),
                        model.getCreatedByAgencyId(),
                        model.getCreatedByCountryId(),
                        model.getNetworkId(),
                        model.getIsArchived(),
                        model.getIsComplete(),
                        createdAt,
                        model.getUpdatedAt(),
                        model.getType(),
                        model.getDueDate(),
                        model.getBudget(),
                        level,
                        model.getFrequencyBase(),
                        freqValue,
                        user,
                        dbAgencyRef.getRef(),
                        dbUserPublicRef.getRef(),
                        dbNetworkRef)

                );
            }
            else {
                getAdapter().removeItem(getChild.getKey());
            }
        }
        else {
            getAdapter().removeItem(getChild.getKey());
        }
    }

    private void update(AlertModel model) {
        alertHazardTypes.add(model.getHazardScenario());
    }
}

