package org.alertpreparedness.platform.v2.mycountry

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_group.view.*
import kotlinx.android.synthetic.main.item_programme.view.*
import org.alertpreparedness.platform.R
import org.alertpreparedness.platform.v1.risk_monitoring.model.CountryJsonData
import org.alertpreparedness.platform.v2.models.Agency
import org.alertpreparedness.platform.v2.models.Programme
import org.alertpreparedness.platform.v2.utils.DiffGroupAdapter
import org.alertpreparedness.platform.v2.utils.DiffGroupComparator
import org.alertpreparedness.platform.v2.utils.GlideApp

class ProgrammeResultsAdapter(val context: Context, val countryData: CountryJsonData): DiffGroupAdapter<Agency, Programme, ProgrammeResultsAdapter.AgencyViewHolder, ProgrammeResultsAdapter.ProgrammeViewHolder>(object: DiffGroupComparator<Agency, Programme> {
    override fun areGroupsTheSame(o1: Agency, o2: Agency): Boolean {
        return o1.id == o2.id
    }

    override fun areGroupContentsTheSame(o1: Agency, o2: Agency): Boolean {
        return o1.name == o2.name
    }

    override fun areItemsTheSame(o1: Programme, o2: Programme): Boolean {
        return o1.id == o2.id
    }

    override fun areItemContentsTheSame(o1: Programme, o2: Programme): Boolean {
        return o1 == o2
    }
}, Agency::class.java, Programme::class.java) {
    override fun onCreateGroupViewHolder(parent: ViewGroup): AgencyViewHolder {
        return AgencyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false))
    }

    override fun onCreateItemViewHolder(parent: ViewGroup): ProgrammeViewHolder {
        return ProgrammeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_programme, parent, false))
    }

    inner class AgencyViewHolder(itemView: View): ViewHolderGroup(itemView) {
        val ivIcon = itemView.ivIcon
        val tvGroupTitle = itemView.tvGroupTitle
        val ivArrow = itemView.ivArrow

        init {
            itemView.setOnClickListener {
                toggle()
            }
        }

        override fun bind(model: Agency, position: Int) {
            GlideApp.with(ivIcon)
                    .load(model.logoPath)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.mipmap.ic_launcher_round)
                    .into(ivIcon)

            tvGroupTitle.text = model.name
            ivArrow.rotation = if (isExpanded()) 180F else 0F
        }

        override fun expanded() {
            ivArrow.animate()
                    .rotation(180f)
                    .setDuration(200)
        }

        override fun collapsed() {
            ivArrow.animate()
                    .rotation(0f)
                    .setDuration(200)
        }

    }

    inner class ProgrammeViewHolder(itemView: View): ViewHolderItem(itemView) {

        val tvTitle = itemView.tvTitle
        val tvDesc = itemView.tvDesc
        val tvTo = itemView.tvTo
        val tvIn = itemView.tvIn
        val tvFrom = itemView.tvFrom

        override fun bind(model: Programme, position: Int) {

            tvTitle.setText(model.sector.string)
            tvDesc.text = model.what
            tvTo.text = context.getString(R.string.programme_to, model.toWho)

            val level1 = if(model.level1 != null && countryData.levelOneValues != null) countryData.levelOneValues.first { it.id == model.level1 } else null
            val level2 = if(model.level2 != null && level1?.levelTwoValues != null) level1.levelTwoValues.first { it.id == model.level2 } else null

            tvIn.text = context.getString(R.string.programme_in, listOf(
                    context.getString(model.where.string),
                    level1?.value,
                    level2?.value
            ).filterNotNull().joinToString(", "))
            tvFrom.text = context.getString(R.string.programme_from, model.time.toDate(), model.toDate.toDate())
        }
    }
}
