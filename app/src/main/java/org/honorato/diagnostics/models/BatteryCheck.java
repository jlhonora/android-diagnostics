package org.honorato.diagnostics.models;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import org.honorato.diagnostics.R;

/**
 * Created by jlh on 11/29/15.
 */
public class BatteryCheck extends Check {

    public BatteryCheck(Context context) {
        super(context);
        setTitle(R.string.battery_title);
    }

    @Override
    public void performCheck() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, filter);

        if (batteryStatus == null) {
            setStatus(STATUS_OK, "");
            return;
        }

        // Are we charging / charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        // How are we charging?
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float)scale;
        int statusCheck = STATUS_OK;
        String statusString = "";
        if (isCharging) {
            statusString = context.getString(R.string.battery_charging) + " (" + (usbCharge ? "USB" : "AC") + ") ";
        } else {
            float threshError = 0.15f;
            float threshWarning = 0.30f;
            if (batteryPct <= threshError) {
                statusCheck = STATUS_ERROR;
            } else if (batteryPct <= threshWarning) {
                statusCheck = STATUS_WARNING;
            } else {
                statusCheck = STATUS_OK;
            }
        }
        statusString += String.format("%d%%", (int) (batteryPct * 100));
        setStatus(statusCheck, statusString);
    }
}
