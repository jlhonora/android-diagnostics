package org.honorato.diagnostics.models;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.text.format.DateUtils;

import org.honorato.diagnostics.BuildConfig;
import org.honorato.diagnostics.R;
import org.honorato.diagnostics.utils.SntpClient;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by jlh on 11/30/15.
 */
public class TimeCheck extends Check {

    TimeCheckTask mTask;

    public TimeCheck(Context context) {
        super(context);
        setTitle(R.string.time_title);
        this.setStatus(STATUS_IDLE, "");
    }

    @Override
    protected void performCheck() {
        mTask = new TimeCheckTask();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this);
        } else {
            mTask.execute(this);
        }
    }

    protected static long getNtpMillis(String url) {
        long result = 0L;
        SntpClient client = new SntpClient();
        try {
            if (client.requestTime(url, (int) (DateUtils.SECOND_IN_MILLIS))) {
                result = client.getNtpTime() + SystemClock.elapsedRealtime() - client.getNtpTimeReference();
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public void updateNtpTime(long millis) {
        Calendar localCal = Calendar.getInstance();
        Calendar ntpCal = Calendar.getInstance();
        ntpCal.setTimeInMillis(millis);

        long diff = localCal.getTimeInMillis() - ntpCal.getTimeInMillis();

        int status = STATUS_OK;
        float diffFraction = (float) diff / (float) DateUtils.SECOND_IN_MILLIS;
        int unitStringRes = R.string.seconds;
        if (ntpCal.getTimeInMillis() == 0L) {
            status = STATUS_ERROR;
            this.setStatus(status, this.context.getString(R.string.generic_check_error));
            return;
        } else if (Math.abs(diff) > 10 * DateUtils.MINUTE_IN_MILLIS) {
            diffFraction = (float) diff / (float) DateUtils.HOUR_IN_MILLIS;
            unitStringRes = R.string.hours;
            status = STATUS_ERROR;
        } else if (Math.abs(diff) > 2 * DateUtils.MINUTE_IN_MILLIS) {
            diffFraction = (float) diff / (float) DateUtils.MINUTE_IN_MILLIS;
            unitStringRes = R.string.minutes;
            status = STATUS_WARNING;
        }
        this.setStatus(status, this.context.getString(R.string.time_offset) + " " +
                String.format(Locale.getDefault(), "%.1f", diffFraction) + " " +
                this.context.getString(unitStringRes));
    }


    @Override
    public void cancel() {
        super.cancel();
        super.cancel(mTask);
    }

    public class TimeCheckTask extends AsyncTask<TimeCheck, Void, Long> {

        protected TimeCheck mCheck;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Long doInBackground(TimeCheck... checks) {
            this.mCheck = checks[0];
            return getNtpMillis("pool.ntp.org");
        }

        @Override
        protected void onPostExecute(Long result) {
            this.mCheck.updateNtpTime(result);
        }
    }
}
