package org.honorato.diagnostics.models;

import android.content.Context;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import org.honorato.diagnostics.R;

import java.util.Random;

/**
 * Created by jlh on 11/26/15.
 */
public class Check implements Comparable {
    public final static int STATUS_OK      = 0;
    public final static int STATUS_IDLE    = 1;
    public final static int STATUS_WARNING = 2;
    public final static int STATUS_ERROR   = 3;
    public final static int STATUS_INFO    = 4;

    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableField<String> description = new ObservableField<>();
    public final ObservableInt status = new ObservableInt(STATUS_IDLE);

    /**
     * A context to be used within this check
     */
    protected Context context;

    /**
     * The main handler for this check's runnable
     */
    protected Handler handler;

    /**
     * The runnable to be used by @ref{handler} to actually
     * run this check
     */
    protected Runnable runnable;

    public Check(Context context) {
        this.context = context;
    }

    /**
     * Called when this check is ran externally
     */
    public void run() {
        if (runnable == null) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    performCheck();
                }
            };
        }

        if (handler == null) {
            handler = new Handler();
        }
        handler.post(runnable);
    }

    /**
     * Actually perform the check duty
     */
    protected void performCheck() {
        Random r = new Random();
        int status = r.nextInt(STATUS_INFO) + 1;
        Check.this.status.set(status);
        Check.this.description.set("Description status: " + Check.this.status.get());
    }

    public void cancel() {
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    public void cancel(AsyncTask<?, ?, ?> task) {
        if (task != null && !task.isCancelled()) {
            task.cancel(true);
        }
    }

    @Nullable
    public Drawable getStatusDrawable() {
        return getStatusDrawable(status.get());
    }

    @Nullable
    public Drawable getStatusDrawable(int status) {
        switch (status) {
            case STATUS_IDLE:
                return ContextCompat.getDrawable(context, R.drawable.ic_wait);
            case STATUS_OK:
                return ContextCompat.getDrawable(context, R.drawable.ic_check);
            case STATUS_WARNING:
                return ContextCompat.getDrawable(context, R.drawable.ic_warning);
            case STATUS_ERROR:
                return ContextCompat.getDrawable(context, R.drawable.ic_error);
            case STATUS_INFO:
                return ContextCompat.getDrawable(context, R.drawable.ic_info);
        }
        return null;
    }

    public int getStatusColor() {
        return getStatusColor(status.get());
    }

    public int getStatusColor(int status) {
        switch (status) {
            case STATUS_OK:
                return ContextCompat.getColor(context, R.color.success);
            case STATUS_WARNING:
                return ContextCompat.getColor(context, R.color.warning);
            case STATUS_ERROR:
                return ContextCompat.getColor(context, R.color.error);
            default:
                return ContextCompat.getColor(context, R.color.idle);
        }
    }

    @NonNull
    public String getStatusString() {
        return getStatusString(status.get());
    }

    @NonNull
    public String getStatusString(int status) {
        switch (status) {
            case STATUS_OK:
                return "ok";
            case STATUS_WARNING:
                return "warning";
            case STATUS_ERROR:
                return "error";
            case STATUS_INFO:
                return "error";
            case STATUS_IDLE:
                return "idle";
            default:
                return "unknown";
        }
    }

    public void setTitle(int titleRes) {
        this.title.set(this.context.getString(titleRes));
    }

    public void setStatus(int status, int description) {
        setStatus(status, this.context.getString(description));
    }

    public void setStatus(int status, String description) {
        this.status.set(status);
        this.description.set(description);
    }

    public int compareTo(@NonNull  Object o) {
        if (!(o instanceof Check)) {
            return 1;
        }
        Check other = (Check) o;
        return other.status.get() - this.status.get();
    }
}
