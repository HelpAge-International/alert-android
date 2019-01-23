package org.alertpreparedness.platform.v1.adv_preparedness.fragment;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

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
//                    System.out.println("dy = " + dy);

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
