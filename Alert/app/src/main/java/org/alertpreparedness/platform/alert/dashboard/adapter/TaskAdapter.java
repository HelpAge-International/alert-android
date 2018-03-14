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
import org.alertpreparedness.platform.alert.dashboard.model.Task;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.helper.DateHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.OnClick;

/**
 * Created by faizmohideen on 13/11/2017.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> implements Comparable<Task> {
    private HashMap<String, Task> items = new HashMap<>();
    private String dateFormat = "MMM dd,yyyy";
    private SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.getDefault());
    private List<String> keys = new ArrayList<>();

    @Inject
    User user;
    private TaskSelectListener listener;

    public TaskAdapter(TaskSelectListener listener) {
        this.listener = listener;
        DependencyInjector.applicationComponent().inject(this);
    }

    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_my_tasks, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskAdapter.ViewHolder holder, int position) {
        Task task = items.get(keys.get(position));
        holder.bind(task);
    }

    @Override
    public int compareTo(@NonNull Task task) {
        int dueDate = (int) task.getDueDate();
        return Integer.compare(dueDate, dueDate);
    }

    public void add(String key, Task task) {
        items.put(key, task);
        int index = keys.indexOf(key);
        if(index == -1) {
            keys.add(key);
            notifyItemInserted(keys.size()-1);
        }
        else {
            notifyItemChanged(index);
        }
    }

    public void tryRemove(String key) {
        int index = keys.indexOf(key);
        if(index != -1) {
            items.remove(keys.get(index));
            keys.remove(index);
            notifyItemRemoved(index);
        }
    }

    public void updateKeys(ArrayList<String> keys) {
        for(String newKey : keys) {
            tryRemove(newKey);
        }
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

        private void bind(Task task) {
            //System.out.println("task.getActionType() = " + task.getActionType());
            itemView.setOnClickListener((v) -> listener.onTaskSelected(keys.get(getAdapterPosition()), items.get(keys.get(getAdapterPosition()))));
            if (task.getTaskType().equals("action") && DateHelper.isDueToday(task.dueDate)) {
                txt_taskName.setText(task.getTaskName());
                txt_taskStatus.setText(dueTodayString(task.getAlertLevel(), "action"));
                img_task.setImageResource(R.drawable.home_task_red);
            }
            else if (task.getTaskType().equals("indicator") && DateHelper.isDueToday(task.dueDate)) {
                txt_taskName.setText(task.getTaskName());
                txt_taskStatus.setText(dueTodayString(task.getAlertLevel(), "indicator"));
                img_task.setImageResource(R.drawable.home_task_red);
            }
            else if (task.getTaskType().equals("action") && DateHelper.isDueInWeek(task.dueDate)) {
                txt_taskName.setText(task.getTaskName());
                txt_taskStatus.setText(dueWeekString(task.getAlertLevel(), "action"));
                img_task.setImageResource(R.drawable.home_task_amber);
            }
            else if (task.getTaskType().equals("indicator") && DateHelper.isDueInWeek(task.dueDate)) {
                txt_taskName.setText(task.getTaskName());
                txt_taskStatus.setText(dueWeekString(task.getAlertLevel(), "indicator"));
                img_task.setImageResource(R.drawable.home_task_amber);
            }
            else if (task.getTaskType().equals("action") && DateHelper.itWasDue(task.dueDate)) {
                txt_taskName.setText(task.getTaskName());
                txt_taskStatus.setText(dueBeforeString(task.getAlertLevel(), "action", format.format(new Date(task.dueDate))));
                img_task.setImageResource(R.drawable.home_task_red);
            }
            else if (task.getTaskType().equals("indicator") && DateHelper.itWasDue(task.dueDate)) {
                txt_taskName.setText(task.getTaskName());
                txt_taskStatus.setText(dueBeforeString(task.getAlertLevel(), "indicator", format.format(new Date(task.dueDate))));
                img_task.setImageResource(R.drawable.home_task_red);
            }
            else {
                txt_taskName.setText(task.getTaskName());
                txt_taskStatus.setText(dueBeforeString(task.getAlertLevel(), "indicator", format.format(new Date(task.dueDate))));
                img_task.setImageResource(R.drawable.home_task_blue);
//                txt_taskName.setVisibility(View.GONE);
//                txt_taskStatus.setVisibility(View.GONE);
//                img_task.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return keys.size();
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

    public interface TaskSelectListener {
        void onTaskSelected(String id, Task task);
    }

}