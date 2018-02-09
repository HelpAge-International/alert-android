package org.alertpreparedness.platform.alert.min_preparedness.fragment;

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

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.ActionAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.PreparednessAdapter;
import org.alertpreparedness.platform.alert.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.whalemare.sheetmenu.SheetMenu;

/**
 * Created by faizmohideen on 21/12/2017.
 */

public class ActionExpiredFragment extends BaseExpiredFragment {

    @Nullable
    @BindView(R.id.rvMinAction)
    RecyclerView mActionRV;

    @Nullable
    @BindView(R.id.tvStatus)
    TextView tvActionExpired;

    @Nullable
    @BindView(R.id.imgStatus)
    ImageView imgExpired;

    @Nullable
    @BindView(R.id.tvNoAction)
    TextView txtNoAction;

    protected ActionAdapter mExpiredAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_minimum, container, false);

        ButterKnife.bind(this, v);
        DependencyInjector.applicationComponent().inject(this);

        initViews();

        return v;
    }

    private void initViews() {
        assert imgExpired != null;
        imgExpired.setImageResource(R.drawable.ic_close_round);
        assert tvActionExpired != null;
        tvActionExpired.setText("Expired");
        tvActionExpired.setTextColor(getResources().getColor(R.color.alertRed));

        mExpiredAdapter = new ActionAdapter(getContext(), dbActionBaseRef, this);
        mActionRV.setAdapter(mExpiredAdapter);

        mActionRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mActionRV.setItemAnimator(new DefaultItemAnimator());
        mActionRV.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

//        dbActionBaseRef.addChildEventListener(this);
        ids = new String[]{user.getCountryID(), user.getNetworkID(), user.getLocalNetworkID(), user.getNetworkCountryID()};

        for (String id : ids) {
            if(id != null) {
                dbActionBaseRef.child(id).addChildEventListener(new ExpiredChildListener(id));
            }

        }

        handleMinFab();

    }

    @Override
    protected int getType() {
        return Constants.MPA;
    }

    @Override
    protected PreparednessAdapter getAdapter() {
        return mExpiredAdapter;
    }

    @Override
    protected RecyclerView getListView() {
        return mActionRV;
    }

    @Override
    public void onActionItemSelected(int pos, String key, String userTypeID) {
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

    @Override
    protected TextView getNoActionView() {
        return txtNoAction;
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
}
