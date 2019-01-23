package org.alertpreparedness.platform.v1.helper;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;

import org.alertpreparedness.platform.v1.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Tj on 06/12/2017.
 */

public class AlertLevelDialog extends DialogFragment {

    private TypeSelectedListener mListener;
    private Unbinder unbinder;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.alert_level)
                .setView(R.layout.dialog_alert_type)
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    // User cancelled the dialog
                });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        unbinder = ButterKnife.bind(this, getDialog());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.red)
    public void onRedClick(View v) {
        if(mListener != null) {
            mListener.onTypeSelected(2);
        }
        dismiss();
    }

    @OnClick(R.id.green)
    public void onGreenClick(View v) {
        if(mListener != null) {
            mListener.onTypeSelected(0);
        }
        dismiss();
    }

    @OnClick(R.id.amber)
    public void onAmberClick(View v) {
        if(mListener != null) {
            mListener.onTypeSelected(1);
        }
        dismiss();
    }

    public void setListener(TypeSelectedListener listener) {
        mListener = listener;
    }

    public interface TypeSelectedListener {
        void onTypeSelected(int type);
    }

}