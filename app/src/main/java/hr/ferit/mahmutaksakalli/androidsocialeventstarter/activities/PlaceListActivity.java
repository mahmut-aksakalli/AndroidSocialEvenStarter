package hr.ferit.mahmutaksakalli.androidsocialeventstarter.activities;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.DataRepository;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.PlaceViewModel;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.R;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.model.PlaceInfo;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.model.SearchResult;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.network.RetrofitHelper;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.recylerview.PlacesAdapter;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.recylerview.PlacesClickCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceListActivity extends AppCompatActivity {

    @BindView(R.id.rvNearbylocation) RecyclerView rvSearchPlaces;
    @BindView(R.id.loading) TextView loadingTextview;

    private static final int REQUEST_LOCATION_PERMISSION = 10;
    private static final String TAG = "response_ok";
    public static final String KEY_API = "AIzaSyCLhS2ls4zHqefSBqgCnqwGbM4XyniJNq0";
    public static final String PLACE_ID = "place_id";
    public static final String PLACE_NAME = "place_name";
    public static final String PLACE_ADDRESS = "place_address";
    public static final String PLACE_RATING = "place_rating";
    public static final String PLACE_PHOTO = "place_photo";

    private PlaceViewModel mViewModel;
    private DataRepository mDataRepository;

    LocationListener mLocationListener;
    LocationManager mLocationManager;

    private PlacesClickCallback mClickCallback = new PlacesClickCallback() {
        @Override
        public void onClick(PlaceInfo place) {
            Intent placeIntent = new Intent(getApplicationContext(), PlaceDetailsActivity.class);
            placeIntent.putExtra(PLACE_ID,place.getPlaceId());
            placeIntent.putExtra(PLACE_NAME,place.getName());
            placeIntent.putExtra(PLACE_ADDRESS,place.getVicinity());
            placeIntent.putExtra(PLACE_RATING,String.valueOf(place.getRating()));
            placeIntent.putExtra(PLACE_PHOTO,place.getPhotoURL());
            startActivity(placeIntent);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        ButterKnife.bind(this);
        loadingTextview.setVisibility(View.VISIBLE);

        mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        mLocationListener = new hr.ferit.mahmutaksakalli.androidsocialeventstarter.activities.PlaceListActivity.SimpleLocationListener();

        mViewModel = ViewModelProviders.of(this)
                .get(PlaceViewModel.class);

        setUpRecyclerView();

        mDataRepository = DataRepository.getInstance();

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

    void fetchShows(String key, final Location location){
        StringBuilder locationString = new StringBuilder();
        locationString.append(location.getLatitude()).append(",");
        locationString.append(location.getLongitude());

        RetrofitHelper
                .getApi()
                .getNearbyPlaces(key, locationString.toString())
                .enqueue(new Callback<SearchResult>() {
                    Location resultLocation = new Location("");
                    @Override
                    public void onResponse(Call<SearchResult> call,
                                           Response<SearchResult> response) {

                        SearchResult resultBody = response.body();
                        for(PlaceInfo result: resultBody.getResults()){
                            this.resultLocation.setLatitude(result.getGeometry().getLocation().getLat());
                            this.resultLocation.setLongitude(result.getGeometry().getLocation().getLng());
                            result.setDistanceTo((int)location.distanceTo(resultLocation));
                        }
                        loadingTextview.setVisibility(View.GONE);
                        mViewModel.setSearchPlaces(resultBody.getResults());
                        mDataRepository.setPlaces(resultBody.getResults());

                    }

                    @Override
                    public void onFailure(Call<SearchResult> call,
                                          Throwable t) {
                        Log.d(TAG , t.getLocalizedMessage());
                    }
                });
    }

    void setUpRecyclerView() {

        LinearLayoutManager linearLayout =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        DividerItemDecoration divider =
                new DividerItemDecoration(this, linearLayout.getOrientation());

        PlacesAdapter adapter = new PlacesAdapter(new ArrayList<PlaceInfo>(), mClickCallback);

        rvSearchPlaces.setLayoutManager(linearLayout);
        rvSearchPlaces.addItemDecoration(divider);
        rvSearchPlaces.setAdapter(adapter);

        mViewModel.getSearchPlaces().observe(this, new Observer<List<PlaceInfo>>() {
            @Override
            public void onChanged(@Nullable List<PlaceInfo> places) {
                ((PlacesAdapter)(rvSearchPlaces.getAdapter())).refreshData(places);

            }
        });
    }

    public void updateLocation(Location location) {
        mDataRepository.setLocation(location);
        this.fetchShows(KEY_API,location);
    }

    public void startTracking() {
        Log.d("Tracking", "Tracking started.");

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String locationProvider = mLocationManager.getBestProvider(criteria, true);
        long minTime = 1000*60;
        float minDistance = 1000;

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
        ActivityCompat.requestPermissions(hr.ferit.mahmutaksakalli.androidsocialeventstarter.activities.PlaceListActivity.this,
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

    @OnClick(R.id.mapView)
    void onClickMapView(){
        startActivity(new Intent(getApplicationContext(), PlaceMapActivity.class ));
    }

    @OnClick(R.id.listView)
    void onClickListView(){
        startActivity(new Intent(getApplicationContext(), PlaceListActivity.class ));
    }

    private class SimpleLocationListener implements LocationListener{
        @Override
        public void onLocationChanged(Location location) { updateLocation(location); }
        @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override public void onProviderEnabled(String provider) {}
        @Override public void onProviderDisabled(String provider) {}
    }
}
