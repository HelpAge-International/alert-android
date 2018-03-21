package org.alertpreparedness.platform.alert.mycountry;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.ExtensionHelperKt;
import org.alertpreparedness.platform.alert.MainDrawer;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.risk_monitoring.dialog.OnCountrySelectedListener;
import org.alertpreparedness.platform.alert.risk_monitoring.dialog.OnLevel1SelectedListener;
import org.alertpreparedness.platform.alert.risk_monitoring.dialog.OnLevel2SelectedListener;
import org.alertpreparedness.platform.alert.risk_monitoring.dialog.SelectCountryDialog;
import org.alertpreparedness.platform.alert.risk_monitoring.dialog.SelectLevel1Dialog;
import org.alertpreparedness.platform.alert.risk_monitoring.dialog.SelectLevel2Dialog;
import org.alertpreparedness.platform.alert.risk_monitoring.model.CountryJsonData;
import org.alertpreparedness.platform.alert.risk_monitoring.model.LevelOneValuesItem;
import org.alertpreparedness.platform.alert.risk_monitoring.model.LevelTwoValuesItem;
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelIndicatorLocation;
import org.alertpreparedness.platform.alert.risk_monitoring.view_model.SelectAreaViewModel;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.SnackbarHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Tj on 19/12/2017.
 */

public class MyCountryFragment extends Fragment implements OnCountrySelectedListener, OnLevel1SelectedListener, OnLevel2SelectedListener, ValueEventListener {

    @Inject
    User user;

    @BindView(R.id.tvSelectLevel1)
    TextView tvSelectLevel1;

    @BindView(R.id.tvSelectLevel2)
    TextView tvSelectLevel2;

    @BindView(R.id.tvSelectCountry)
    TextView tvSelectCountry;

    @BindView(R.id.tvSelectLevel2Heading)
    TextView tvSelectLevel2Heading;

    @BindView(R.id.tvSelectLevel1Heading)
    TextView tvSelectLevel1Heading;

    @BindView(R.id.llAreaLevel1)
    LinearLayout level1Con;

    @BindView(R.id.llAreaLevel2)
    LinearLayout level2Con;

    @BindView(R.id.llAreaCountry)
    LinearLayout countryCon;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindColor(R.color.page_indicator_color)
    int activeColor;

    @BindColor(R.color.divider_color)
    int inactiveColor;

    @Inject @AgencyRef
    DatabaseReference agency;

    private ArrayList<CountryJsonData> mCountryDataList;
    private SelectCountryDialog mSelectCountryDialog;
    private SelectLevel1Dialog mSelectLevel1Dialog;
    private SelectLevel2Dialog mSelectLevel2Dialog;

    private MutableLiveData<Integer> mCountrySelected = new MutableLiveData<>();
    private MutableLiveData<LevelTwoValuesItem> mLevel2Selected = new MutableLiveData<>();
    private MutableLiveData<LevelOneValuesItem> mLevel1Selected = new MutableLiveData<>();
    private boolean mHasLevel2;
    private boolean mHasLevel1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_country, container, false);

        DependencyInjector.userScopeComponent().inject(this);
        ButterKnife.bind(MyCountryFragment.this, v);

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

        countryCon.setClickable(false);
        level1Con.setClickable(false);
        level2Con.setClickable(false);

        SelectAreaViewModel mViewModel = ViewModelProviders.of(this).get(SelectAreaViewModel.class);

        mViewModel.getCountryJsonDataLive().observe(this, countryJsonData -> {

            if(countryJsonData != null) {
                mCountryDataList = new ArrayList<>(countryJsonData);

                if (mCountryDataList.size() == 248) {

                    agency.child("country").addListenerForSingleValueEvent(this);
                }
            }

        });

        startObservers();

        tvSelectCountry.setText(user.getCountryName());

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
            if (mHasLevel1) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("select_country_args", mCountryDataList);
                bundle.putInt("select_level1_args", mCountrySelected.getValue());
                if(!mSelectLevel1Dialog.isAdded()) {
                    mSelectLevel1Dialog.setArguments(bundle);
                    mSelectLevel1Dialog.show(getFragmentManager(), "select_level1_args");
                }
            }
        }
        else {
            SnackbarHelper.show(getActivity(), getString(R.string.select_country_error));
        }
    }

    @OnClick(R.id.llAreaLevel2)
    public void onLevel2Clicked(View v) {
        if(mCountrySelected.getValue() == null) {
            SnackbarHelper.show(getActivity(), getString(R.string.select_country_error));
        }
        else if(mLevel1Selected.getValue() == null) {
            SnackbarHelper.show(getActivity(), getString(R.string.select_level_1_error));
        }
        else if(mLevel1Selected.getValue() != null && mHasLevel2) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("select_country_args", mCountryDataList);
            bundle.putInt("select_level1_args", mCountrySelected.getValue());
            bundle.putInt("select_level2_args", mLevel1Selected.getValue().getId());
            if(!mSelectLevel2Dialog.isAdded()) {
                mSelectLevel2Dialog.setArguments(bundle);
                mSelectLevel2Dialog.show(getFragmentManager(), "select_level2_args");
            }
        }
    }

    @OnClick(R.id.btnSearch)
    public void onSearchClicked(View v) {
        if(mCountrySelected.getValue() == null) {
            SnackbarHelper.show(getActivity(), getString(R.string.select_country_error));
        }
//        else if(mLevel1Selected.getValue() == null && mHasLevel1) {
//            SnackbarHelper.show(getActivity(), getString(R.string.select_level_1_error));
//        }
//        else if(mLevel2Selected.getValue() == null && mHasLevel2) {
//            SnackbarHelper.show(getActivity(), getString(R.string.select_level_2_error));
//        }
        else {
            int level2 = mLevel2Selected.getValue() == null ? -1 : mLevel2Selected.getValue().getId();
            int level1 = mLevel1Selected.getValue() == null ? -1 : mLevel1Selected.getValue().getId();

            ModelIndicatorLocation modelArea = new ModelIndicatorLocation(mCountrySelected.getValue(), level1, level2);

            Intent intent = new Intent(getContext(), ProgramResultsActivity.class);
            intent.putExtra(ProgramResultsActivity.BUNDLE_FILTER, modelArea);
//            if(level1 != -1) {
                intent.putExtra(ProgramResultsActivity.TITLE_1, Constants.COUNTRIES[mCountrySelected.getValue()]);
//            }
            if(mLevel1Selected.getValue() != null && level1 != -1) {
                intent.putExtra(ProgramResultsActivity.TITLE_2, mLevel1Selected.getValue().getValue());
            }
            startActivity(intent);
        }
    }

    @Override
    public void selectedCountry(@NotNull CountryJsonData countryJsonData) {
        if (mCountrySelected.getValue() == null || countryJsonData.getCountryId() != mCountrySelected.getValue()) {
            mLevel1Selected.setValue(null);
            mLevel2Selected.setValue(null);
            mCountrySelected.setValue(countryJsonData.getCountryId());

            List<String> list = ExtensionHelperKt.getLevel1Values(mCountrySelected.getValue(), mCountryDataList);

            mHasLevel1 = list != null && list.size() > 0;

            if(mHasLevel1) {
                level1Con.setClickable(true);
                tvSelectLevel1Heading.setTextColor(activeColor);
                level2Con.setClickable(true);
                tvSelectLevel2Heading.setTextColor(activeColor);
            }
            else {
                level1Con.setClickable(false);
                tvSelectLevel1Heading.setTextColor(inactiveColor);
                level2Con.setClickable(false);
                tvSelectLevel2Heading.setTextColor(inactiveColor);
            }
        }
    }

    @Override
    public void selectedLevel1(@Nullable LevelOneValuesItem level1Value) {
        if(level1Value != null && (mLevel1Selected.getValue() == null || (level1Value.getId() != mLevel1Selected.getValue().getId()))) {
            mLevel1Selected.setValue(level1Value);
            mLevel2Selected.setValue(null);

            assert mCountrySelected.getValue() != null;
            List<String> list = ExtensionHelperKt.getLevel2Values(mCountrySelected.getValue(), mLevel1Selected.getValue().getId(), mCountryDataList);
            mHasLevel2 = list != null && list.size() > 0;

            if(mHasLevel2) {
                level2Con.setClickable(true);
                tvSelectLevel2Heading.setTextColor(activeColor);
            }
            else {
                level2Con.setClickable(false);
                tvSelectLevel2Heading.setTextColor(inactiveColor);
            }
        }
    }

    @Override
    public void selectedLevel2(@Nullable LevelTwoValuesItem level2Value) {
        if(level2Value != null && (mLevel2Selected.getValue() == null || (level2Value.getId() != mLevel2Selected.getValue().getId()))) {
            mLevel2Selected.setValue(level2Value);
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        mCountrySelected.setValue(user.getCountryListId());
        countryCon.setClickable(true);

        mLevel1Selected.setValue(null);
        mLevel2Selected.setValue(null);

        assert mCountrySelected.getValue() != null;
        List<String> list = ExtensionHelperKt.getLevel1Values(mCountrySelected.getValue(), mCountryDataList);

        mHasLevel1 = list != null && list.size() > 0;

        if(mHasLevel1) {
            level1Con.setClickable(true);
            tvSelectLevel1Heading.setTextColor(activeColor);
            level2Con.setClickable(true);
            tvSelectLevel2Heading.setTextColor(activeColor);
        }
        else {
            level1Con.setClickable(false);
            tvSelectLevel1Heading.setTextColor(inactiveColor);
            level2Con.setClickable(false);
            tvSelectLevel2Heading.setTextColor(inactiveColor);
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
