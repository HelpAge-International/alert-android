package org.alertpreparedness.platform.alert.dashboard.model;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.text.InputType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tj on 06/12/2017.
 */

public class AlertFieldModel {

    public int originalPosition = -1;
    public int viewType;
    @DrawableRes
    public int drawable;
    @StringRes
    public int initialTitle;
    public String currentTitle;
    public List<String> strings = new ArrayList<>();
    public String resultTitle;
    public int inputType = InputType.TYPE_CLASS_TEXT;

    public AlertFieldModel() {
    }

    @Override
    public String toString() {
        return "AlertFieldModel{" +
                "originalPosition=" + originalPosition +
                ", viewType=" + viewType +
                ", drawable=" + drawable +
                ", initialTitle=" + initialTitle +
                ", strings=" + strings +
                ", resultTitle='" + resultTitle + '\'' +
                ", inputType=" + inputType +
                '}';
    }

    public String toString(Context context) {
        return "AlertFieldModel{" +
                "originalPosition=" + originalPosition +
                ", viewType=" + viewType +
                ", drawable=" + drawable +
                ", initialTitle=" + context.getString(initialTitle) +
                ", strings=" + strings +
                ", resultTitle='" + resultTitle + '\'' +
                ", inputType=" + inputType +
                '}';
    }

    public AlertFieldModel(int viewType, int drawable, int title) {
        this.viewType = viewType;
        this.drawable = drawable;
        this.initialTitle = title;
    }

    public AlertFieldModel(int viewType, int drawable, int title, int inputType) {
        this.viewType = viewType;
        this.drawable = drawable;
        this.initialTitle = title;
        this.inputType = inputType;
    }

    public AlertFieldModel(int viewType, int drawable, int title, List<String> strings) {
        this.viewType = viewType;
        this.drawable = drawable;
        this.initialTitle = title;
        if(strings != null) {
            this.strings = strings;
        }
    }
}
