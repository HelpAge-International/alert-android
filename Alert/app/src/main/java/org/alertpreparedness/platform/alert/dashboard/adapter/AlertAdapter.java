package org.alertpreparedness.platform.alert.dashboard.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.model.Alert;
import org.alertpreparedness.platform.alert.model.Tasks;
import org.alertpreparedness.platform.alert.utils.Constants;

import java.util.List;

/**
 * Created by faizmohideen on 20/11/2017.
 */

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.ViewHolder> {

    List<Alert> listArray;
    public AlertAdapter(List<Alert> List) {
        this.listArray = List;
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

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txt_hazard_name;
        TextView txt_num_of_people;
        ImageView img_hazard_icon;

        public ViewHolder(View itemView) {
            super(itemView);

            img_hazard_icon = (ImageView) itemView.findViewById(R.id.img_hazard_icon);
            txt_hazard_name = (TextView) itemView.findViewById(R.id.txt_hazard_name);
            txt_num_of_people = (TextView) itemView.findViewById(R.id.txt_num_of_people);
        }

        public void bind(Alert alert) {
            for(int i = 0; i < Constants.HAZARD_SCENARIO_NAME.length; i++) {
                if(i==alert.getHazardScenario()) {
                    txt_hazard_name.setText(Constants.HAZARD_SCENARIO_NAME[i]);
                    System.out.println("Constants: "+Constants.HAZARD_SCENARIO_NAME[i]);
                    //txt_num_of_people.setText(getNumOfPeopleText(alert.getPopulation(), alert.getNumOfAreas()));
                }
            }
        }
    }

    private String getNumOfPeopleText(String population, int numOfAreas) {
        return population+" people affected in "+numOfAreas+" area";
    }
}
