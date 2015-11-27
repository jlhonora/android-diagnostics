package org.honorato.diagnostics.models;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import org.honorato.diagnostics.R;

import java.io.File;

/**
 * Created by jlh on 11/26/15.
 */
public class DiskCheck extends Check {

    public DiskCheck(Context context) {
        super(context);
        this.title.set(context.getString(R.string.disk_title));
    }

    @Override
    protected void performCheck() {
        File f = Environment.getDataDirectory();
        StatFs stat = new StatFs(f.getPath());
        double bytesAvailable = (double) getAvailableBytes(stat);
        long totalBytes = getTotalBytes(stat);

        double ratio = 100.0 - ((double) bytesAvailable * 100.0) / (double) totalBytes;
        double warningLimit = 70.0;
        double errorLimit = 90.0;

        int status = STATUS_OK;
        int description = R.string.disk_ok;
        if (ratio > warningLimit && ratio < errorLimit) {
            status = STATUS_WARNING;
            description = R.string.disk_warning;
        } else if (ratio >= errorLimit) {
            status = STATUS_ERROR;
            description = R.string.disk_full;
        }

        String bytesAvailableStr = String.format("%.1f GB " + this.context.getString(R.string.disk_available),
                (bytesAvailable / (1024.f * 1024.f * 1024.f)));

        this.status.set(status);
        this.description.set(String.format(this.context.getString(description),
                (int) ratio, bytesAvailableStr));
    }

    public long getAvailableBytes(StatFs stat) {
        if (Build.VERSION.SDK_INT >= 18) {
            return stat.getAvailableBytes();
        }
        return (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
    }

    public long getTotalBytes(StatFs stat) {
        if (Build.VERSION.SDK_INT >= 18) {
            return stat.getTotalBytes();
        }
        return (long) stat.getBlockSize() * (long) stat.getBlockCount();
    }
}
