package org.alertpreparedness.platform.alert.dashboard.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.alertpreparedness.platform.alert.ExtensionHelperKt;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.firebase.AlertModel;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.interfaces.OnAlertItemClickedListener;
import org.alertpreparedness.platform.alert.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by faizmohideen on 20/11/2017.
 */

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.ViewHolder> {

    private Context context;
    private OnAlertItemClickedListener listener;
    private HashMap<String, AlertModel> alertsMap;
    private List<AlertModel> alertsList = new ArrayList<>();
    private final static String _TAG = "Adapter";
    private boolean isCountryDirector;


    public AlertAdapter(@NonNull HashMap<String, AlertModel> alertsMap, Context context, OnAlertItemClickedListener listener) {
        this.isCountryDirector = true;
        this.alertsMap = alertsMap;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public AlertAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_alert, parent, false);
        return new AlertAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlertAdapter.ViewHolder holder, int position) {
        AlertModel alert = alertsList.get(position);
        holder.bind(alert);
    }

    public void remove(String id){
        alertsMap.remove(id);
        updateList();
    }

    private void updateList() {
        alertsList.clear();
        alertsList.addAll(alertsMap.values());
        Collections.sort(alertsList, (o1, o2) -> Long.compare(o2.getAlertLevel(), o1.getAlertLevel()));

        notifyDataSetChanged();
    }

    public void update(String id, AlertModel alert) {
        alertsMap.put(id, alert);

        updateList();
    }

    @Override
    public int getItemCount() {
        return alertsMap.size();
    }

    public List<AlertModel> getAlerts() {
        return alertsList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_alert_level)
        TextView tvAlertLevel;

        @BindView(R.id.txt_hazard_name)
        TextView tvHazardName;

        @BindView(R.id.txt_num_of_people)
        TextView tvPeopleCount;

        @BindView(R.id.textViewAlertReq)
        TextView txtRedRequested;

        @BindView(R.id.img_alert_colour)
        ImageView imgAlertColour;

        @BindView(R.id.img_hazard_icon)
        ImageView imgHazardIcon;
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
                listener.onAlertItemClicked(alertsList.get(position));
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
                    imgAlertColour.setImageResource(R.drawable.red_alert_left);
                    break;
                case Constants.TRIGGER_AMBER:
                    fetchIcon(hazardName, imgHazardIcon);
                    tvAlertLevel.setText(R.string.amber_alert_text);
                    imgAlertColour.setImageResource(R.drawable.amber_alert_left);
                    break;
            }

            if(!hazardName.equals("Other")) {
                tvHazardName.setText(hazardName);
            }
            else {
                tvHazardName.setText(alert.getOtherName());
            }

            tvPeopleCount.setText(getNumOfPeopleText(alert.getEstimatedPopulation(), alert.getAffectedAreas().size()));

            System.out.println("isCountryDirector && alert.wasRedAlertRequested() = " + (isCountryDirector && alert.wasRedAlertRequested()));

            if(isCountryDirector && alert.wasRedAlertRequested()) {
//                imgAlertReq.setVisibility(View.VISIBLE);
                txtRedRequested.setVisibility(View.VISIBLE);
                imgAlertColour.setImageResource(R.drawable.gray_alert_left);
                txtRedRequested.setText(R.string.txt_cd_red_request);
            }
            else if(alert.wasRedAlertRequested() && !isCountryDirector) {
//                imgAlertReq.setVisibility(View.VISIBLE);
                txtRedRequested.setVisibility(View.VISIBLE);
                imgAlertColour.setImageResource(R.drawable.gray_alert_left);
                txtRedRequested.setText(R.string.txt_red_requested);
            }
            else {
//                imgAlertReq.setVisibility(View.GONE);
                txtRedRequested.setVisibility(View.GONE);
            }
        }
    }

    public static void fetchIcon(String hazardName, ImageView imageView) {
        imageView.setImageResource(ExtensionHelperKt.getHazardImg(hazardName));
    }

    private String getNumOfPeopleText(long population, long numOfAreas) {
        return population + " people affected in " + numOfAreas + " areas";
    }



}
