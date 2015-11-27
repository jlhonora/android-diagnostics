package org.honorato.diagnostics.models;

import android.content.Context;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;

import org.honorato.diagnostics.R;

import java.util.Random;

/**
 * Created by jlh on 11/26/15.
 */
public class Check {
    public final static int STATUS_IDLE    = 0;
    public final static int STATUS_OK      = 1;
    public final static int STATUS_WARNING = 2;
    public final static int STATUS_ERROR   = 3;
    public final static int STATUS_INFO    = 4;

    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableField<String> description = new ObservableField<>();
    public final ObservableInt status = new ObservableInt();
    public final ObservableInt code = new ObservableInt();

    Context context;
    Handler handler;
    Runnable runnable;

    public Check(Context context) {
        this.context = context;
    }

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

    public Drawable getStatusDrawable(int status) {
        return getStatusDrawable();
    }

    public Drawable getStatusDrawable() {
        switch (status.get()) {
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

    public int getStatusColor(int status) {
        return getStatusColor();
    }

    public int getStatusColor() {
        switch (status.get()) {
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
}
