package org.honorato.diagnostics.models;

import android.app.ActivityManager;
import android.content.Context;

import org.honorato.diagnostics.R;

/**
 * Created by jlh on 11/26/15.
 */
public class MemoryCheck extends Check {

    public MemoryCheck(Context context) {
        super(context);
        this.title.set(context.getString(R.string.memory_title));
    }

    @Override
    protected void performCheck() {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        String memory = "" + (mi.availMem / 1048576L) + " MB";
        int status;
        int description;
        if (mi.lowMemory) {
            status = STATUS_WARNING;
            description = R.string.memory_low;
        } else {
            status = STATUS_OK;
            description = R.string.memory_ok;
        }
        this.status.set(status);
        this.description.set(String.format(context.getString(description), memory));
    }

}
