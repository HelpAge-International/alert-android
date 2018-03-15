package org.alertpreparedness.platform.alert.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.firebase.ActionModel;
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
    public boolean checkMPAActionAssign(ActionModel item , Activity context) {
        if(item.isChs() && !permissions.canAssignCHS()) {
            SnackbarHelper.show(context, context.getString(R.string.permission_assign_action_error));
            return false;
        }
        else if(item.getType() == 1 && !permissions.canAssignMPA()) {//mandated
            SnackbarHelper.show(context, context.getString(R.string.permission_assign_action_error));
            return false;
        }
        else if(item.getType() == 2 && !permissions.canAssignCustomMPA()) {
            SnackbarHelper.show(context, context.getString(R.string.permission_assign_action_error));
            return false;
        }
        return true;
    }

    /**
     * @return false if user does not have permission
     */
    public boolean checkCompleteMPAAction(ActionModel  item, Activity context) {
        if(item.getType() == 0 && !permissions.canCompleteCHS()) {
            SnackbarHelper.show(context, context.getString(R.string.permission_complete_error));
            return false;
        }
        else if(item.getType() == 1 && !permissions.canCompleteMPA()) {//mandated
            SnackbarHelper.show(context, context.getString(R.string.permission_complete_error));
            return false;
        }
        else if(item.getType() == 2 && !permissions.canCompleteCustomMPA()) {
            SnackbarHelper.show(context, context.getString(R.string.permission_complete_error));
            return false;
        }
        return true;
    }

    public boolean checkCompleteAPAAction(ActionModel  item, Activity context) {
        if(item.getType() == 1 && !permissions.canCompleteMandatedAPA()) {
            SnackbarHelper.show(context, context.getString(R.string.permission_complete_error));
            return false;
        }
        else if(item.getType() == 2 && !permissions.canCompleteCustomAPA()) {
            SnackbarHelper.show(context, context.getString(R.string.permission_complete_error));
            return false;
        }
        return true;
    }

    public boolean checkCreateAPA(Activity context) {
        return permissions.canCreateCustomAPA();
    }

    public boolean checkAssignAPA(ActionModel  item, Activity context) {
        if(item.getType() == 1 && !permissions.canAssignMandatedAPA()) {//mandated
            SnackbarHelper.show(context, context.getString(R.string.permission_assign_action_error));
            return false;
        }
        else if(item.getType() == 2 && !permissions.canAssignCustomAPA()) {
            SnackbarHelper.show(context, context.getString(R.string.permission_assign_action_error));
            return false;
        }
        return true;
    }

    public boolean checkEditAPA(ActionModel  item, Activity context) {
        if(item.getType() == 1 && !permissions.canEditMandatedAPA()) {
            SnackbarHelper.show(context, context.getString(R.string.permission_edit_error));
            return false;
        }
        else if(item.getType() == 2 && !permissions.canEditCustomAPA()) {
            SnackbarHelper.show(context, context.getString(R.string.permission_edit_error));
            return false;
        }
        return true;
    }

    public boolean checkCanViewAPA(ActionModel  item) {
        if(item.getType() == 1 && !permissions.canViewMandatedAPA()) {
            return false;
        }
        else if(item.getType() == 2 && !permissions.canViewCustomAPA()) {
            return false;
        }
        return true;
    }


    public boolean checkCanViewMPA(ActionModel  item) {
        if(item.getType() == 0 && !permissions.canViewCHS()) {
            return false;
        }
        else if(item.getType() == 1 && !permissions.canViewMPA()) {
            return false;
        }
        else if(item.getType() == 2 && !permissions.canViewCustomMPA()) {
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
