package org.alertpreparedness.platform.alert.dashboard.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dashboard.model.Tasks;

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
    private List<Tasks> listArray;
    private String dateFormat = "MMM dd,yyyy";
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
        return Integer.compare(dueDate, dueDate);
    }

    public void add(Tasks tasks) {
        listArray.add(tasks);
        Collections.sort(listArray, new Comparator<Tasks>() {
            @Override
            public int compare(Tasks o1, Tasks o2) {
                return Long.compare(o1.getDueDate(), o2.getDueDate());
            }
        });
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_taskStatus;
        TextView txt_taskName;
        ImageView img_task;

        private ViewHolder(View itemView) {
            super(itemView);
            img_task = (ImageView) itemView.findViewById(R.id.img_task);
            txt_taskStatus = (TextView) itemView.findViewById(R.id.task_status);
            txt_taskName = (TextView) itemView.findViewById(R.id.task_name);
        }

        private void bind(Tasks tasks) {
            if (tasks.getTaskType().equals("action") && isDueToday(tasks.dueDate)) {
                txt_taskName.setText(tasks.getTaskName());
                txt_taskStatus.setText(dueTodayString(tasks.getAlertLevel(), "action"));
                img_task.setImageResource(R.drawable.home_task_red);
            } else if (tasks.getTaskType().equals("indicator") && isDueToday(tasks.dueDate)) {
                txt_taskName.setText(tasks.getTaskName());
                txt_taskStatus.setText(dueTodayString(tasks.getAlertLevel(), "indicator"));
                img_task.setImageResource(R.drawable.home_task_red);
            } else if (tasks.getTaskType().equals("action") && isDueInWeek(tasks.dueDate)) {
                txt_taskName.setText(tasks.getTaskName());
                txt_taskStatus.setText(dueWeekString(tasks.getAlertLevel(), "action"));
                img_task.setImageResource(R.drawable.home_task_amber);
            } else if (tasks.getTaskType().equals("indicator") && isDueInWeek(tasks.dueDate)) {
                txt_taskName.setText(tasks.getTaskName());
                txt_taskStatus.setText(dueWeekString(tasks.getAlertLevel(), "indicator"));
                img_task.setImageResource(R.drawable.home_task_amber);
            } else if (tasks.getTaskType().equals("action") && itWasDue(tasks.dueDate)) {
                txt_taskName.setText(tasks.getTaskName());
                txt_taskStatus.setText(dueBeforeString(tasks.getAlertLevel(), "action", format.format(new Date(tasks.dueDate))));
                img_task.setImageResource(R.drawable.home_task_red);
            } else if (tasks.getTaskType().equals("indicator") && itWasDue(tasks.dueDate)) {
                txt_taskName.setText(tasks.getTaskName());
                txt_taskStatus.setText(dueBeforeString(tasks.getAlertLevel(), "indicator", format.format(new Date(tasks.dueDate))));
                img_task.setImageResource(R.drawable.home_task_red);
            }else {
                txt_taskName.setVisibility(View.GONE);
                txt_taskStatus.setVisibility(View.GONE);
                img_task.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return listArray.size();
    }

    private String getLevelAsString(int level){

        switch (level){
            case 0:
                return "green level";
            case 1:
                return "amber level";
            case 2:
                return "red level";
            default:
                return "";
        }

    }

    private SpannableStringBuilder setBoldText(String str) {
        SpannableStringBuilder sb = new SpannableStringBuilder(" "+str);
        StyleSpan b = new StyleSpan(android.graphics.Typeface.BOLD);
        sb.setSpan(b, 0, str.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return sb;
    }

    private String dueTodayString(int level, String type) {
        if (type.equals("action")) {
            return "A minimum preparedness action needs to be completed today";
        } else {
            return "A " + getLevelAsString(level) + " " + type + " needs to be completed today";
        }
    }

    private String dueWeekString(int level, String type) {
        if (type.equals("action")) {
            return "A minimum preparedness action needs to be completed this week";
        } else {
            return "A " + getLevelAsString(level)  + " " + type + " needs to be completed this week";
        }
    }

    private String dueBeforeString(int level, String type, String date) {
        if (type.equals("action")) {
            return "A minimum preparedness action was due on" + setBoldText(date);
        } else {
            return "A " + getLevelAsString(level)  + " " + type + " was due on" + setBoldText(date);
        }
    }

    public boolean isDueToday(long milliseconds) {
        Calendar today = Calendar.getInstance();
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(milliseconds);

        return today.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR);
    }

    public boolean isDueInWeek(long milliseconds) {
        Calendar oneWeekAhead = Calendar.getInstance();
        oneWeekAhead.add(Calendar.DAY_OF_YEAR, 8); // 8 to make it at the end of the day
        oneWeekAhead.set(Calendar.HOUR, 0);
        oneWeekAhead.set(Calendar.MINUTE, 0);
        Calendar now = Calendar.getInstance();

        return oneWeekAhead.getTimeInMillis() >= milliseconds && milliseconds >= now.getTimeInMillis();
    }

    private boolean itWasDue(long milliseconds) {
        Calendar dueDate = Calendar.getInstance();
        dueDate.setTimeInMillis(milliseconds);
        Calendar today = Calendar.getInstance();

        return dueDate.before(today);
    }

    public boolean isNotDue(long milliseconds) {
        Calendar oneWeekAhead = Calendar.getInstance();
        oneWeekAhead.add(Calendar.DAY_OF_YEAR, 8); // 8 to make it at the end of the day
        oneWeekAhead.set(Calendar.HOUR, 0);
        oneWeekAhead.set(Calendar.MINUTE, 0);

        return oneWeekAhead.after(new Date(milliseconds));
    }

}