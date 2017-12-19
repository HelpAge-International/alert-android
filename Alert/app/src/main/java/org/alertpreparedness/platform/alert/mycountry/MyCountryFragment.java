package org.alertpreparedness.platform.alert.mycountry;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.MainDrawer;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.ResponsePlansRef;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.responseplan.ResponsePlanObj;
import org.alertpreparedness.platform.alert.responseplan.ResponsePlansAdapter;
import org.alertpreparedness.platform.alert.risk_monitoring.dialog.OnCountrySelectedListener;
import org.alertpreparedness.platform.alert.risk_monitoring.dialog.OnLevel1SelectedListener;
import org.alertpreparedness.platform.alert.risk_monitoring.dialog.OnLevel2SelectedListener;
import org.alertpreparedness.platform.alert.risk_monitoring.dialog.SelectCountryDialog;
import org.alertpreparedness.platform.alert.risk_monitoring.dialog.SelectLevel1Dialog;
import org.alertpreparedness.platform.alert.risk_monitoring.dialog.SelectLevel2Dialog;
import org.alertpreparedness.platform.alert.risk_monitoring.model.CountryJsonData;
import org.alertpreparedness.platform.alert.risk_monitoring.model.LevelOneValuesItem;
import org.alertpreparedness.platform.alert.risk_monitoring.model.LevelTwoValuesItem;
import org.alertpreparedness.platform.alert.risk_monitoring.view_model.SelectAreaViewModel;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Tj on 19/12/2017.
 */

public class MyCountryFragment extends Fragment implements OnCountrySelectedListener, OnLevel1SelectedListener, OnLevel2SelectedListener {

    @Inject
    User user;

    @BindView(R.id.tvSelectLevel1)
    TextView tvSelectLevel1;

    @BindView(R.id.tvSelectLevel2)
    TextView tvSelectLevel2;

    @BindView(R.id.tvSelectCountry)
    TextView tvSelectCountry;

    private SelectAreaViewModel mViewModel;
    private ArrayList<CountryJsonData> mCountryDataList;
    private SelectCountryDialog mSelectCountryDialog;
    private SelectLevel1Dialog mSelectLevel1Dialog;
    private SelectLevel2Dialog mSelectLevel2Dialog;

    private MutableLiveData<Integer> mCountrySelected = new MutableLiveData<>();
    private MutableLiveData<LevelTwoValuesItem> mLevel2Selected = new MutableLiveData<>();
    private MutableLiveData<LevelOneValuesItem> mLevel1Selected = new MutableLiveData<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_country, container, false);

        initViews(v);

        ((MainDrawer)getActivity()).toggleActionBarWithTitle(MainDrawer.ActionBarState.NORMAL, R.string.my_country);

        return v;
    }

    private void initViews(View v) {

        mSelectCountryDialog = new SelectCountryDialog();
        mSelectLevel1Dialog = new SelectLevel1Dialog();
        mSelectLevel2Dialog = new SelectLevel2Dialog();

        mSelectCountryDialog.setOnCountrySelectedListener(this);
        mSelectLevel1Dialog.setOnLevel1SelectedListener(this);
        mSelectLevel2Dialog.setOnLevel2SelectedListener(this);

        mViewModel = ViewModelProviders.of(this).get(SelectAreaViewModel.class);

        mViewModel.getCountryJsonDataLive().observe(this, countryJsonData -> {

            if(countryJsonData != null) {
                mCountryDataList = new ArrayList<>(countryJsonData);

                if (mCountryDataList.size() > 240) {
                    ButterKnife.bind(MyCountryFragment.this, v);
                }
            }

        });

        startObservers();
    }

    private void startObservers() {
        mCountrySelected.observe(this, integer -> {
            if(integer != null) {
                tvSelectCountry.setText(Constants.COUNTRIES[integer]);
            }
            else {
                tvSelectCountry.setText("");
            }
            tvSelectLevel1.setText("");
            tvSelectLevel2.setText("");
        });

        mLevel1Selected.observe(this, obj -> {
            if(obj != null) {
                tvSelectLevel1.setText(obj.getValue());
            }
            else {
                tvSelectLevel1.setText("");
            }
            tvSelectLevel2.setText("");
        });

        mLevel2Selected.observe(this, obj -> {
            if(obj != null) {
                tvSelectLevel2.setText(obj.getValue());
            }
            else {
                tvSelectLevel2.setText("");
            }
        });

    }

    @OnClick(R.id.llAreaCountry)
    public void onCountryClicked(View v) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("select_country_args", mCountryDataList);
        mSelectCountryDialog.setArguments(bundle);
        mSelectCountryDialog.show(getFragmentManager(), "select_country_args");
    }

    @OnClick(R.id.llAreaLevel1)
    public void onLevel1Clicked(View v) {
        if(mCountrySelected.getValue() != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("select_country_args", mCountryDataList);
            bundle.putInt("select_level1_args", mCountrySelected.getValue());
            mSelectLevel1Dialog.setArguments(bundle);
            mSelectLevel1Dialog.show(getFragmentManager(), "select_level1_args");
        }
        else {
            //TODO show error
        }
    }

    @OnClick(R.id.llAreaLevel2)
    public void onLevel2Clicked(View v) {
        if(mCountrySelected.getValue() == null) {

        }
        else if(mLevel1Selected.getValue() == null) {

        }
        else {
            Bundle bundle = new Bundle();
            bundle.putSerializable("select_country_args", mCountryDataList);
            bundle.putInt("select_level1_args", mCountrySelected.getValue());
            bundle.putInt("select_level2_args", mLevel1Selected.getValue().getId());
            mSelectLevel2Dialog.setArguments(bundle);
            mSelectLevel2Dialog.show(getFragmentManager(), "select_level2_args");
        }
    }

    @OnClick(R.id.btnSearch)
    public void onSearchClicked(View v) {

    }

    @Override
    public void selectedCountry(@NotNull CountryJsonData countryJsonData) {
        if (mCountrySelected.getValue() == null || countryJsonData.getCountryId() != mCountrySelected.getValue()) {
            mLevel1Selected.setValue(null);
            mLevel2Selected.setValue(null);
            mCountrySelected.setValue(countryJsonData.getCountryId());
        }
    }

    @Override
    public void selectedLevel1(@Nullable LevelOneValuesItem level1Value) {
        if(level1Value != null && (mLevel1Selected.getValue() == null || (level1Value.getId() != mLevel1Selected.getValue().getId()))) {
            mLevel1Selected.setValue(level1Value);
            mLevel2Selected.setValue(null);
        }
    }

    @Override
    public void selectedLevel2(@Nullable LevelTwoValuesItem level2Value) {
        if(level2Value != null && (mLevel2Selected.getValue() == null || (level2Value.getId() != mLevel2Selected.getValue().getId()))) {
            mLevel2Selected.setValue(level2Value);
        }
    }

}
