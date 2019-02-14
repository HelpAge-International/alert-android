package org.alertpreparedness.platform.v2.dashboard.home


import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.grpEmptyTasks
import kotlinx.android.synthetic.main.fragment_home.pbTasks
import kotlinx.android.synthetic.main.fragment_home.rvMyTasks
import org.alertpreparedness.platform.v1.MainDrawer
import org.alertpreparedness.platform.v1.R
import org.alertpreparedness.platform.v2.asObservable
import org.alertpreparedness.platform.v2.base.BaseFragment
import org.alertpreparedness.platform.v2.db
import org.alertpreparedness.platform.v2.utils.extensions.hide
import org.alertpreparedness.platform.v2.utils.extensions.show
import org.alertpreparedness.platform.v2.utils.extensions.toJson

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

        (activity as MainDrawer).toggleActionBar(MainDrawer.ActionBarState.ALERT)


        tasksAdapter = HomeTasksAdapter(context!!)
        rvMyTasks.layoutManager = LinearLayoutManager(context)
        rvMyTasks.adapter = tasksAdapter


        disposables += db.child("module").child("-LVnNNkdSddoKucUh1rg").child("4").asObservable().map { it.toJson() }
                .subscribe { println(it.toString()) }

    }

    override fun observeViewModel() {

        disposables += viewModel.tasks()
                .subscribe{tasks ->
                    tasksAdapter.updateItems(tasks)
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
