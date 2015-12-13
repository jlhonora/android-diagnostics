package org.honorato.diagnostics.example;

import android.os.Bundle;

import org.honorato.diagnostics.DiagnosticsActivity;

public class MainActivity extends DiagnosticsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setChecks() {
        super.setChecks();

        // Add your checks like this:
        // this.checks.add(new Check(this.getBaseContext()));
    }
}
