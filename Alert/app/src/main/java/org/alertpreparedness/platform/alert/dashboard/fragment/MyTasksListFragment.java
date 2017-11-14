package org.alertpreparedness.platform.alert.dashboard.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.model.Tasks;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyTasksListFragment extends Fragment {

    private RecyclerView myTasksListView;
    private LinearLayoutManager layoutManager;
    private Adapter adapter;

    public MyTasksListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.my_tasks_list, container, false);

        myTasksListView = (RecyclerView) view.findViewById(R.id.tasks_list_view);

        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        return view;
    }

}
