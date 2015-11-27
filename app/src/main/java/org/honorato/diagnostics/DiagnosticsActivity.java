package org.honorato.diagnostics;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import org.honorato.diagnostics.adapters.ChecksAdapter;
import org.honorato.diagnostics.databinding.ActivityDiagnosticsBinding;
import org.honorato.diagnostics.models.Check;
import org.honorato.diagnostics.models.DiskCheck;
import org.honorato.diagnostics.models.GpsCheck;
import org.honorato.diagnostics.models.MemoryCheck;

public class DiagnosticsActivity extends AppCompatActivity {

    ObservableList<Check> checks;

    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnostics);
        ActivityDiagnosticsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_diagnostics);
        mListView = binding.listview;
        setChecks();
        runChecks();
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
        checks.add(new MemoryCheck(context));
        checks.add(new GpsCheck(context));
        checks.add(new DiskCheck(context));

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
}
