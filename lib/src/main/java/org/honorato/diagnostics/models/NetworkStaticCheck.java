package org.honorato.diagnostics.models;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import org.honorato.diagnostics.R;

/**
 * Created by jlh on 11/27/15.
 */
public class NetworkStaticCheck extends Check {

    protected ConnectivityManager connectivityManager;

    public NetworkStaticCheck(Context context) {
        super(context);
        setTitle(R.string.static_network_title);
    }

    @Override
    protected void performCheck() {
        connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= 21) {
            performMultiNetworkCheck();
        } else {
            performNetworkCheck();
        }
        boolean isConnected = performWifiCheck();
        if (isConnected) {
            setStatus(STATUS_OK, this.context.getString(R.string.static_network_ok) + ": " + "WiFi");
        } else {
            //performCellCheck();
        }
    }

    @TargetApi(21)
    protected void performMultiNetworkCheck() {
        Network[] networks = connectivityManager.getAllNetworks();
        if (networks.length == 0) {
            setStatus(STATUS_ERROR, R.string.static_network_no_connection);
            return;
        }

        for (Network nw : networks) {
            NetworkCapabilities nwCapabilities = connectivityManager.getNetworkCapabilities(nw);
            if (nwCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) ||
                    nwCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)) {
                setStatus(STATUS_OK, R.string.static_network_ok);
                return;
            }
        }
        setStatus(STATUS_WARNING, R.string.static_network_warning);
    }

    protected void performNetworkCheck() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            setStatus(STATUS_OK, R.string.static_network_ok);
        } else {
            setStatus(STATUS_ERROR, R.string.static_network_no_connection);
        }
    }

    protected boolean performWifiCheck() {
        NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }
}
