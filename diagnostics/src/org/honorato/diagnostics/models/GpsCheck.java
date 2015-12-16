package org.honorato.diagnostics.models;

import android.content.Context;
import android.location.LocationManager;

import org.honorato.diagnostics.BuildConfig;
import org.honorato.diagnostics.R;

/**
 * Created by jlh on 11/26/15.
 */
public class GpsCheck extends Check {

    public GpsCheck(Context context) {
        super(context);
        setTitle(R.string.gps_title);
    }

    @Override
    protected void performCheck() {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        int status = STATUS_WARNING;
        int description = R.string.gps_disabled;

        try {
            if (manager != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                status = STATUS_OK;
                description = R.string.gps_ok;
            }
        } catch (IllegalArgumentException exc) {
            if (BuildConfig.DEBUG) {
                exc.printStackTrace();
            }
        }

        setStatus(status, context.getString(description));
    }
}
