package org.alertpreparedness.platform.v2.dashboard.home


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.rvMyTasks

import org.alertpreparedness.platform.v1.R
import org.alertpreparedness.platform.v2.base.BaseFragment
import org.alertpreparedness.platform.v2.repository.UserRepository

class HomeFragment : BaseFragment<HomeViewModel>() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    override fun viewModelClass(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun initViews() {
        super.initViews()
        rvMyTasks.layoutManager = LinearLayoutManager(context)
    }

    override fun observeViewModel() {

        disposables += viewModel.tasks()
                .subscribe{

                }

    }

}
