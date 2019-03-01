package org.alertpreparedness.platform.v1.mycountry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import org.alertpreparedness.platform.v1.ExtensionHelperKt;
import org.alertpreparedness.platform.R;
import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.firebase.ProgrammeModel;
import org.alertpreparedness.platform.v2.utils.GlideApp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Tj on 02/01/2018.
 */

public class ProgrammesAdapter extends ExpandableRecyclerViewAdapter<ProgrammesAdapter.ProgrammeHolder, ProgrammesAdapter.ProgrammeInfoViewHolder> {

    private final LayoutInflater inflater;
    private Context context;
    @Inject
    SimpleDateFormat dateFormatter;

    public ProgrammesAdapter(Context context, List<? extends ExpandableGroup> groups) {
        super(groups);
        inflater = LayoutInflater.from(context);
        this.context = context;
        DependencyInjector.userScopeComponent().inject(this);
    }

    @Override
    public ProgrammeHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        assert inflater != null;
        View view = inflater.inflate(R.layout.item_group, parent, false);
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
        final ProgrammeModel programme = ((ProgrammeInfo) group).getItems().get(childIndex);
        holder.bind(programme);
    }

    @Override
    public void onBindGroupViewHolder(ProgrammeHolder holder, int flatPosition, ExpandableGroup group) {
        holder.bind(group);
    }

    public class ProgrammeInfoViewHolder extends ChildViewHolder {

        @BindView(R.id.tvTitle)
        TextView title;

        @BindView(R.id.tvDesc)
        TextView desc;

        @BindView(R.id.tvTo)
        TextView to;

        @BindView(R.id.tvIn)
        TextView in;

        @BindView(R.id.tvFrom)
        TextView from;

        public ProgrammeInfoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(ProgrammeModel model) {
            from.setText(
                    String.format("%s - %s",
                            dateFormatter.format(new Date(model.getWhen())),
                            dateFormatter.format(new Date(model.getToDate()))
                    )
            );
            to.setText(model.getToWho());
            title.setText(ExtensionHelperKt.GetProgrammeSector(model.getSector()));
            desc.setText(model.getWhat());
            in.setText(String.format("%s, %s", model.getCountryName(), model.getLevel1Name()));
        }
    }

    public class ProgrammeHolder extends GroupViewHolder {

        @BindView(R.id.tvTitle)
        TextView title;

        @BindView(R.id.ivIcon)
        CircleImageView icon;

        @BindView(R.id.ivArrow)
        ImageView arrow;

        public ProgrammeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(ExpandableGroup group) {
            ProgrammeInfo info = (ProgrammeInfo)group;
            title.setText(group.getTitle());
            GlideApp.with(context)
                    .load(info.getAgency().getLogoPath())
                    .placeholder(R.drawable.agency_icon_placeholder)
                    .into(icon);
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
