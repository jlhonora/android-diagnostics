package org.honorato.diagnostics.models;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.format.DateUtils;

import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import com.facebook.network.connectionclass.DeviceBandwidthSampler;

import org.honorato.diagnostics.BuildConfig;
import org.honorato.diagnostics.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by jlh on 11/27/15.
 */
public class NetworkQualityCheck extends Check implements ConnectionClassManager.ConnectionClassStateChangeListener {

    public long DURATION_MILLIS = 3 * DateUtils.SECOND_IN_MILLIS;
    public long BYTES = 150000;
    public int MAX_TRIES = 8;
    protected int nTries = 0;

    DownloadBytes mTask;

    public NetworkQualityCheck(Context context) {
        super(context);
        setTitle(R.string.network_quality_title);
    }

    @Override
    protected void performCheck() {
        startDownload();
        startCheck();
    }

    protected void startDownload() {

    }

    protected void startCheck() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            updateConnectionQuality(ConnectionQuality.UNKNOWN);
            return;
        }
        ConnectionClassManager.getInstance().register(this);
        DeviceBandwidthSampler.getInstance().startSampling();

        if (mTask == null || mTask.getStatus() == AsyncTask.Status.FINISHED || mTask.isCancelled()) {
            mTask = new DownloadBytes();
            mTask.execute("https://httpbin.org/drip?numbytes=" + BYTES);
        }

        runnable = new Runnable() {
            @Override
            public void run() {
                DeviceBandwidthSampler.getInstance().stopSampling();
                samplingDone();
            }
        };
        nTries = 0;
        handler.postDelayed(runnable, DURATION_MILLIS);
        setStatus(STATUS_IDLE, R.string.checking);
    }

    protected void samplingDone() {
        ConnectionQuality cq = ConnectionClassManager.getInstance().getCurrentBandwidthQuality();
        if (cq == ConnectionQuality.UNKNOWN && nTries < MAX_TRIES) {
            nTries++;
            startCheck();
            return;
        }
        updateConnectionQuality(cq);
    }

    protected void updateConnectionQuality(ConnectionQuality cq) {
        int status = STATUS_ERROR;
        int description = R.string.network_quality_unknown;
        switch (cq) {
            case POOR:
                description = R.string.network_quality_poor;
                break;
            case MODERATE:
                status = STATUS_WARNING;
                description = R.string.network_quality_warning;
                break;
            case GOOD:
            case EXCELLENT:
                status = STATUS_OK;
                description = R.string.network_quality_ok;
        }
        setStatus(status, description);
    }

    public void onBandwidthStateChange(ConnectionQuality bandwidthState) {

    }

    @Override
    public void cancel() {
        super.cancel();
        super.cancel(mTask);
        ConnectionClassManager.getInstance().remove(this);
    }

    private class DownloadBytes extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(String... url) {
            String strURL = url[0];
            try {
                URLConnection connection = new URL(strURL).openConnection();
                connection.setUseCaches(false);
                connection.connect();
                InputStream input = connection.getInputStream();
                try {
                    byte[] buffer = new byte[1024];
                    while (input.read(buffer) != -1) ;
                } finally {
                    input.close();
                }
            } catch (IOException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }
}
