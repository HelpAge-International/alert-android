package org.alertpreparedness.platform.v2.base

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.disposables.Disposable
import org.alertpreparedness.platform.v2.base.BaseActivity

abstract class BaseFragment<VM: BaseViewModel>: Fragment() {

    lateinit var viewModel: VM
    val disposables: MutableList<Disposable> = mutableListOf()

    /**
     * OnCreate Method
     * - Will run arguments(bundle) in the appropriate time
     * - Apply view
     * - Set the VM
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        arguments?.let {
            arguments(it)
        }
        super.onCreate(savedInstanceState)
        viewModel = viewModelProvider().get(viewModelClass())
    }

    /**
     * OnCreateView
     * - Inflate the current view
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayoutId(), container, false)
    }

    /**
     * OnViewCreated
     * - Called when the view is successfully created.
     *   Needs to be here due to Kotlin Layout binding!
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    /**
     * Argument intercept method. To handle argument imports to the project
     *   Override to import arguments
     */
    open fun arguments(bundle: Bundle) {

    }

    /**
     * Initialise the views
     *   Override to perform some operations on the view
     */
    open fun initViews() {

    }

    override fun onResume() {
        super.onResume()
        observeViewModel()
    }

    /**
     * OnPause method
     * - Clean up observables
     */
    override fun onPause() {
        super.onPause()
        if (disposeOnPause()) {
            disposables.forEach { it.dispose() }
        }
    }
    open fun disposeOnPause(): Boolean {
        return true
    }


    /**
     * State restoration methods for passing data to the view models
     * - onSaveInstanceState
     */
    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.saveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }


    /**
     * State restoration methods for passing data to the view models
     * - onRestoreInstanceState
     */
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            viewModel.restoreInstanceState(it)
        }
    }

    //region Permissions


    /**
     * Request a given permission
     * - Callbacks will be fired below under permissionDenied, permissionGranted, or permissionShowRational
     */
    fun requestPermission(permission: String) {
        if (ActivityCompat.checkSelfPermission(activity!!, permission) == PackageManager.PERMISSION_GRANTED) {
            permissionGranted(permission)
        } else {
            ActivityCompat.requestPermissions(activity!!, arrayOf(permission), BaseActivity.PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == BaseActivity.PERMISSION_REQUEST_CODE && permissions.isNotEmpty()) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionGranted(permissions[0])
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permissions[0])) {
                    permissionShowRational(permissions[0])
                } else {
                    permissionDenied(permissions[0])
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Method fired when a permission is denied
     */
    open fun permissionDenied(perm: String) {

    }

    /**
     * Method fired when a permission is granted
     */
    open fun permissionGranted(perm: String) {

    }

    /**
     * Method fired when a permission should show rational
     */
    open fun permissionShowRational(perm: String) {

    }

    //endregion

    //region Abstract methods

    /**
     * Abstract method to currentLanguage the layout ID.
     *   Will be used in the onCreate to handle layout inflation
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun viewModelClass(): Class<VM>

    open fun viewModelProvider(): ViewModelProvider {
        return ViewModelProviders.of(this)
    }

    /**
     * Observe I/O on the view model
     */
    abstract fun observeViewModel()

    //endregion
}