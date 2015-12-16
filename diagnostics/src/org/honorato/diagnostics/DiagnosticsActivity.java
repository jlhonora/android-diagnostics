package org.honorato.diagnostics;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import org.honorato.diagnostics.adapters.ChecksAdapter;
import org.honorato.diagnostics.databinding.ActivityDiagnosticsBinding;
import org.honorato.diagnostics.models.BatteryCheck;
import org.honorato.diagnostics.models.Check;
import org.honorato.diagnostics.models.DiskCheck;
import org.honorato.diagnostics.models.GpsCheck;
import org.honorato.diagnostics.models.MemoryCheck;
import org.honorato.diagnostics.models.NetworkQualityCheck;
import org.honorato.diagnostics.models.NetworkStaticCheck;
import org.honorato.diagnostics.models.TimeCheck;

import java.util.Collections;

public class DiagnosticsActivity extends AppCompatActivity {

    protected ObservableList<Check> checks;

    ListView mListView;

    ChecksAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnostics);
        setBar();

        ActivityDiagnosticsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_diagnostics);
        mListView = binding.listview;
        setChecks();
        runChecks();
    }

    protected void setBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, android.R.color.transparent)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_refresh) {
            setChecks();
            runChecks();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (checks == null) {
            setChecks();
        } else {
            registerChecksCallbacks();
        }
    }

    protected void setChecks() {
        Context context = this.getBaseContext();
        checks = new ObservableArrayList<Check>();
        checks.add(new BatteryCheck(context));
        checks.add(new DiskCheck(context));
        checks.add(new MemoryCheck(context));
        checks.add(new TimeCheck(context));
        checks.add(new NetworkStaticCheck(context));
        checks.add(new NetworkQualityCheck(context));
        checks.add(new GpsCheck(context));

        mAdapter = new ChecksAdapter(context, checks);
        mListView.setAdapter(mAdapter);

        registerChecksCallbacks();
    }

    protected void runChecks() {
        if (checks == null) {
            return;
        }
        for (Check c : checks) {
            c.run();
        }
    }

    @Override
    protected void onPause() {
        cancelChecks();
        super.onPause();
    }

    protected void cancelChecks() {
        if (checks == null) {
            return;
        }
        for (Check c : checks) {
            c.cancel();
        }

        unregisterChecksCallbacks();
    }

    Observable.OnPropertyChangedCallback mCallback;
    ObservableList.OnListChangedCallback mListCallback;

    protected void registerChecksCallbacks() {
        if (mCallback == null) {
            mCallback = new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    sortChecks();
                }
            };
        }
        for (Check c : this.checks) {
            registerCheckCallback(c);
        }

        if (mListCallback == null) {
            mListCallback = new ObservableList.OnListChangedCallback() {


                @Override
                public void onChanged(ObservableList sender) {

                }

                @Override
                public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {

                }

                @Override
                public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {
                    for (int i = positionStart; i < (positionStart + itemCount); i++) {
                        registerCheckCallback((Check) sender.get(i));
                    }
                }

                @Override
                public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {

                }

                @Override
                public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {
                    for (int i = positionStart; i < (positionStart + itemCount); i++) {
                        unregisterCheckCallback((Check) sender.get(i));
                    }
                }
            };

        }
        this.checks.addOnListChangedCallback(mListCallback);
    }

    protected void registerCheckCallback(@NonNull Check c) {
        if (mCallback == null) {
            return;
        }
        c.status.addOnPropertyChangedCallback(mCallback);
    }

    protected void unregisterChecksCallbacks() {
        if (mCallback != null) {
            for (Check c : this.checks) {
                unregisterCheckCallback(c);
            }
        }
        if (mListCallback != null) {
            this.checks.removeOnListChangedCallback(mListCallback);
        }
    }

    protected void unregisterCheckCallback(Check c) {
        if (mCallback == null) {
            return;
        }
        c.status.removeOnPropertyChangedCallback(mCallback);
    }

    protected void sortChecks() {
        if (checks == null) {
            return;
        }
        Collections.sort(checks);

        mAdapter.notifyDataSetChanged();
    }
}
