package org.alertpreparedness.platform.alert.utils

import android.support.v4.app.Fragment
import android.view.View
import android.widget.ProgressBar
import org.alertpreparedness.platform.alert.AlertApplication

/**
 * Created by fei on 14/11/2017.
 */

class ExtensionHelper {

    private var mLoading: ProgressBar? = null

    fun Fragment.showloading() {
        if (mLoading == null) {
            mLoading = ProgressBar(AlertApplication.getContext())

        }
    }

    fun Fragment.hideLoading() {
        if (mLoading != null) {
            mLoading!!.visibility = View.GONE
        }
    }

//    public void showProgressDialog() {
//        if (mProgressDialog == null) {
//            mProgressDialog = new ProgressDialog(this);
//            mProgressDialog.setMessage(getString(R.string.loading));
//            mProgressDialog.setCancelable(false);
//            mProgressDialog.setCanceledOnTouchOutside(false);
//            mProgressDialog.setIndeterminate(true);
//        }
//
//        mProgressDialog.show();
//    }
}