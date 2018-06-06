package hr.ferit.mahmutaksakalli.androidsocialeventstarter.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import hr.ferit.mahmutaksakalli.androidsocialeventstarter.DataRepository;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.R;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.model.PlaceInfo;

public class PlaceMapActivity extends AppCompatActivity
        implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private Location mLocation;
    private List<PlaceInfo> mPlaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_map);

        findViewById(R.id.mapView).setOnClickListener(this);
        findViewById(R.id.listView).setOnClickListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocation = DataRepository.getInstance().getLocation();
        mPlaces = DataRepository.getInstance().getPlaces();


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        UiSettings mapUI = mMap.getUiSettings();
        mapUI.setZoomControlsEnabled(true);
        mapUI.setMyLocationButtonEnabled(true);
        mapUI.setAllGesturesEnabled(true);

        LatLng myLocation = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));

        Bitmap myMarker = resizeMapIcons("if_mylocation",47,64);
        MarkerOptions myLocationMarker = new MarkerOptions()
                            .position(myLocation)
                            .title("Your location")
                            .icon(BitmapDescriptorFactory.fromBitmap(myMarker));

        mMap.addMarker(myLocationMarker);
        markPlaces();

    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.mapView:
                startActivity(new Intent(getApplicationContext(), PlaceMapActivity.class ));
                break;
            case R.id.listView:
                startActivity(new Intent(getApplicationContext(), PlaceListActivity.class ));
                break;
        }
    }

    private void markPlaces(){

        Bitmap myMarker = resizeMapIcons("cafe_icon",64,64);

        for(PlaceInfo place: mPlaces){
            double lang = place.getGeometry().getLocation().getLat();
            double lng = place.getGeometry().getLocation().getLng();
            LatLng placeLocation = new LatLng(lang, lng);
            MarkerOptions locationMarker = new MarkerOptions()
                    .position(placeLocation)
                    .title(place.getName())
                    .icon(BitmapDescriptorFactory.fromBitmap(myMarker));

            mMap.addMarker(locationMarker);        }
    }

    private Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),
                getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

}
