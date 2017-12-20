package org.alertpreparedness.platform.alert.dashboard.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.alertpreparedness.platform.alert.ExtensionHelperKt;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.interfaces.OnAlertItemClickedListener;
import org.alertpreparedness.platform.alert.dashboard.model.Alert;
import org.alertpreparedness.platform.alert.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


/**
 * Created by faizmohideen on 20/11/2017.
 */

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.ViewHolder> {

    private Context context;
    private OnAlertItemClickedListener listener;
    private HashMap<String, Alert> alertsMap;
    private List<Alert> alertsList = new ArrayList<>();
    private final static String _TAG = "Adapter";
    private boolean isCountryDirector;


    public AlertAdapter(@NonNull HashMap<String, Alert> alertsMap, Context context, OnAlertItemClickedListener listener) {
        super();

        this.isCountryDirector = UserInfo.getUser(context).isCountryDirector();
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
        Alert alert = alertsList.get(position);
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

    public void update(String id, Alert alert) {
        alertsMap.put(id, alert);

        updateList();
    }

    @Override
    public int getItemCount() {
        return alertsMap.size();
    }

    public void updateRedRequested(String id, long redrequested) {
//        for(Alert a: alertsMap){
//            Log.e("f", id + " - " + a.getId());
//            if (a.getId().equals(id)){
//                a.setRedAlertRequested(redrequested);
//                break;
//            }
//        }
//        notifyDataSetChanged();
    }

    public List<Alert> getAlerts() {
        return alertsList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_alert_level;
        TextView txt_hazard_name;
        TextView txt_num_of_people;
        TextView txt_red_requested;
        ImageView img_alert_colour;
        ImageView img_hazard_icon;
        ImageView img_alert_req;

        private ViewHolder(View itemView) {
            super(itemView);

            txt_alert_level = (TextView) itemView.findViewById(R.id.txt_alert_level);
            txt_hazard_name = (TextView) itemView.findViewById(R.id.txt_hazard_name);
            txt_num_of_people = (TextView) itemView.findViewById(R.id.txt_num_of_people);
            txt_red_requested = (TextView) itemView.findViewById(R.id.textViewAlertReq);
            img_alert_colour = (ImageView) itemView.findViewById(R.id.img_alert_colour);
            img_hazard_icon = (ImageView) itemView.findViewById(R.id.img_hazard_icon);
            img_alert_req = (ImageView) itemView.findViewById(R.id.imgRedReq);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null) {
                        listener.onAlertItemClicked(alertsList.get(position));
                    }
                }
            });
        }

        private void bind(Alert alert) {

            for (int i = 0; i < Constants.HAZARD_SCENARIO_NAME.length; i++) {

                if (i == alert.getHazardScenario() && alert.getAlertLevel() == Constants.TRIGGER_RED) {
                    fetchIcon(Constants.HAZARD_SCENARIO_NAME[i], img_hazard_icon);
                    txt_alert_level.setText(R.string.red_alert_text);
                    img_alert_colour.setImageResource(R.drawable.red_alert_left);
                    txt_hazard_name.setText(Constants.HAZARD_SCENARIO_NAME[i]);
                    txt_num_of_people.setText(getNumOfPeopleText(alert.getPopulation(), alert.getNumOfAreas()));
                } else if (i == alert.getHazardScenario() && alert.getAlertLevel() == Constants.TRIGGER_AMBER) {
                    txt_alert_level.setText(R.string.amber_alert_text);
                    img_alert_colour.setImageResource(R.drawable.amber_alert_left);
                    txt_hazard_name.setText(Constants.HAZARD_SCENARIO_NAME[i]);
                    txt_num_of_people.setText(getNumOfPeopleText(alert.getPopulation(), alert.getNumOfAreas()));
                    fetchIcon(Constants.HAZARD_SCENARIO_NAME[i], img_hazard_icon);
                } else if (alert.getOtherName() != null && alert.getAlertLevel() == Constants.TRIGGER_RED) {
                    txt_alert_level.setText(R.string.red_alert_text);
                    img_alert_colour.setImageResource(R.drawable.red_alert_left);
                    img_hazard_icon.setImageResource(R.drawable.other);
                    txt_hazard_name.setText(alert.getOtherName());
                    txt_num_of_people.setText(getNumOfPeopleText(alert.getPopulation(), alert.getNumOfAreas()));
                } else if (alert.getOtherName() != null && alert.getAlertLevel() == Constants.TRIGGER_AMBER) {
                    txt_alert_level.setText(R.string.amber_alert_text);
                    img_alert_colour.setImageResource(R.drawable.amber_alert_left);
                    img_hazard_icon.setImageResource(R.drawable.other);
                    txt_hazard_name.setText(alert.getOtherName());
                    txt_num_of_people.setText(getNumOfPeopleText(alert.getPopulation(), alert.getNumOfAreas()));
                }
            }

            if(isCountryDirector && alert.getRedAlertRequested() == 0) {
                img_alert_req.setVisibility(View.VISIBLE);
                img_alert_colour.setImageResource(R.drawable.gray_alert_left);
                txt_red_requested.setText(R.string.txt_cd_red_request);
            }else if(alert.getRedAlertRequested() == 0 && !isCountryDirector) {
                img_alert_req.setVisibility(View.VISIBLE);
                img_alert_colour.setImageResource(R.drawable.gray_alert_left);
                txt_red_requested.setText(R.string.txt_red_requested);
            }else {
                img_alert_req.setVisibility(View.GONE);
            }
        }
    }

    public static void fetchIcon(String hazardName, ImageView imageView) {
        imageView.setImageResource(ExtensionHelperKt.getHazardImg(hazardName));
    }

    private String getNumOfPeopleText(long population, long numOfAreas) {
        return population + " people affected in " + numOfAreas + " area";
    }



}
