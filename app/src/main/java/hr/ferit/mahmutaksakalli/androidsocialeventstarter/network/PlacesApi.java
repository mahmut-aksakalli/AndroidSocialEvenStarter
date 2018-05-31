package hr.ferit.mahmutaksakalli.androidsocialeventstarter.network;

import hr.ferit.mahmutaksakalli.androidsocialeventstarter.model.SearchResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesApi {

    @GET("api/place/nearbysearch/json?type=cafe&rankby=distance")
    Call<SearchResult> getNearbyPlaces(@Query("key") String key, @Query("location") String location);
}
