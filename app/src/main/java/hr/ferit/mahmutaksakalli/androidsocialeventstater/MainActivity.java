package hr.ferit.mahmutaksakalli.androidsocialeventstater;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 10;
    TextView tvLocationDisplay;
    LocationListener mLocationListener;
    LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLocationDisplay = (TextView) findViewById(R.id.tvLocationDisplay);

        mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        mLocationListener = new SimpleLocationListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!hasLocationPermission()) {
            requestPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.hasLocationPermission()) {
            startTracking();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTracking();
    }

    private void startTracking() {
        Log.d("Tracking", "Tracking started.");
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String locationProvider = this.mLocationManager.getBestProvider(criteria, true);
        long minTime = 1000;
        float minDistance = 10;
        try {
            this.mLocationManager.requestLocationUpdates(locationProvider, minTime, minDistance,
                    this.mLocationListener);
        }catch (SecurityException error){
            Log.d("Tracking", error.getMessage());
        }
    }

    private void stopTracking() {
        Log.d("Tracking", "Tracking stopped.");
        this.mLocationManager.removeUpdates(this.mLocationListener);
    }

    private void updateLocationDisplay(Location location) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Lat: ").append(location.getLatitude()).append("\n");
        stringBuilder.append("Lon: ").append(location.getLongitude());
        tvLocationDisplay.setText(stringBuilder.toString());
    }

    private boolean hasLocationPermission() {
        String LocationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        int status = ContextCompat.checkSelfPermission(this, LocationPermission);
        if (status == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermission() {
        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(MainActivity.this,
                permissions, REQUEST_LOCATION_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.d("Permission", "Permission granted. User pressed allow.");
                    } else {
                        Log.d("Permission", "Permission not granted. User pressed deny.");
                    }
                }
        }
    }

    private class SimpleLocationListener implements LocationListener{
        @Override
        public void onLocationChanged(Location location) { updateLocationDisplay(location); }
        @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override public void onProviderEnabled(String provider) { }
        @Override public void onProviderDisabled(String provider) {}
    }
}
