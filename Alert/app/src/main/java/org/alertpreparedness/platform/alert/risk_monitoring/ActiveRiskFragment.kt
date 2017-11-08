package org.alertpreparedness.platform.alert.risk_monitoring


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import org.alertpreparedness.platform.alert.AlertApplication
import org.alertpreparedness.platform.alert.R
import org.jetbrains.anko.find


/**
 * A simple [Fragment] subclass.
 */
class ActiveRiskFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater?.inflate(R.layout.fragment_active_risk, container, false)
        val rvRiskActive: RecyclerView? = view?.find(R.id.rvRiskActive)
        rvRiskActive?.layoutManager = LinearLayoutManager(AlertApplication.getContext())
        rvRiskActive?.hasFixedSize()
        val indicator1 = ModelIndicator("1", ModelHazard("1", 2, true, false, 10, 10000000, ""), 1, "test 1", "", 0, 1111111111, 2222222222, listOf("a", "b"), listOf(ModelTrigger("1", 1, "1"), ModelTrigger("1", 1, "1")))
        val indicator2 = ModelIndicator("2", ModelHazard("1", 2, true, false, 10, 10000000, ""), 1, "test 2", "", 0, 1111111111, 2222222222, listOf("a", "b"), listOf(ModelTrigger("1", 1, "1"), ModelTrigger("1", 1, "1")))
        val indicator3 = ModelIndicator("3", ModelHazard("1", 2, true, false, 10, 10000000, ""), 1, "test 2", "", 0, 1111111111, 2222222222, listOf("a", "b"), listOf(ModelTrigger("1", 1, "1"), ModelTrigger("1", 1, "1")))
        val indicator4 = ModelIndicator("4", ModelHazard("1", 2, true, false, 10, 10000000, ""), 1, "test 2", "", 0, 1111111111, 2222222222, listOf("a", "b"), listOf(ModelTrigger("1", 1, "1"), ModelTrigger("1", 1, "1")))
        val group1: ExpandableGroup<ModelIndicator> = ExpandableGroup("r1", listOf(indicator1, indicator2))
        val group2: ExpandableGroup<ModelIndicator> = ExpandableGroup("r2", listOf(indicator3, indicator4))
        rvRiskActive?.adapter = HazardAdapter(listOf(group1, group2))
        return view
    }

}
