package hr.ferit.mahmutaksakalli.androidsocialeventstarter;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
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

import hr.ferit.mahmutaksakalli.androidsocialeventstarter.model.SearchResult;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.network.RetrofitHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private PlaceViewModel mViewModel;

    private static final int REQUEST_LOCATION_PERMISSION = 10;
    private static final String TAG = "response_ok";
    private static final String KEY_API = "AIzaSyCLhS2ls4zHqefSBqgCnqwGbM4XyniJNq0";
    int radius = 5000;

    LocationListener mLocationListener;
    LocationManager mLocationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        mLocationListener = new SimpleLocationListener();

        mViewModel = ViewModelProviders.of(this)
                .get(PlaceViewModel.class);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!this.hasLocationPermission()) {
            requestPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.hasLocationPermission()) {
            this.startTracking();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.stopTracking();
    }

     void fetchShows(String key,String location, int radius){
        RetrofitHelper
                .getApi()
                .getNearbyPlaces(key, location, radius)
                .enqueue(new Callback<SearchResult>() {
                    @Override
                    public void onResponse(Call<SearchResult> call,
                                           Response<SearchResult> response) {

                        SearchResult resultBody = response.body();
                        mViewModel.setSearchPlaces(resultBody.getResults());

                    }

                    @Override
                    public void onFailure(Call<SearchResult> call,
                                          Throwable t) {
                        Log.d(TAG , t.getLocalizedMessage());
                    }
                });
    }

    public void updateLocationDisplay(Location location) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(location.getLatitude()).append(",");
        stringBuilder.append(location.getLongitude());

        this.fetchShows(KEY_API,stringBuilder.toString(),radius);

    }

    public void startTracking() {
        Log.d("Tracking", "Tracking started.");

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String locationProvider = mLocationManager.getBestProvider(criteria, true);
        long minTime = 1000*60;
        float minDistance = 100;

        try {
            mLocationManager.requestLocationUpdates(locationProvider, minTime, minDistance,
                    mLocationListener);
        }catch (SecurityException error){
            Log.d("Tracking", error.getMessage());
        }
    }

    public void stopTracking() {
        Log.d("Tracking", "Tracking stopped.");
        mLocationManager.removeUpdates(mLocationListener);
    }

    public boolean hasLocationPermission() {
        String LocationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        int status = ContextCompat.checkSelfPermission(this, LocationPermission);
        if (status == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public void requestPermission() {
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
