package org.alertpreparedness.platform.v2.base

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.alertpreparedness.platform.v1.R
import org.alertpreparedness.platform.v2.utils.ColorUtil
import org.alertpreparedness.platform.v2.utils.extensions.subscribeNoError

abstract class BaseActivity<VM : BaseViewModel> : AppCompatActivity(){

    lateinit var viewModel: VM
    val disposables: MutableList<Disposable> = mutableListOf()
    var toolbar: Toolbar? = null

    /**
     * OnCreate Method
     * - Will run arguments(bundle) in the appropriate time
     * - Apply view
     * - Set the VM
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        var bundle: Bundle? = savedInstanceState
        if (bundle == null) {
            bundle = Bundle()
        }
        intent.extras?.let {
            bundle.putAll(it)
        }
        arguments(bundle)
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        viewModel = viewModelProvider().get(viewModelClass())
        toolbar = findViewById(R.id.toolbar)
        initViews()
    }

    abstract fun viewModelClass(): Class<VM>

    open fun viewModelProvider(): ViewModelProvider {
        return ViewModelProviders.of(this)
    }

    /**
     * Argument intercept method. To handle argument imports to the project
     *   Override to import arguments
     */
    open fun arguments(bundle: Bundle) {
    }

    /**
     * Initialise the views
     */
    @CallSuper
    open fun initViews() {
        toolbar?.let{
            setSupportActionBar(it)
            supportActionBar?.title = null
        }
    }

    fun showBackButton(show: Boolean = true, @DrawableRes drawable: Int? = null){
        supportActionBar?.setDisplayHomeAsUpEnabled(show)
        supportActionBar?.setDisplayShowHomeEnabled(show)
        if(drawable != null) {
            supportActionBar?.setHomeAsUpIndicator(drawable)
        }
    }

    fun setToolbarTitle(title: String){
        supportActionBar?.title = title
    }

    fun setToolbarTitle(@StringRes title: Int){
        setToolbarTitle(getString(title))
    }

    fun bindToolbarTitle(titleObservable: Observable<String>){
        disposables += titleObservable.subscribeNoError {
            setToolbarTitle(it)
        }
    }

    @MenuRes
    open fun getToolbarMenu(): Int? {
        return null
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuRes = getToolbarMenu()
        return if (menuRes != null) {
            menuInflater.inflate(menuRes, menu)
            true
        } else {
            super.onCreateOptionsMenu(menu)
        }
    }

    /**
     * OnResume
     * - Observe outputs of the viewmodel
     */
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
        outState.let {
            viewModel.saveInstanceState(it)
        }
        super.onSaveInstanceState(outState)
    }

    /**
     * State restoration methods for passing data to the view models
     * - onRestoreInstanceState
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            viewModel.restoreInstanceState(it)
        }
        super.onRestoreInstanceState(savedInstanceState)
    }

    //region Abstract methods

    /**
     * Abstract method to currentLanguage the layout ID.
     *   Will be used in the onCreate to handle layout inflation
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    /**
     * Initialise the views.
     * Kotlin Extensions should mean views are available in this method!
     */
    abstract fun observeViewModel()

    //endregion

    //region Status bar

    fun setStatusBar(@ColorInt col: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ColorUtil.darken(col)
        }
    }

    //endregion

    //region Permissions


    /**
     * Request a given permission
     * - Callbacks will be fired below under permissionDenied, permissionGranted, or permissionShowRational
     */
    fun requestPermission(permission: String) {
        if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            permissionGranted(permission)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CODE && permissions.isNotEmpty()) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionGranted(permissions[0])
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
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

    //region Fragments

    fun loadFragment(frag: Fragment, @IdRes layoutRes: Int, tag: String?) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (tag != null) {
            transaction.replace(layoutRes, frag, tag)
        } else {
            transaction.replace(layoutRes, frag)
        }
        transaction.commit()
    }

    fun loadFragment(frag: Fragment, @IdRes layoutRes: Int) {
        loadFragment(frag, layoutRes, null)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)
        return if (item?.itemId == android.R.id.home) {
            finish()
            true
        } else {
            false
        }
    }

    //endregion

    companion object {
        const val PERMISSION_REQUEST_CODE: Int = 1001
    }
}