package hr.ferit.mahmutaksakalli.androidsocialeventstarter.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {

    private static final String BASE_URL = "https://maps.googleapis.com/maps/";
    private static Retrofit sRetrofitInstance = null;
    private static PlacesApi sPlacesApi = null;

    public static PlacesApi getApi(){

        if(sPlacesApi == null){
            sRetrofitInstance = getRetrofit();
            sPlacesApi = sRetrofitInstance.create(PlacesApi.class);
        }
        return sPlacesApi;

    }

    private static Retrofit getRetrofit() {
        if(sRetrofitInstance == null) {
            sRetrofitInstance = new Retrofit.Builder()
                    .addConverterFactory(getConverterFactory())
                    .baseUrl(BASE_URL)
                    .client(getOkHttpClient())
                    .build();
        }
        return sRetrofitInstance;
    }

    private static OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .build();
    }

    private static Converter.Factory getConverterFactory() {
        Gson gson = new GsonBuilder()
                .create();

        return GsonConverterFactory.create(gson);
    }

}
