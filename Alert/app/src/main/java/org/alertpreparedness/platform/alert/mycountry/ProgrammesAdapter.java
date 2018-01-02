package org.alertpreparedness.platform.alert.mycountry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import org.alertpreparedness.platform.alert.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Tj on 02/01/2018.
 */

public class ProgrammesAdapter extends ExpandableRecyclerViewAdapter<ProgrammesAdapter.ProgrammeHolder, ProgrammesAdapter.ProgrammeInfoViewHolder> {

    private final LayoutInflater inflater;
    private Context context;

    public ProgrammesAdapter(Context context, List<? extends ExpandableGroup> groups) {
        super(groups);
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public ProgrammeHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        assert inflater != null;
        View view = inflater.inflate(R.layout.risk_hazard_item_view, parent, false);
        return new ProgrammeHolder(view);
    }

    @Override
    public ProgrammeInfoViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_programme, parent, false);
        return new ProgrammeInfoViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(ProgrammeInfoViewHolder holder, int flatPosition, ExpandableGroup group,
                                      int childIndex) {
        final Programme programme = ((ProgrammeInfo) group).getItems().get(childIndex);

    }

    @Override
    public void onBindGroupViewHolder(ProgrammeHolder holder, int flatPosition, ExpandableGroup group) {
        holder.setTitle(group);
    }



    public class ProgrammeInfoViewHolder extends ChildViewHolder {

        @BindView(R.id.title)
        TextView title;

        @BindView(R.id.desc)
        TextView desc;

        @BindView(R.id.to)
        TextView to;

        @BindView(R.id.in)
        TextView in;

        @BindView(R.id.from)
        TextView from;

        public ProgrammeInfoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(Programme artist) {

        }
    }

    public class ProgrammeHolder extends GroupViewHolder {

        @BindView(R.id.tvHazardName)
        TextView title;

        @BindView(R.id.civHazard)
        CircleImageView icon;

        @BindView(R.id.ivArrow)
        ImageView arrow;

        public ProgrammeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setTitle(ExpandableGroup group) {
            title.setText(group.getTitle());
        }

        @Override
        public void expand() {
            arrow.animate().rotationBy(180f)
                    .setDuration(300)
                    .setInterpolator(new LinearInterpolator())
                    .start();
        }

        @Override
        public void collapse() {
            arrow.animate().rotationBy(180f)
                    .setDuration(300)
                    .setInterpolator(new LinearInterpolator())
                    .start();
        }
    }
}
