package org.alertpreparedness.platform.alert.dashboard.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dashboard.activity.HomeScreen;
import org.alertpreparedness.platform.alert.model.Tasks;

import java.util.List;

/**
 * Created by faizmohideen on 13/11/2017.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    List<Tasks> listArray;
    HomeScreen home = new HomeScreen();

    public TaskAdapter(List<Tasks> List){
        this.listArray = List;
    }

    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_my_tasks, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskAdapter.ViewHolder holder, int position) {
        Tasks tasks = listArray.get(position);

        holder.img_task.setBackgroundResource(R.drawable.home_task_red);

        if(tasks.getTaskType().equals("action")) {
            holder.txt_taskStatus.setText(getTaskStatusString("red", "action"));
            holder.txt_taskName.setText(tasks.getTaskName());
        }else if(tasks.getTaskType().equals("indicator")){
            holder.txt_taskStatus.setText(getTaskStatusString("red", "indicator"));
            holder.txt_taskName.setText(tasks.getTaskName());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txt_taskStatus;
        TextView txt_taskName;
        ImageView img_task;

        public ViewHolder(View itemView) {
            super(itemView);
            img_task = (ImageView)itemView.findViewById(R.id.img_task);
            txt_taskStatus = (TextView)itemView.findViewById(R.id.task_status);
            txt_taskName = (TextView)itemView.findViewById(R.id.task_name);
        }
    }

    @Override
    public int getItemCount() {
        return listArray.size();
    }

    public static String getTaskStatusString(String level, String type){
        String taskStatusString = "A "+level+" "+type+" needs to be completed today";
        return taskStatusString;
    }
}
