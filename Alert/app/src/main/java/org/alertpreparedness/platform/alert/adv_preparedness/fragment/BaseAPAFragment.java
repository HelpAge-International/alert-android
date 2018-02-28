package org.alertpreparedness.platform.alert.min_preparedness.fragment;


import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.adv_preparedness.fragment.AdvPreparednessFragment;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionCHSRef;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionMandatedRef;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseCountryOfficeRef;
import org.alertpreparedness.platform.alert.dagger.annotation.NetworkRef;
import org.alertpreparedness.platform.alert.dagger.annotation.UserPublicRef;
import org.alertpreparedness.platform.alert.helper.DateHelper;
import org.alertpreparedness.platform.alert.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.CompleteActionActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.ViewAttachmentsActivity;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.ActionAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.PreparednessAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.min_preparedness.model.DataModel;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.Constants;

import javax.inject.Inject;

import ru.whalemare.sheetmenu.SheetMenu;

/**
 * Created by Tj on 01/02/2018.
 */

public abstract class BaseAPAFragment extends Fragment {

    protected abstract RecyclerView getListView();

    public void handleAdvFab() {
        AdvPreparednessFragment xFragment = null;
        for(Fragment fragment : getFragmentManager().getFragments()){
            if(fragment instanceof AdvPreparednessFragment){
                xFragment = (AdvPreparednessFragment) fragment;
                break;
            }
        }
        if(xFragment != null) {
            FloatingActionButton fab = xFragment.fabCreateAPA;
            fab.show();

            getListView().addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0 && fab.isShown()) {
                        fab.hide();
                    }
//                    else if (!fab.isShown() && dy <= 0) {
//                        fab.show();
//                    }
                    System.out.println("dy = " + dy);

                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        fab.show();
                    }
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });
        }
    }
}
