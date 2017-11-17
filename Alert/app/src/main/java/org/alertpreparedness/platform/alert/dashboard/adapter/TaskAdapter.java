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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by faizmohideen on 13/11/2017.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    List<Tasks> listArray;
    HomeScreen home = new HomeScreen();
    public final static long MILLIS_PER_DAY = 24 * 60 * 60 * 1000L;
    public String dateFormat = "dd/MM/yyyy hh:mm:ss.SSS";

    public TaskAdapter(List<Tasks> List) {
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
        //holder.img_task.setBackgroundResource(R.drawable.home_task_red);
        //DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
        //Date date = new Date();
        //System.out.println(dateFormat.format(date));

        //System.out.println("Is due today: " + isDueToday(tasks.getDueDate()));
        if (isDueToday(tasks.getDueDate())) {
            if (tasks.getTaskType().equals("action")) {
                this.listArray.get(position);
                holder.txt_taskStatus.setText(getTaskStatusString("red", "action"));
                holder.txt_taskName.setText(tasks.getTaskName());
            } else if (tasks.getTaskType().equals("indicator")) {
                holder.txt_taskStatus.setText(getTaskStatusString("red", "indicator"));
                holder.txt_taskName.setText(tasks.getTaskName());
            }
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_taskStatus;
        TextView txt_taskName;
        ImageView img_task;

        public ViewHolder(View itemView) {
            super(itemView);
            img_task = (ImageView) itemView.findViewById(R.id.img_task);
            txt_taskStatus = (TextView) itemView.findViewById(R.id.task_status);
            txt_taskName = (TextView) itemView.findViewById(R.id.task_name);
        }
    }

    @Override
    public int getItemCount() {
        return listArray.size();
    }

    public static String getTaskStatusString(String level, String type) {
        String taskStatusString = "A " + level + " " + type + " needs to be completed today";
        return taskStatusString;
    }

    public static boolean isDueToday(long milliSeconds) {
        long todaysDate = System.currentTimeMillis();
        long oneDayInMillis = todaysDate + MILLIS_PER_DAY;
        //System.out.println("Is " + milliSeconds + " > " + oneDayInMillis);
        boolean isDueToday =  milliSeconds < oneDayInMillis;
        return isDueToday;
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
