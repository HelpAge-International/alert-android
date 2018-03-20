package org.alertpreparedness.platform.alert.dashboard.adapter;

import android.content.Context;
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
import org.alertpreparedness.platform.alert.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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

    @Inject
    Context context;

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

    public void updateKeys(ArrayList<String> newKeys) {
        ArrayList<String> itemsToRemove = new ArrayList<>();
        for(String oldKey : this.keys) {
            if(!newKeys.contains(oldKey)) {
                itemsToRemove.add(oldKey);
            }
        }

        for (String key : itemsToRemove) {
            int index = this.keys.indexOf(key);
            String oldKey = this.keys.get(index);
            Task task = this.items.get(oldKey);
            if(task.getTaskType().equals(Task.TASK_ACTION)) {
                this.items.remove(oldKey);
                this.keys.remove(index);
                notifyItemRemoved(index);
            }
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
            itemView.setOnClickListener((v) -> listener.onTaskSelected(keys.get(getAdapterPosition()), items.get(keys.get(getAdapterPosition()))));
            if (task.getTaskType().equals("action") && DateHelper.isDueToday(task.dueDate)) {
                txt_taskName.setText(task.getTaskName());
                txt_taskStatus.setText(dueTodayString(task.getAlertLevel(), task));
                img_task.setImageResource(R.drawable.home_task_red);
            }
            else if (task.getTaskType().equals("indicator") && DateHelper.isDueToday(task.dueDate)) {
                txt_taskName.setText(task.getTaskName());
                txt_taskStatus.setText(dueTodayString(task.getAlertLevel(), task));
                img_task.setImageResource(R.drawable.home_task_red);
            }
            else if (task.getTaskType().equals("action") && DateHelper.isDueInWeek(task.dueDate)) {
                txt_taskName.setText(task.getTaskName());
                txt_taskStatus.setText(dueWeekString(task.getAlertLevel(), task));
                img_task.setImageResource(R.drawable.home_task_amber);
            }
            else if (task.getTaskType().equals("indicator") && DateHelper.isDueInWeek(task.dueDate)) {
                txt_taskName.setText(task.getTaskName());
                txt_taskStatus.setText(dueWeekString(task.getAlertLevel(), task));
                img_task.setImageResource(R.drawable.home_task_amber);
            }
            else if (task.getTaskType().equals("action") && DateHelper.itWasDue(task.dueDate)) {
                txt_taskName.setText(task.getTaskName());
                txt_taskStatus.setText(dueBeforeString(task.getAlertLevel(), task, format.format(new Date(task.dueDate))));
                img_task.setImageResource(R.drawable.home_task_red);
            }
            else if (task.getTaskType().equals("indicator") && DateHelper.itWasDue(task.dueDate)) {
                txt_taskName.setText(task.getTaskName());
                txt_taskStatus.setText(dueBeforeString(task.getAlertLevel(), task, format.format(new Date(task.dueDate))));
                img_task.setImageResource(R.drawable.home_task_red);
            }
            else {
                txt_taskName.setText(task.getTaskName());
                txt_taskStatus.setText(dueBeforeString(task.getAlertLevel(), task, format.format(new Date(task.dueDate))));
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

    private String dueTodayString(int level, Task task) {
        if (task.getTaskType().equals(Task.TASK_ACTION)) {
            return String.format(
                    context.getString(R.string.an_actoin_needs_to_be_completed),
                    (task.getActionType() == Constants.MPA ? context.getString(R.string.minimum) : context.getString(R.string.advanced))
            );
        }
        else {
            return "A " + getLevelAsString(level) + " " + task.getTaskType() + context.getString(R.string.needs_completing_today);
        }
    }

    private String dueWeekString(int level, Task task) {
        if (task.getTaskType().equals(Task.TASK_ACTION)) {
            return String.format(
                    context.getString(R.string.needs_completing_this_week),
                    (task.getActionType() == Constants.MPA ? context.getString(R.string.minimum) : context.getString(R.string.advanced))
            );
        } else {
            return "A " + getLevelAsString(level)  + " " + task.getTaskType() + " needs to be completed this week";
        }
    }

    private String dueBeforeString(int level, Task task, String date) {
        if (task.getTaskType().equals(Task.TASK_ACTION)) {
            return String.format(
                    context.getString(R.string.was_due_on),
                    (task.getActionType() == Constants.MPA ? context.getString(R.string.minimum) : context.getString(R.string.advanced)),
                    date
            );
        } else {
            return "A " + getLevelAsString(level)  + " " + task.getTaskType() + " was due on" + setBoldText(date);
        }
    }

    public interface TaskSelectListener {
        void onTaskSelected(String id, Task task);
    }

}