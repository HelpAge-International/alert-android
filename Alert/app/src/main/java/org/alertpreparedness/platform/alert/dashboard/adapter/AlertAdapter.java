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

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dashboard.activity.HomeScreen;
import org.alertpreparedness.platform.alert.interfaces.OnAlertItemClickedListener;
import org.alertpreparedness.platform.alert.model.Alert;
import org.alertpreparedness.platform.alert.utils.Constants;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by faizmohideen on 20/11/2017.
 */

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.ViewHolder> {

    private Context context;
    private OnAlertItemClickedListener listener;
    private List<Alert> listArray;
    private static WeakReference<HomeScreen> mActivityRef;
    private final static String _TAG = "Adapter";

    public List<Alert> getAlertList() {
        return listArray;
    }

    public AlertAdapter(@NonNull List<Alert> List) {
        super();

        this.listArray = List;
        this.context = mActivityRef.get();
        if (context != null) {
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
        Collections.sort(this.listArray, new Comparator<Alert>() {
            @Override
            public int compare(Alert o1, Alert o2) {
                return Long.compare(o2.getAlertLevel(), o1.getAlertLevel());
            }
        });
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
        ImageView img_alert_req;

        private ViewHolder(View itemView) {
            super(itemView);

            txt_alert_level = (TextView) itemView.findViewById(R.id.txt_alert_level);
            txt_hazard_name = (TextView) itemView.findViewById(R.id.txt_hazard_name);
            txt_num_of_people = (TextView) itemView.findViewById(R.id.txt_num_of_people);
            img_alert_colour = (ImageView) itemView.findViewById(R.id.img_alert_colour);
            img_hazard_icon = (ImageView) itemView.findViewById(R.id.img_hazard_icon);
            img_alert_req = (ImageView) itemView.findViewById(R.id.imgRedReq);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null) {
                        listener.onAlertItemClicked(listArray.get(position));
                    }
                }
            });
        }

        private void bind(Alert alert) {
            img_alert_req.setVisibility(View.GONE);
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
        }
    }

    public static void fetchIcon(String hazardName, ImageView imageView) {
        switch (hazardName) {
            case "Cold Wave":
                imageView.setImageResource(R.drawable.cold_wave);
                break;
            case "Conflict":
                imageView.setImageResource(R.drawable.conflict);
                break;
            case "Cyclone":
                imageView.setImageResource(R.drawable.cyclone);
                break;
            case "Drought":
                imageView.setImageResource(R.drawable.drought);
                break;
            case "Earthquake":
                imageView.setImageResource(R.drawable.earthquake);
                break;
            case "Epidemic":
                imageView.setImageResource(R.drawable.epidemic);
                break;
            case "Fire":
                imageView.setImageResource(R.drawable.fire);
                break;
            case "Flash Flood":
                imageView.setImageResource(R.drawable.flash_flood);
                break;
            case "Heat Wave":
                imageView.setImageResource(R.drawable.heat_wave);
                break;
            case "Humanitarian Access":
                imageView.setImageResource(R.drawable.humanitarian_access);
                break;
            case "Insect Infestation":
                imageView.setImageResource(R.drawable.insect_infestation);
                break;
            case "Landslide":
            case "Mudslide":
                imageView.setImageResource(R.drawable.landslide_mudslide);
                break;
            case "Locust Infestation":
                imageView.setImageResource(R.drawable.locust_infestation);
                break;
            case "Population Displacement":
                imageView.setImageResource(R.drawable.population_displacement);
                break;
            case "Population Return":
                imageView.setImageResource(R.drawable.population_return);
                break;
            case "Snow Avalanche":
                imageView.setImageResource(R.drawable.snow_avalanche);
                break;
            case "Snowfall":
                imageView.setImageResource(R.drawable.snowfall);
                break;
            case "Storm":
                imageView.setImageResource(R.drawable.storm);
                break;
            case "Storm Surge":
                imageView.setImageResource(R.drawable.storm_surge);
                break;
            case "Technological Disaster":
                imageView.setImageResource(R.drawable.technological_disaster);
                break;
            case "Tornado":
                imageView.setImageResource(R.drawable.tornado);
                break;
            case "Tsunami":
                imageView.setImageResource(R.drawable.tsunami);
                break;
            case "Violent Wind":
                imageView.setImageResource(R.drawable.violent_wind);
                break;
            case "Volcano":
                imageView.setImageResource(R.drawable.volcano);
                break;
        }
    }

    private String getNumOfPeopleText(long population, long numOfAreas) {
        return population + " people affected in " + numOfAreas + " area";
    }

    public static void updateActivity(HomeScreen homeActivity) {
        mActivityRef = new WeakReference<HomeScreen>(homeActivity);
    }
}
