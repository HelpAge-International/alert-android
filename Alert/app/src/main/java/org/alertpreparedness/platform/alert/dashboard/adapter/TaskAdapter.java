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
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.UserRef;
import org.alertpreparedness.platform.alert.dashboard.model.Tasks;
import org.alertpreparedness.platform.alert.firebase.ActionModel;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.helper.DateHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

/**
 * Created by faizmohideen on 13/11/2017.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> implements Comparable<Tasks> {
    private List<Tasks> listArray;
    private String dateFormat = "MMM dd,yyyy";
    private SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.getDefault());

    @Inject
    User user;

    public TaskAdapter(List<Tasks> List) {
        this.listArray = List;
        DependencyInjector.applicationComponent().inject(this);
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

    public void add(Tasks task) {
        listArray.add(task);
        Collections.sort(listArray, (o1, o2) -> Long.compare(o1.getDueDate(), o2.getDueDate()));
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
            if (tasks.getTaskType().equals("action") && DateHelper.isDueToday(tasks.dueDate)) {
                txt_taskName.setText(tasks.getTaskName());
                txt_taskStatus.setText(dueTodayString(tasks.getAlertLevel(), "action"));
                img_task.setImageResource(R.drawable.home_task_red);
            }
            else if (tasks.getTaskType().equals("indicator") && DateHelper.isDueToday(tasks.dueDate)) {
                txt_taskName.setText(tasks.getTaskName());
                txt_taskStatus.setText(dueTodayString(tasks.getAlertLevel(), "indicator"));
                img_task.setImageResource(R.drawable.home_task_red);
            }
            else if (tasks.getTaskType().equals("action") && DateHelper.isDueInWeek(tasks.dueDate)) {
                txt_taskName.setText(tasks.getTaskName());
                txt_taskStatus.setText(dueWeekString(tasks.getAlertLevel(), "action"));
                img_task.setImageResource(R.drawable.home_task_amber);
            }
            else if (tasks.getTaskType().equals("indicator") && DateHelper.isDueInWeek(tasks.dueDate)) {
                txt_taskName.setText(tasks.getTaskName());
                txt_taskStatus.setText(dueWeekString(tasks.getAlertLevel(), "indicator"));
                img_task.setImageResource(R.drawable.home_task_amber);
            }
            else if (tasks.getTaskType().equals("action") && DateHelper.itWasDue(tasks.dueDate)) {
                txt_taskName.setText(tasks.getTaskName());
                txt_taskStatus.setText(dueBeforeString(tasks.getAlertLevel(), "action", format.format(new Date(tasks.dueDate))));
                img_task.setImageResource(R.drawable.home_task_red);
            }
            else if (tasks.getTaskType().equals("indicator") && DateHelper.itWasDue(tasks.dueDate)) {
                txt_taskName.setText(tasks.getTaskName());
                txt_taskStatus.setText(dueBeforeString(tasks.getAlertLevel(), "indicator", format.format(new Date(tasks.dueDate))));
                img_task.setImageResource(R.drawable.home_task_red);
            }
            else {
                txt_taskName.setText(tasks.getTaskName());
                txt_taskStatus.setText(dueBeforeString(tasks.getAlertLevel(), "indicator", format.format(new Date(tasks.dueDate))));
                img_task.setImageResource(R.drawable.home_task_red);
//                txt_taskName.setVisibility(View.GONE);
//                txt_taskStatus.setVisibility(View.GONE);
//                img_task.setVisibility(View.GONE);
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

}