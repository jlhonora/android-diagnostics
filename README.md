An extensible diagnostics activity for Android.

![](./images/sample.jpg)

Uses Android's data binding framework, which is still in beta, so expect changes.

## Usage

Add this library to your app's `build.gradle` file:

```
dependencies {
    compile 'org.honorato.diagnostics:diagnostics:0.1.2'
}
```

Create an activity that extends `DiagnosticsActivity` (remember to declare it on your manifest as well):

```
public class MyDiagnosticsActivity extends DiagnosticsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
```

Call it:

```
Intent intent = new Intent(this, MyDiagnosticsActivity.class);
startActivity(intent);
```

## Implemented checks

- RAM Memory
- GPS enabled
- Disk space
- Network connection
- Network quality (speed)
- Battery status
- NTP time sync

## Adding custom checks

To add custom checks you'll need to:

- Create a custom check and override the `performCheck` method:

```
public class CustomCheck extends Check {

    public CustomCheck(Context context) {
        super(context);
        setTitle(R.string.custom_title);
    }

    @Override
    public void performCheck() {
        setStatus(STATUS_OK, "Everything's OK!");
    }
}
```

- Create an activity that extends `DiagnosticsActivity` and override `setChecks`:

```
public class MyDiagnosticsActivity extends DiagnosticsActivity {

    @Override
    protected void setChecks() {
        super.setChecks();

        // Add your checks like this:
        // this.checks.add(new CustomCheck(this.getBaseContext()));
    }
}
```

## Contributing

Contributions are welcome! Here are a few ideas:

0. Fix permissions for Android 6.0 (Marshmallow)
1. Setup a result (to be used along with `startActivityForResult`)
2. `NetworkQualityCheck` needs to be more stable, and provide uplink/downlink checks
3. Sensor checks are welcome
4. Create an icon
5. Coordinator layout for ActionBar + ListView
6. Add kbps measurement to `NetworkQualityCheck`
