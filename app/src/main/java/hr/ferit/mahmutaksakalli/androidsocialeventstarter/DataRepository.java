package hr.ferit.mahmutaksakalli.androidsocialeventstarter;

import android.location.Location;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import hr.ferit.mahmutaksakalli.androidsocialeventstarter.model.PlaceInfo;

public class DataRepository {

    private static DataRepository sInstance;
    private List<PlaceInfo> mPlaces;
    private Location mLocation;

    private DataRepository(){
        mPlaces = new ArrayList<>();
        mLocation = null;
    }

    public static DataRepository getInstance(){
        if(sInstance == null){
            sInstance = new DataRepository();
        }
        return sInstance;
    }

    public List<PlaceInfo> getPlaces() {
        return mPlaces;
    }

    public void setPlaces(List<PlaceInfo> mPlaces) {
        this.mPlaces = mPlaces;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location mLocation) {
        this.mLocation = mLocation;
    }

}
