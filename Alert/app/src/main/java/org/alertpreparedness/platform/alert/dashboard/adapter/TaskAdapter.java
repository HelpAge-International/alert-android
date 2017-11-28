package org.alertpreparedness.platform.alert.dashboard.adapter;

import android.support.annotation.NonNull;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by faizmohideen on 13/11/2017.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> implements Comparable<Tasks> {

    List<Tasks> listArray;
    Calendar today = Calendar.getInstance();
    Calendar date = Calendar.getInstance();

    public final static long MILLIS_PER_DAY = 24 * 60 * 60 * 1000L;
    public String dateFormat = "dd/MM/yyyy hh:mm:ss.SSS";
    private SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.getDefault());

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
        holder.bind(tasks);
    }

    @Override
    public int compareTo(@NonNull Tasks tasks) {
        int dueDate = (int) tasks.getDueDate();
        return Integer.compare(dueDate,dueDate);
    }

    public void add(Tasks tasks) {
        listArray.add(tasks);

        Collections.sort(listArray, new Comparator<Tasks>() {
            @Override
            public int compare(Tasks o1, Tasks o2) {
                // System.out.println(o1.getDueDate() + " " + o2.getDueDate());
//                if (o1.getDueDate() > o2.getDueDate()) return -1;
//                if (o1.getDueDate() < o2.getDueDate()) return 1;
                return Long.compare(o1.dueDate, o2.dueDate);
            }
        });
        notifyDataSetChanged();
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

        public void bind(Tasks tasks){
            txt_taskName.setText(tasks.getTaskName());
            if (tasks.getTaskType().equals("action") && isDueToday(tasks.dueDate)) {
                txt_taskStatus.setText(dueTodayString("red level", "preparedness action"));
                img_task.setImageResource(R.drawable.home_task_red);
            } else if (tasks.getTaskType().equals("indicator") && isDueToday(tasks.dueDate)) {
                txt_taskStatus.setText(dueTodayString("red level", "indicator"));
                img_task.setImageResource(R.drawable.home_task_red);
            } else if (tasks.getTaskType().equals("action") && isDueInWeek(tasks.dueDate)){
                txt_taskStatus.setText(dueWeekString("amber level", "preparedness action"));
                img_task.setImageResource(R.drawable.home_task_amber);
            } else if (tasks.getTaskType().equals("indicator") && isDueInWeek(tasks.dueDate)) {
                txt_taskStatus.setText(dueTodayString("amber level", "indicator"));
                img_task.setImageResource(R.drawable.home_task_amber);
            } else if (tasks.getTaskType().equals("action") && itWasDue(tasks.dueDate)) {
                txt_taskStatus.setText(dueBeforeString("red level", "preparedness action", format.format(new Date(tasks.dueDate))));
                img_task.setImageResource(R.drawable.home_task_red);
            } else if (tasks.getTaskType().equals("indicator") && itWasDue(tasks.dueDate)) {
                txt_taskStatus.setText(dueBeforeString("red level", "indicator", format.format(new Date(tasks.dueDate))));
                img_task.setImageResource(R.drawable.home_task_red);
            }
            // img_task.setImageResource(isDueToday(tasks.getDueDate()) ? R.drawable.home_task_red : R.drawable.home_task_amber);
        }
    }

    @Override
    public int getItemCount() {
        return listArray.size();
    }

    public static String dueTodayString(String level, String type) {
        return "A " + level + " " + type + "\n needs to be completed today";
    }

    public static String dueWeekString(String level, String type) {
        return "A " + level + " " + type + "\n needs to be completed this week";
    }

    public static String dueBeforeString(String level, String type, String date) {
        return "A " + level + " " + type + " was due on \n"+date;
    }

    public  boolean isDueToday(long milliSeconds) {
        date.setTimeInMillis(milliSeconds);
        return  today.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR);
    }

    public boolean isDueInWeek(long milliSeconds){
        date.setTimeInMillis(milliSeconds);
        return date.get(Calendar.DAY_OF_YEAR) > today.get(Calendar.DAY_OF_YEAR) &&  date.get(Calendar.DAY_OF_YEAR) < (today.get(Calendar.DAY_OF_YEAR)+7);
    }

    public boolean itWasDue(long milliSeconds){
        date.setTimeInMillis(milliSeconds);
        return date.get(Calendar.DAY_OF_YEAR) < (today.get(Calendar.DAY_OF_YEAR));
    }
}