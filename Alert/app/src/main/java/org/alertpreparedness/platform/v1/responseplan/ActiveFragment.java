package org.alertpreparedness.platform.v1.responseplan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.v1.ExtensionHelperKt;
import org.alertpreparedness.platform.v1.MainDrawer;
import org.alertpreparedness.platform.v1.R;
import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.dagger.annotation.ResponsePlanObservable;
import org.alertpreparedness.platform.v1.dagger.annotation.ResponsePlansRef;
import org.alertpreparedness.platform.v1.firebase.UserProfileModel;
import org.alertpreparedness.platform.v1.firebase.consumers.ItemConsumer;
import org.alertpreparedness.platform.v1.firebase.data_fetchers.FetcherResultItem;
import org.alertpreparedness.platform.v1.firebase.wrappers.ResponsePlanResultItem;
import org.alertpreparedness.platform.v1.min_preparedness.model.Note;
import org.alertpreparedness.platform.v1.model.User;
import org.alertpreparedness.platform.v1.utils.AppUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Tj on 12/12/2017.
 */

public class ActiveFragment extends Fragment implements ResponsePlansAdapter.ResponseAdapterListener {

    @BindView(R.id.rvPlans)
    RecyclerView mPlansList;

    @Inject @ResponsePlansRef
    DatabaseReference responsePlans;

    @Inject
    User user;

    @Inject
    @ResponsePlanObservable
    Flowable<FetcherResultItem<ResponsePlanResultItem>> responsePlanFlowable;

    protected ResponsePlansAdapter mAdapter;

    private CompositeDisposable disposable = new CompositeDisposable();

    protected boolean active = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_response_plans_subfrag, container, false);

        ButterKnife.bind(this, v);
        DependencyInjector.userScopeComponent().inject(this);

        initViews();

        ((MainDrawer)getActivity()).toggleActionBarWithTitle(MainDrawer.ActionBarState.NORMAL, R.string.response_plans);
        ((MainDrawer)getActivity()).removeActionbarElevation();

        return v;
    }

    private void initViews() {

        mAdapter = getmAdapter();
        mPlansList.setAdapter(mAdapter);

        mPlansList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPlansList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        disposable.add(responsePlanFlowable.subscribe(
            new ItemConsumer<>(
                this::onDataChange,
                (m) -> onItemRemoved(m.getResponsePlan())
            )
        ));
    }

    protected ResponsePlansAdapter getmAdapter() {
        return new ResponsePlansAdapter(getContext(), responsePlans, true, this);
    }

    @Override
    public void onResponsePlanSelected(int pos) {
        if(mAdapter.getItem(pos).status > 0) {
            ApprovalStatusDialog dialog = new ApprovalStatusDialog();
            Bundle data = new Bundle();
            List<ApprovalStatusObj> items = new ArrayList<>();

            ResponsePlanObj planObj = mAdapter.getItem(pos);

            if (mAdapter.getItem(pos).countryApproval != -1) {
                items.add(new ApprovalStatusObj("Country Director", planObj.countryApproval, planObj.getNotes()));
            }
            if(mAdapter.getItem(pos).regionalApproval != -1) {
                items.add(new ApprovalStatusObj("Regional Director", planObj.regionalApproval, planObj.getNotes()));
            }
            if(mAdapter.getItem(pos).globalApproval != -1) {
                items.add(new ApprovalStatusObj("Global Director", planObj.globalApproval, planObj.getNotes()));
            }
            data.putParcelableArray(ApprovalStatusDialog.APPROVAL_STATUSES, items.toArray(new ApprovalStatusObj[items.size()]));
            dialog.setArguments(data);
            dialog.show(getActivity().getSupportFragmentManager(), "alert_level");
        }
    }

    public void onDataChange(ResponsePlanResultItem item) {
        if (item.getResponsePlan().child("isActive").exists()) {

            boolean isActive = (boolean) item.getResponsePlan().child("isActive").getValue();

            if (isActive == active) {
                int regionalApproval = getApprovalStatus(item.getResponsePlan().child("approval").child("regionDirector"));
                int countryApproval = getApprovalStatus(item.getResponsePlan().child("approval").child("countryDirector"));
                int globalApproval = getApprovalStatus(item.getResponsePlan().child("approval").child("globalDirector"));

                Long createdAt = (Long) item.getResponsePlan().child("timeCreated").getValue();
                String hazardType = ExtensionHelperKt.getHazardTypes().get(Integer.valueOf((String) item.getResponsePlan().child("hazardScenario").getValue()));
                String percentCompleted = String.valueOf(item.getResponsePlan().child("sectionsCompleted").getValue());
                int status = Integer.valueOf(String.valueOf(item.getResponsePlan().child("status").getValue()));
                String name = (String) item.getResponsePlan().child("name").getValue();

                mAdapter.addItem(item.getResponsePlan().getKey(), new ResponsePlanObj(
                        hazardType,
                        percentCompleted,
                        name,
                        status,
                        new Date(createdAt),
                        regionalApproval,
                        countryApproval,
                        globalApproval)
                );

                disposable.add(item.getNotes().subscribe(new ItemConsumer<>(
                    (m) -> {
                        System.out.println("m.getUser() = " + m.getUser());
                        addNote(item.getResponsePlan().getKey(), m.getValue(), m.getUser());
                    },
                    (m) -> removeNote(item.getResponsePlan().getKey(), m.getValue()))
                ));

            }
        }
    }

    private void addNote(String planKey, DataSnapshot dataSnapshot, Single<DataSnapshot> userSignle) {
        disposable.add(userSignle.subscribe((userSnapshot) -> {
            Note n = AppUtils.getFirebaseModelFromDataSnapshot(dataSnapshot, Note.class);
            UserProfileModel user =  AppUtils.getFirebaseModelFromDataSnapshot(userSnapshot, UserProfileModel.class);
            n.setFullName(user.getFirstName() + " " + user.getLastName());
            mAdapter.addNote(planKey, n);
        }));
    }

    private void removeNote(String planKey, DataSnapshot dataSnapshot) {
        mAdapter.removeNote(planKey, dataSnapshot.getKey());
    }

    public void onItemRemoved(DataSnapshot dataSnapshot) {
        mAdapter.removeItem(dataSnapshot);
    }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    protected int getApprovalStatus(DataSnapshot ref) {
        if(ref.getValue() != null) {
            for (DataSnapshot child : ref.getChildren()) {
                if(child.getValue() != null) {
                   return  (int)((long)child.getValue());
                }
                else {
                    break;
                }
            }
        }
        return -1;//does not contain ref
    }

    @Override
    public void onStop() {
        super.onStop();
        disposable.dispose();
    }

}
