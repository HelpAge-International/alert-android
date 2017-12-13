package org.alertpreparedness.platform.alert.dashboard.activity;

/**
 * Created by am2230 on 10/12/2017.
 */

interface AlertLevelListener {
    void onAlertLevelUpdated(long when, int newLevel, String reason);
}
