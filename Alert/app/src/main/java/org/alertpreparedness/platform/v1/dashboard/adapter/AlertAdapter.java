package org.alertpreparedness.platform.v1.dashboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import org.alertpreparedness.platform.v1.ExtensionHelperKt;
import org.alertpreparedness.platform.v1.R;
import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.firebase.AlertModel;
import org.alertpreparedness.platform.v1.interfaces.OnAlertItemClickedListener;
import org.alertpreparedness.platform.v1.model.User;
import org.alertpreparedness.platform.v1.utils.Constants;


/**
 * Created by faizmohideen on 20/11/2017.
 */

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.ViewHolder> {

    private Context context;
    private OnAlertItemClickedListener listener;
    private HashMap<String, AlertModel> items = new HashMap<>();
    private List<String> keys = new ArrayList<>();
    private final static String _TAG = "Adapter";
    private boolean isCountryDirector;

    @Inject
    User user;

    public AlertAdapter(Context context, OnAlertItemClickedListener listener) {
        DependencyInjector.userScopeComponent().inject(this);
        this.isCountryDirector = user.isCountryDirector();

        this.context = context;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivHazardIcon)
        ImageView imgHazardIcon;

        @BindView(R.id.tvAlertLevel)
        TextView tvAlertLevel;

        @BindView(R.id.tvNumOfPeople)
        TextView tvPeopleCount;

        @BindView(R.id.tvHazardName)
        TextView tvTitle;

        @BindView(R.id.tvAlertRequested)
        TextView txtRedRequested;
//
//        @BindView(R.id.imgRedReq)
//        ImageView imgAlertReq;

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (listener != null) {
                listener.onAlertItemClicked(items.get(keys.get(position)));
            }
        }

        private void bind(AlertModel alert) {

            String hazardName;

            if(alert.getHazardScenario() != -1) {
                 hazardName = ExtensionHelperKt.getHazardTypes().get(alert.getHazardScenario());
            }
            else {
                hazardName = "Other";
            }

            switch (alert.getAlertLevel()) {
                case Constants.TRIGGER_RED:
                    fetchIcon(hazardName, imgHazardIcon);
                    tvAlertLevel.setText(R.string.red_alert_text);
                    txtRedRequested.setVisibility(View.GONE);
                    break;
                case Constants.TRIGGER_AMBER:
                    fetchIcon(hazardName, imgHazardIcon);
                    tvAlertLevel.setText(R.string.amber_alert_text);
                    txtRedRequested.setVisibility(View.GONE);
                    break;
            }

            if(!hazardName.equals("Other")) {
                tvTitle.setText(hazardName);
            }
            else {
                tvTitle.setText(alert.getOtherName());
            }

            tvPeopleCount.setText(getNumOfPeopleText(alert.getEstimatedPopulation(), alert.getAffectedAreas().size()));

            if(isCountryDirector && !alert.getRedAlertApproved() && !alert.isNetwork() && alert.getAlertLevel() == Constants.TRIGGER_RED) {
                txtRedRequested.setVisibility(View.VISIBLE);
                txtRedRequested.setText(R.string.red_alert_requested_action_required);
            }
            else if(alert.isNetwork() && !alert.getRedAlertApproved() && alert.getAgencyAdminId().equals(alert.getLeadAgencyId()) && alert.getAgencyAdminId().equals(user.getUserID())  && alert.getAlertLevel() == Constants.TRIGGER_RED) {
                txtRedRequested.setVisibility(View.VISIBLE);
                txtRedRequested.setText(R.string.red_alert_requested_action_required);
            }
            else if(!alert.getRedAlertApproved() && !isCountryDirector && alert.getAlertLevel() == Constants.TRIGGER_RED) {
                txtRedRequested.setVisibility(View.VISIBLE);
                txtRedRequested.setText(R.string.red_alert_requested);
            }
            else {
                txtRedRequested.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBindViewHolder(AlertAdapter.ViewHolder holder, int position) {
        AlertModel alert = items.get(keys.get(position));
        holder.bind(alert);
    }

    public void remove(String id) {
        int index = keys.indexOf(id);
        keys.remove(index);
        items.remove(id);
        notifyItemRemoved(index);
    }

//    private void updateList() {
//        alertsList.clear();
//        alertsList.addAll(alertsMap.values());
//        Collections.sort(alertsList, (o1, o2) -> Long.compare(o2.getLevel(), o1.getLevel()));
//
//        notifyDataSetChanged();
//    }

    public void update(String id, AlertModel alert) {
//        System.out.println("id = [" + id + "], alert = [" + alert + "]");
        int index = keys.indexOf(id);
        if (index == -1) {
            keys.add(id);
            items.put(id, alert);
            notifyItemInserted(keys.size() - 1);
        } else {
            items.put(id, alert);
            notifyDataSetChanged();
        }
        if ((alert.isNetwork() && alert.hasNetworkApproval() || alert.getAlertLevel() == Constants.TRIGGER_GREEN)
                && index != -1) {
            keys.remove(index);
            items.remove(id);
            notifyItemRemoved(index);
        }
    }

    @Override
    public int getItemCount() {
        return keys.size();
    }

    public List<String> getAlerts() {
        return keys;
    }

    public AlertModel getModel(String key) {
        return items.get(key);
    }

    @Override
    public AlertAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alert, parent, false);
        return new AlertAdapter.ViewHolder(view);
    }

    public static void fetchIcon(String hazardName, ImageView imageView) {
        imageView.setImageResource(ExtensionHelperKt.getHazardImg(hazardName));
    }

    private String getNumOfPeopleText(long population, long numOfAreas) {
        return population + " people affected in " + numOfAreas + " areas";
    }



}
