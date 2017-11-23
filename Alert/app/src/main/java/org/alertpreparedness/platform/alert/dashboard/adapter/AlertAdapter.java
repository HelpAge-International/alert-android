package org.alertpreparedness.platform.alert.dashboard.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dashboard.activity.AlertDetailActivity;
import org.alertpreparedness.platform.alert.dashboard.activity.HomeScreen;
import org.alertpreparedness.platform.alert.helper.OnAlertItemClickedListener;
import org.alertpreparedness.platform.alert.model.Alert;
import org.alertpreparedness.platform.alert.model.Tasks;
import org.alertpreparedness.platform.alert.utils.Constants;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by faizmohideen on 20/11/2017.
 */

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.ViewHolder> {

    private Context context;
    private OnAlertItemClickedListener listener;
    public List<Alert> getAlertList() {
        return listArray;
    }

    public static List<Alert> listArray;
    private static WeakReference<HomeScreen> mActivityRef;
    private static AlertAdapter instance = new AlertAdapter();
    public static AlertAdapter getInstance() {
        return instance;
    }
    private final static String _TAG = "Adapter";

    public AlertAdapter(List<Alert> List) {
        super();

        this.listArray = List;
        this.context  = mActivityRef.get();
        if (context instanceof OnAlertItemClickedListener) {
            listener = (OnAlertItemClickedListener) context;
        } else {
            Log.e(_TAG, "Activity does not support OnAlertListListener interface");
        }
    }

    public AlertAdapter() {
        // Required empty public constructor
    }

    @Override
    public AlertAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_alert, parent, false);
        return new AlertAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlertAdapter.ViewHolder holder, int position) {
        Alert alert = listArray.get(position);
        holder.bind(alert);
    }

    public void add(Alert alert) {
        listArray.add(alert);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_alert_level;
        TextView txt_hazard_name;
        TextView txt_num_of_people;
        ImageView img_alert_colour;
        ImageView img_hazard_icon;

        public ViewHolder(View itemView) {
            super(itemView);

            txt_alert_level = (TextView) itemView.findViewById(R.id.txt_alert_level);
            txt_hazard_name = (TextView) itemView.findViewById(R.id.txt_hazard_name);
            txt_num_of_people = (TextView) itemView.findViewById(R.id.txt_num_of_people);
            img_alert_colour = (ImageView) itemView.findViewById(R.id.img_alert_colour);
            img_hazard_icon = (ImageView) itemView.findViewById(R.id.img_hazard_icon);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    int position = getLayoutPosition();
                    if (listener != null) {
                        listener.onAlertItemClicked(position);
                    }
                }
            });
        }

        private void bind(Alert alert) {
            for (int i = 0; i < Constants.HAZARD_SCENARIO_NAME.length; i++) {
                if (i == alert.getHazardScenario() && alert.getAlertLevel() == Constants.TRIGGER_RED) {
                    fetchIcon(Constants.HAZARD_SCENARIO_NAME[i]);
                    txt_alert_level.setText(R.string.red_alert_text);
                    img_alert_colour.setImageResource(R.drawable.red_alert_left);
                    txt_hazard_name.setText(Constants.HAZARD_SCENARIO_NAME[i]);
                    txt_num_of_people.setText(getNumOfPeopleText(alert.getPopulation(), alert.getNumOfAreas()));
                } else if (i == alert.getHazardScenario() && alert.getAlertLevel() == Constants.TRIGGER_AMBER) {
                    txt_alert_level.setText(R.string.amber_alert_text);
                    img_alert_colour.setImageResource(R.drawable.amber_alert_left);
                    txt_hazard_name.setText(Constants.HAZARD_SCENARIO_NAME[i]);
                    txt_num_of_people.setText(getNumOfPeopleText(alert.getPopulation(), alert.getNumOfAreas()));
                    fetchIcon(Constants.HAZARD_SCENARIO_NAME[i]);
                } else if (alert.getOtherName() != null) {
                    img_hazard_icon.setImageResource(R.drawable.other);
                    txt_hazard_name.setText(alert.getOtherName());
                    txt_num_of_people.setText(getNumOfPeopleText(alert.getPopulation(), alert.getNumOfAreas()));
                }
            }
        }

        public void fetchIcon(String hazardName) {
            if (hazardName.equals("Cold Wave")) {
                img_hazard_icon.setImageResource(R.drawable.cold_wave);
            }else if (hazardName.equals("Conflict")) {
                img_hazard_icon.setImageResource(R.drawable.conflict);
            }else if (hazardName.equals("Cyclone")) {
                img_hazard_icon.setImageResource(R.drawable.cyclone);
            }else if (hazardName.equals("Drought")) {
                img_hazard_icon.setImageResource(R.drawable.drought);
            }else if (hazardName.equals("Earthquake")) {
                img_hazard_icon.setImageResource(R.drawable.earthquake);
            }else if (hazardName.equals("Epidemic")) {
                img_hazard_icon.setImageResource(R.drawable.epidemic);
            }else if (hazardName.equals("Fire")) {
                img_hazard_icon.setImageResource(R.drawable.fire);
            }else if (hazardName.equals("Flash Flood")) {
                img_hazard_icon.setImageResource(R.drawable.flash_flood);
            }else if (hazardName.equals("Heat Wave")) {
                img_hazard_icon.setImageResource(R.drawable.heat_wave);
            }else if (hazardName.equals("Humanitarian Access")) {
                img_hazard_icon.setImageResource(R.drawable.humanitarian_access);
            }else if (hazardName.equals("Insect Infestation")) {
                img_hazard_icon.setImageResource(R.drawable.insect_infestation);
            }else if (hazardName.equals("Landslide") || hazardName.equals("Mudslide") ) {
                img_hazard_icon.setImageResource(R.drawable.landslide_mudslide);
            }else if (hazardName.equals("Locust Infestation")) {
                img_hazard_icon.setImageResource(R.drawable.locust_infestation);
            }else if (hazardName.equals("Population Displacement")) {
                img_hazard_icon.setImageResource(R.drawable.population_displacement);
            }else if (hazardName.equals("Population Return")) {
                img_hazard_icon.setImageResource(R.drawable.population_return);
            }else if (hazardName.equals("Snow Avalanche")) {
                img_hazard_icon.setImageResource(R.drawable.snow_avalanche);
            }else if (hazardName.equals("Snowfall")) {
                img_hazard_icon.setImageResource(R.drawable.snowfall);
            }else if (hazardName.equals("Storm")) {
                img_hazard_icon.setImageResource(R.drawable.storm);
            }else if (hazardName.equals("Storm Surge")) {
                img_hazard_icon.setImageResource(R.drawable.storm_surge);
            }else if (hazardName.equals("Technological Disaster")) {
                img_hazard_icon.setImageResource(R.drawable.technological_disaster);
            }else if (hazardName.equals("Tornado")) {
                img_hazard_icon.setImageResource(R.drawable.tornado);
            }else if (hazardName.equals("Tsunami")) {
                img_hazard_icon.setImageResource(R.drawable.tsunami);
            }else if (hazardName.equals("Violent Wind")) {
                img_hazard_icon.setImageResource(R.drawable.violent_wind);
            }else if (hazardName.equals("Volcano")) {
                img_hazard_icon.setImageResource(R.drawable.volcano);
            }
        }
    }

    private String getNumOfPeopleText(long population, long numOfAreas) {
        return population + " people affected in " + numOfAreas + " area";
    }

    public static void updateActivity(HomeScreen homeActivity) {
        mActivityRef = new WeakReference<HomeScreen>(homeActivity);
    }
}
