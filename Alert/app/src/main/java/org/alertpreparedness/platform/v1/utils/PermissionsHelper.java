package org.alertpreparedness.platform.v1.utils;

import android.app.Activity;
import androidx.fragment.app.FragmentActivity;
import org.alertpreparedness.platform.R;
import org.alertpreparedness.platform.v1.firebase.ActionModel;
import org.alertpreparedness.platform.v1.realm.SettingsRealm;
import org.alertpreparedness.platform.v2.models.Action;
import org.alertpreparedness.platform.v2.models.enums.ActionLevel;
import org.alertpreparedness.platform.v2.models.enums.ActionType;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Tj on 07/03/2018.
 */

@Deprecated
public class PermissionsHelper {

    private SettingsRealm permissions;

    public PermissionsHelper(String userId) {
        this.permissions = SettingsFactory.getSettings(userId);
    }

    public PermissionsHelper(SettingsRealm permissions) {
        this.permissions = permissions;
    }

    public boolean checkAssign(@NotNull final Action action) {
        if (action.getActionLevel() == ActionLevel.MPA) {
            return checkMPAActionAssign(action.getActionType());
        } else {
            return checkAssignAPA(action.getActionType());
        }
    }

    public boolean checkAssignAPA(ActionModel model, Activity context) {
        return checkAssignAPA(model.getType(), context);
    }

    public boolean checkAssignAPA(ActionType actionType) {
        return checkAssignAPA(actionType.getValue(), null);
    }

    public boolean checkCanViewAPA(ActionModel model) {
        return checkCanViewAPA(model.getType());
    }

    public boolean checkCanViewAPA(ActionType actionType) {
        return checkCanViewAPA(actionType.getValue());
    }

    public boolean checkCanViewMPA(ActionModel model) {
        return checkCanViewMPA(model.getType());
    }

    public boolean checkCanViewMPA(ActionType actionType) {
        return checkCanViewMPA(actionType.getValue());
    }

    public boolean checkCompleteAPAAction(ActionModel model, Activity context) {
        return checkCompleteAPAAction(model.getType(), context);
    }

    public boolean checkCompleteAPAAction(ActionType actionType) {
        return checkCompleteAPAAction(actionType.getValue(), null);
    }

    public boolean checkCompleteAction(@NotNull final Action action) {
        if (action.getActionLevel() == ActionLevel.MPA) {
            return checkCompleteMPAAction(action.getActionType());
        } else {
            return checkCompleteAPAAction(action.getActionType());
        }
    }

    /**
     * @return false if user does not have permission
     */
    public boolean checkCompleteMPAAction(ActionModel model, Activity context) {
        return checkCompleteMPAAction(model.getType(), context);
    }

    public boolean checkCreateAPA(Activity context) {
        return permissions.canCreateCustomAPA();
    }

    public boolean checkCompleteMPAAction(ActionType actionType) {
        return checkCompleteMPAAction(actionType.getValue(), null);
    }

    public boolean checkEditAPA(ActionModel model, Activity context) {
        return checkEditAPA(model.getType(), context);
    }

    public boolean checkEditAPA(ActionType actionType) {
        return checkEditAPA(actionType.getValue(), null);
    }

    public boolean checkEditIndicator(FragmentActivity activity) {
        if (!permissions.canEditHazardIndicators()) {
            if (activity != null) {
                SnackbarHelper.show(activity, activity.getString(R.string.permission_edit_indicator));
            }
            return false;
        }
        return true;
    }

    /**
     * @return false if user does not have permission
     */
    public boolean checkMPAActionAssign(ActionModel model, Activity context) {
        return checkMPAActionAssign(model.getType(), context);
    }

    public boolean checkMPAActionAssign(ActionType actionType) {
        return checkMPAActionAssign(actionType.getValue(), null);
    }

    private boolean checkAssignAPA(int actionType, Activity context) {
        if (actionType == ActionType.MANDATED.getValue() && !permissions.canAssignMandatedAPA()) {//mandated
            if (context != null) {
                SnackbarHelper.show(context, context.getString(R.string.permission_assign_action_error));
            }
            return false;
        } else if (actionType == ActionType.CUSTOM.getValue() && !permissions.canAssignCustomAPA()) {
            if (context != null) {
                SnackbarHelper.show(context, context.getString(R.string.permission_assign_action_error));
            }
            return false;
        }
        return true;
    }

    private boolean checkCanViewAPA(int actionType) {
        if (actionType == ActionType.MANDATED.getValue() && !permissions.canViewMandatedAPA()) {
            return false;
        } else {
            return actionType != ActionType.CUSTOM.getValue() || permissions.canViewCustomAPA();
        }
    }

    private boolean checkCanViewMPA(int actionType) {
        if (actionType == ActionType.CHS.getValue() && !permissions.canViewCHS()) {
            return false;
        } else if (actionType == ActionType.MANDATED.getValue() && !permissions.canViewMPA()) {
            return false;
        } else {
            return actionType != ActionType.CUSTOM.getValue() || permissions.canViewCustomMPA();
        }
    }

    private boolean checkCompleteAPAAction(int actionType, Activity context) {
        if (actionType == ActionType.MANDATED.getValue() && !permissions.canCompleteMandatedAPA()) {
            if (context != null) {
                SnackbarHelper.show(context, context.getString(R.string.permission_complete_error));
            }
            return false;
        } else if (actionType == ActionType.CUSTOM.getValue() && !permissions.canCompleteCustomAPA()) {
            if (context != null) {
                SnackbarHelper.show(context, context.getString(R.string.permission_complete_error));
            }
            return false;
        }
        return true;
    }

    private boolean checkCompleteMPAAction(int actionType, Activity context) {
        if (actionType == ActionType.CHS.getValue() && !permissions.canCompleteCHS()) {
            if (context != null) {
                SnackbarHelper.show(context, context.getString(R.string.permission_complete_error));
            }
            return false;
        } else if (actionType == ActionType.MANDATED.getValue() && !permissions.canCompleteMPA()) {//mandated
            if (context != null) {
                SnackbarHelper.show(context, context.getString(R.string.permission_complete_error));
            }
            return false;
        } else if (actionType == ActionType.CUSTOM.getValue() && !permissions.canCompleteCustomMPA()) {
            if (context != null) {
                SnackbarHelper.show(context, context.getString(R.string.permission_complete_error));
            }
            return false;
        }
        return true;
    }

    private boolean checkEditAPA(int actionType, Activity context) {
        if (actionType == ActionType.MANDATED.getValue() && !permissions.canEditMandatedAPA()) {
            if (context != null) {
                SnackbarHelper.show(context, context.getString(R.string.permission_edit_error));
            }
            return false;
        } else if (actionType == ActionType.CUSTOM.getValue() && !permissions.canEditCustomAPA()) {
            if (context != null) {
                SnackbarHelper.show(context, context.getString(R.string.permission_edit_error));
            }
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

    private boolean checkMPAActionAssign(int actionType, Activity context) {
        if (actionType == ActionType.CHS.getValue() && !permissions.canAssignCHS()) {
            if (context != null) {
                SnackbarHelper.show(context, context.getString(R.string.permission_assign_action_error));
            }
            return false;
        } else if (actionType == ActionType.MANDATED.getValue() && !permissions.canAssignMPA()) {//mandated
            if (context != null) {
                SnackbarHelper.show(context, context.getString(R.string.permission_assign_action_error));
            }
            return false;
        } else if (actionType == ActionType.CUSTOM.getValue() && !permissions.canAssignCustomMPA()) {
            if (context != null) {
                SnackbarHelper.show(context, context.getString(R.string.permission_assign_action_error));
            }
            return false;
        }
        return true;
    }
}
