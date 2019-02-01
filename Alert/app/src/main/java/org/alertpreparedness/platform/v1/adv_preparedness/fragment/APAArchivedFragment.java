package org.alertpreparedness.platform.v1.adv_preparedness.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import java.util.ArrayList;
import java.util.Collection;
import javax.inject.Inject;
import org.alertpreparedness.platform.v1.R;
import org.alertpreparedness.platform.v1.adv_preparedness.activity.EditAPAActivity;
import org.alertpreparedness.platform.v1.adv_preparedness.adapter.APActionAdapter;
import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.dagger.annotation.ActionGroupObservable;
import org.alertpreparedness.platform.v1.firebase.ActionModel;
import org.alertpreparedness.platform.v1.firebase.wrappers.ActionItemWrapper;
import org.alertpreparedness.platform.v1.interfaces.DisposableFragment;
import org.alertpreparedness.platform.v1.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.v1.min_preparedness.activity.ViewAttachmentsActivity;
import org.alertpreparedness.platform.v1.model.User;
import org.alertpreparedness.platform.v1.utils.Constants;
import org.alertpreparedness.platform.v1.utils.PermissionsHelper;
import ru.whalemare.sheetmenu.SheetMenu;

/**
 * Created by faizmohideen on 06/01/2018.
 */

public class APAArchivedFragment extends BaseAPAFragment implements APActionAdapter.APAAdapterListener, DisposableFragment {

    public APAArchivedFragment() {
        // Required empty public constructor
    }

    @Nullable
    @BindView(R.id.rvAdvAction)
    RecyclerView mAdvActionRV;

    @Nullable
    @BindView(R.id.imgStatus)
    ImageView imgActionArchived;

    @Nullable
    @BindView(R.id.tvStatus)
    TextView tvActionArchived;

    @BindView(R.id.tvAPANoAction)
    TextView txtNoAction;

    private APActionAdapter mAPAdapter;

    @Inject
    PermissionsHelper permissions;

    @Inject
    @ActionGroupObservable
    Flowable<Collection<ActionItemWrapper>> actionFlowable;

    CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_advanced, container, false);

        ButterKnife.bind(this, v);
        DependencyInjector.userScopeComponent().inject(this);

        initViews();

        return v;
    }

    private void initViews() {
        assert imgActionArchived != null;
        imgActionArchived.setImageResource(R.drawable.preparedness_grey);
        assert tvActionArchived != null;
        tvActionArchived.setText(R.string.archived_title);
        tvActionArchived.setTextColor(getResources().getColor(R.color.alertGray));
        mAPAdapter = new APActionAdapter(getContext(), this);
        assert mAdvActionRV != null;
        mAdvActionRV.setAdapter(mAPAdapter);

        mAdvActionRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdvActionRV.setItemAnimator(new DefaultItemAnimator());
        mAdvActionRV.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        initData();

        handleAdvFab();
    }

    private void initData() {
        disposable = new CompositeDisposable();
        disposable.add(actionFlowable.subscribe(collectionFetcherResultItem -> {

            ArrayList<String> result = new ArrayList<>();

            for(ActionItemWrapper wrapper : collectionFetcherResultItem) {
                ActionModel actionModel = wrapper.makeModel();

                if(actionModel.getAsignee() != null && actionModel.getAsignee().equals(user.getUserID()) && actionModel.getIsArchived() && actionModel.getLevel() == Constants.APA) {
                    onActionRetrieved(actionModel);
                    result.add(actionModel.getId());
                }
            }

            mAPAdapter.updateKeys(result);

        }));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActionItemSelected(int pos, String key, String parentId) {
        SheetMenu.with(getContext()).setMenu(R.menu.menu_archived).setClick(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.edit:
                    if(permissions.checkEditAPA(mAPAdapter.getItem(pos), getActivity())) {
                        Intent i = new Intent(getContext(), EditAPAActivity.class);
                        i.putExtra(EditAPAActivity.MODEL, mAPAdapter.getItem(pos));
                        startActivity(i);
                    }
                    break;
                case R.id.action_notes:
                    Intent intent2 = new Intent(getActivity(), AddNotesActivity.class);
                    intent2.putExtra(AddNotesActivity.PARENT_ACTION_ID, parentId);
                    intent2.putExtra(AddNotesActivity.ACTION_ID, key);
                    startActivity(intent2);
                    break;
                case R.id.attachments:
                    Intent intent3 = new Intent(getActivity(), ViewAttachmentsActivity.class);
                    intent3.putExtra(ViewAttachmentsActivity.PARENT_ACTION_ID, parentId);
                    intent3.putExtra(ViewAttachmentsActivity.ACTION_ID, key);
                    startActivity(intent3);
                    break;
            }
            return false;
        }).show();
    }

    @Override
    public void onAdapterItemRemoved(String key) {
        if(mAPAdapter.getItemCount()==0) {
            txtNoAction.setVisibility(View.VISIBLE);
        }
    }

    public void onActionRetrieved(ActionModel action) {
        if(permissions.checkCanViewAPA(action)) {
            txtNoAction.setVisibility(View.GONE);
            mAPAdapter.addItems(action.getId(), action);
        }
    }

    @Override
    protected RecyclerView getListView() {
        return mAdvActionRV;
    }

    @Override
    public void dispose() {
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
