package org.alertpreparedness.platform.alert.adv_preparedness.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.autofill.Dataset;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.adv_preparedness.adapter.APActionAdapter;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.CompleteActionActivity;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.PreparednessAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.fragment.BaseExpiredFragment;
import org.alertpreparedness.platform.alert.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    private APActionAdapter mAPAdapter;
    private Boolean isCHS = false;
    private Boolean isCHSAssigned = false;
    private Boolean isMandated = false;
    private Boolean isMandatedAssigned = false;
    private Boolean isInProgress = false;
    private int freqBase = 0;
    private int freqValue = 0;


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

        ids = new String[]{user.getCountryID(), user.getNetworkID(), user.getLocalNetworkID(), user.getNetworkCountryID()};

        for (String id : ids) {
            if(id != null) {
                dbActionBaseRef.child(id).addChildEventListener(new ExpiredChildListener(id));
            }

        }
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
        SheetMenu.with(getContext()).setMenu(R.menu.menu_expired).setClick(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
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
            }
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
}

