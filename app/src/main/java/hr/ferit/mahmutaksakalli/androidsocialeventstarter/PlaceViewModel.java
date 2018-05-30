package hr.ferit.mahmutaksakalli.androidsocialeventstarter;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import hr.ferit.mahmutaksakalli.androidsocialeventstarter.model.PlaceInfo;

public class PlaceViewModel extends ViewModel{

    private MutableLiveData<List<PlaceInfo>> mSearchPlaces =
            new MutableLiveData<>();

    public MutableLiveData<List<PlaceInfo>> getSearchPlaces() {
        return mSearchPlaces;
    }

    public void setSearchPlaces(List<PlaceInfo> mSearchPlaces) {
        this.mSearchPlaces.postValue(mSearchPlaces);
    }
}
