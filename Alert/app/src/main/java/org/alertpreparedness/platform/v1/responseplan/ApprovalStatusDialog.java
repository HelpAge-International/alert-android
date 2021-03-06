package org.alertpreparedness.platform.v1.responseplan;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import org.alertpreparedness.platform.R;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Tj on 12/12/2017.
 */

public class ApprovalStatusDialog extends DialogFragment {

    private Unbinder unbinder;
    public static final String APPROVAL_STATUSES = "approval_statuses";

    @BindView(R.id.rvApprovalStatus)
    RecyclerView mList;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_approval_status, null);
        unbinder = ButterKnife.bind(this, v);

        ApprovalStatusObj[] objs = (ApprovalStatusObj[]) getArguments().getParcelableArray(APPROVAL_STATUSES);

        assert objs != null;
        mList.setAdapter(new ApprovalStatusAdapter(getActivity(), Arrays.asList(objs)));
        mList.setLayoutManager(new LinearLayoutManager(getActivity()));

        builder.setView(v)
                .setNegativeButton(R.string.close, (dialog, id) -> {
                    // User cancelled the dialog
                });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

