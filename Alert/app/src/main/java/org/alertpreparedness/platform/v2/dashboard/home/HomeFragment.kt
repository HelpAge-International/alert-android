package org.alertpreparedness.platform.v2.dashboard.home


import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.grpEmptyTasks
import kotlinx.android.synthetic.main.fragment_home.pbTasks
import kotlinx.android.synthetic.main.fragment_home.rvMyTasks
import org.alertpreparedness.platform.v1.R

import org.alertpreparedness.platform.v2.base.BaseFragment
import org.alertpreparedness.platform.v2.utils.extensions.hide
import org.alertpreparedness.platform.v2.utils.extensions.show

class HomeFragment : BaseFragment<HomeViewModel>() {


    private lateinit var tasksAdapter: HomeTasksAdapter

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    override fun viewModelClass(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun initViews() {
        super.initViews()

        tasksAdapter = HomeTasksAdapter(context!!)
        rvMyTasks.layoutManager = LinearLayoutManager(context)
        rvMyTasks.adapter = tasksAdapter
    }

    override fun observeViewModel() {

        disposables += viewModel.tasks()
                .subscribe{tasks ->
                    tasksAdapter.replaceAll(tasks)
                }

        disposables += viewModel.tasks()
                .map { it.isNotEmpty() }
                .subscribe { tasksVisible ->
                    pbTasks.hide()
                    rvMyTasks.show(tasksVisible)
                    grpEmptyTasks.show(!tasksVisible)
                }

    }

}
