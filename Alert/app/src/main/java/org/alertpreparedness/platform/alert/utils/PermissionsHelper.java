package org.alertpreparedness.platform.alert.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.realm.SettingsRealm;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

/**
 * Created by Tj on 07/03/2018.
 */

public class PermissionsHelper {


    private SettingsRealm permissions;

    public PermissionsHelper(SettingsRealm permissions) {
        this.permissions = permissions;
    }

    /**
     * @return false if user does not have permission
     */
    public boolean checkMPAActionAssign(Action item , Activity context) {
        if(item.isCHS() && !permissions.canAssignCHS()) {
            SnackbarHelper.show(context, context.getString(R.string.permission_assign_action_error));
            return false;
        }
        else if(item.getActionType() == 1 && !permissions.canAssignMPA()) {//mandated
            SnackbarHelper.show(context, context.getString(R.string.permission_assign_action_error));
            return false;
        }
        else if(item.getActionType() == 2 && !permissions.canAssignCustomMPA()) {
            SnackbarHelper.show(context, context.getString(R.string.permission_assign_action_error));
            return false;
        }
        return true;
    }

    /**
     * @return false if user does not have permission
     */
    public boolean checkCompleteMPAAction(Action item, Activity context) {
        if(item.getActionType() == 0 && !permissions.canCompleteCHS()) {
            SnackbarHelper.show(context, context.getString(R.string.permission_complete_error));
            return false;
        }
        else if(item.getActionType() == 1 && !permissions.canCompleteMPA()) {//mandated
            SnackbarHelper.show(context, context.getString(R.string.permission_complete_error));
            return false;
        }
        else if(item.getActionType() == 2 && !permissions.canCompleteCustomMPA()) {
            SnackbarHelper.show(context, context.getString(R.string.permission_complete_error));
            return false;
        }
        return true;
    }

    public boolean checkCompleteAPAAction(Action item, Activity context) {
        if(item.getActionType() == 1 && !permissions.canCompleteMandatedAPA()) {
            SnackbarHelper.show(context, context.getString(R.string.permission_complete_error));
            return false;
        }
        else if(item.getActionType() == 2 && !permissions.canCompleteCustomAPA()) {
            SnackbarHelper.show(context, context.getString(R.string.permission_complete_error));
            return false;
        }
        return true;
    }

    public boolean checkCreateAPA(Activity context) {
        return permissions.canCreateCustomAPA();
    }

    public boolean checkAssignAPA(Action item, Activity context) {
        if(item.getActionType() == 1 && !permissions.canAssignMandatedAPA()) {//mandated
            SnackbarHelper.show(context, context.getString(R.string.permission_assign_action_error));
            return false;
        }
        else if(item.getActionType() == 2 && !permissions.canAssignCustomAPA()) {
            SnackbarHelper.show(context, context.getString(R.string.permission_assign_action_error));
            return false;
        }
        return true;
    }

    public boolean checkEditAPA(Action item, Activity context) {
        if(item.getActionType() == 1 && !permissions.canEditMandatedAPA()) {
            SnackbarHelper.show(context, context.getString(R.string.permission_edit_error));
            return false;
        }
        else if(item.getActionType() == 2 && !permissions.canEditCustomAPA()) {
            SnackbarHelper.show(context, context.getString(R.string.permission_edit_error));
            return false;
        }
        return true;
    }

    public boolean checkCanViewAPA(Action item) {
        if(item.getActionType() == 1 && !permissions.canViewMandatedAPA()) {
            return false;
        }
        else if(item.getActionType() == 2 && !permissions.canViewCustomAPA()) {
            return false;
        }
        return true;
    }


    public boolean checkCanViewMPA(Action item) {
        if(item.getActionType() == 0 && !permissions.canViewCHS()) {
            return false;
        }
        else if(item.getActionType() == 1 && !permissions.canViewMPA()) {
            return false;
        }
        else if(item.getActionType() == 2 && !permissions.canViewCustomMPA()) {
            return false;
        }
        return true;
    }

    public boolean checkCreateNote() {
        return permissions.canCreateNotes();
    }

    public boolean checkCreateIndicator() {
        return permissions.canCreateHazardIndicators();
    }

    public boolean checkEditIndicator(FragmentActivity activity) {
        if(!permissions.canEditHazardIndicators()) {
            SnackbarHelper.show(activity, activity.getString(R.string.permission_edit_indicator));
        }
        return true;
    }
}
