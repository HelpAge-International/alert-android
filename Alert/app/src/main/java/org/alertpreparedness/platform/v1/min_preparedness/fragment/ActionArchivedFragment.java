package org.alertpreparedness.platform.v1.min_preparedness.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.alertpreparedness.platform.v1.R;
import org.alertpreparedness.platform.v1.dagger.annotation.ActionGroupObservable;
import org.alertpreparedness.platform.v1.firebase.ActionModel;
import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.firebase.wrappers.ActionItemWrapper;
import org.alertpreparedness.platform.v1.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.v1.min_preparedness.activity.ViewAttachmentsActivity;
import org.alertpreparedness.platform.v1.min_preparedness.adapter.ActionAdapter;
import org.alertpreparedness.platform.v1.model.User;
import org.alertpreparedness.platform.v1.utils.Constants;
import org.alertpreparedness.platform.v1.utils.PermissionsHelper;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import ru.whalemare.sheetmenu.SheetMenu;

/**
 * Created by faizmohideen on 21/12/2017.
 */

public class ActionArchivedFragment extends Fragment implements ActionAdapter.ActionAdapterListener {

    @BindView(R.id.rvMinAction)
    RecyclerView mActionRV;

    @BindView(R.id.tvStatus)
    TextView tvActionArchived;

    @BindView(R.id.imgStatus)
    ImageView imgArchived;

    @BindView(R.id.tvNoAction)
    TextView txtNoAction;

    private ActionAdapter mAdapter;

    @Inject
    PermissionsHelper permissions;

    @Inject
    User user;

    @Inject
    @ActionGroupObservable
    Flowable<Collection<ActionItemWrapper>>  actionFlowable;

    CompositeDisposable disposable = new CompositeDisposable();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_minimum, container, false);

        ButterKnife.bind(this, v);
        DependencyInjector.userScopeComponent().inject(this);

        initViews();

        return v;
    }

    private void initViews() {
        assert imgArchived != null;
        imgArchived.setImageResource(R.drawable.ic_close_round_gray);
        assert tvActionArchived != null;
        tvActionArchived.setText(R.string.archived_title);
        tvActionArchived.setTextColor(getResources().getColor(R.color.alertGray));

        mAdapter = new ActionAdapter(getContext(), this);
        mActionRV.setAdapter(mAdapter);

        mActionRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mActionRV.setItemAnimator(new DefaultItemAnimator());
        mActionRV.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        initData();
    }

    private void initData() {
        disposable = new CompositeDisposable();
        disposable.add(actionFlowable.subscribe(collectionFetcherResultItem -> {

            ArrayList<String> result = new ArrayList<>();

            for(ActionItemWrapper wrapper : collectionFetcherResultItem) {
                ActionModel actionModel = wrapper.makeModel();

                if(actionModel.getAsignee() != null &&
                        actionModel.getAsignee().equals(user.getUserID()) &&
                        !actionModel.getIsComplete() &&
                        actionModel.getIsArchived() &&
                        actionModel.getLevel() == Constants.MPA) {
                    onActionRetrieved(actionModel);
                    result.add(actionModel.getId());

                }

            }
            mAdapter.updateKeys(result);

        }));

    }

    @Override
    public void onActionItemSelected(int pos, String key, String parentId) {
        SheetMenu.with(getContext()).setMenu(R.menu.menu_archived_mpa).setClick(menuItem -> {
            switch (menuItem.getItemId()) {
//                case R.id.reactive_action:
//                    //TODO
//                    Snackbar.make(getActivity().findViewById(R.id.cl_in_progress), "Reactivate Clicked", Snackbar.LENGTH_LONG).show();
//                    break;
                case R.id.action_notes:
                    Intent intent2 = new Intent(getActivity(), AddNotesActivity.class);
                    intent2.putExtra(AddNotesActivity.PARENT_ACTION_ID, parentId);
                    intent2.putExtra(AddNotesActivity.ACTION_ID, key);
                    startActivity(intent2);
                    break;
                case R.id.attachments:
                    Intent intent3 = new Intent(getActivity(), ViewAttachmentsActivity.class);
                    intent3.putExtra(ViewAttachmentsActivity.PARENT_ACTION_ID, key);
                    intent3.putExtra(ViewAttachmentsActivity.ACTION_ID, key);
                    startActivity(intent3);
                    break;
            }
            return false;
        }).show();
    }

    @Override
    public void itemRemoved(String key) {
        if (mAdapter.getItemCount() == 0) {
            txtNoAction.setVisibility(View.VISIBLE);
        }
    }

    public void onActionRetrieved(ActionModel action) {
        if(permissions.checkCanViewMPA(action)) {
            txtNoAction.setVisibility(View.GONE);
            mAdapter.addItems(action.getId(), action);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        disposable.dispose();
    }

    @Override
    public void onPause() {
        super.onPause();
        disposable.dispose();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(disposable.isDisposed()) {
            initData();
        }
    }
}
