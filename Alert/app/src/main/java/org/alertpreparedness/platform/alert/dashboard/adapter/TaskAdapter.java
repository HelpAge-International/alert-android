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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by faizmohideen on 13/11/2017.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    List<Tasks> listArray;
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
        //holder.img_task.setBackgroundResource(R.drawable.home_task_red);
        //DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
        //Date date = new Date();
        //System.out.println(dateFormat.format(date));

        //System.out.println("Is due today: " + isDueToday(tasks.getDueDate()));

holder.bind(tasks);
    }

    public void add(Tasks tasks) {
        listArray.add(tasks);
        Collections.sort(listArray, new Comparator<Tasks>() {
            @Override
            public int compare(Tasks o1, Tasks o2) {
                System.out.println(o1.getDueDate() + " " + o2.getDueDate());
                if (o1.getDueDate() > o2.getDueDate()) return -1;
                if (o1.getDueDate() < o2.getDueDate()) return 1;
                return 0;
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
            txt_taskName.setText(tasks.getTaskName()); if (tasks.getTaskType().equals("action")) {
                txt_taskStatus.setText(getTaskStatusString("red", "action"));
            } else if (tasks.getTaskType().equals("indicator")) {
                txt_taskStatus.setText(getTaskStatusString("red", "indicator"));
            }
            img_task.setImageResource(isDueToday(tasks.getDueDate()) ? R.drawable.home_task_red : R.drawable.home_task_amber);
        }
    }

    @Override
    public int getItemCount() {
        return listArray.size();
    }

    public static String getTaskStatusString(String level, String type) {
        return "A " + level + " " + type + " needs to be completed today";
    }

    public  boolean isDueToday(long milliSeconds) {
        Calendar today = Calendar.getInstance();
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(milliSeconds);
        System.out.println(format.format(new Date(milliSeconds)));
        return  today.get(Calendar.DAY_OF_YEAR) - 2 == date.get(Calendar.DAY_OF_YEAR);
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.getDefault());

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
