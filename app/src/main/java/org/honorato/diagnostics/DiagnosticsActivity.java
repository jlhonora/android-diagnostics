package org.honorato.diagnostics;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

    ObservableList<Check> checks;

    ListView mListView;

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
        switch (item.getItemId()) {
            case R.id.action_refresh:
                setChecks();
                runChecks();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
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
        }
    }

    protected void setChecks() {
        Context context = this.getBaseContext();
        checks = new ObservableArrayList<>();
        checks.add(new BatteryCheck(context));
        checks.add(new DiskCheck(context));
        checks.add(new MemoryCheck(context));
        checks.add(new TimeCheck(context));
        checks.add(new NetworkStaticCheck(context));
        checks.add(new NetworkQualityCheck(context));
        checks.add(new GpsCheck(context));

        mListView.setAdapter(new ChecksAdapter(context, checks));
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
    }

    protected void sortChecks() {
        if (checks == null) {
            return;
        }
        Collections.sort(checks);
    }
}
