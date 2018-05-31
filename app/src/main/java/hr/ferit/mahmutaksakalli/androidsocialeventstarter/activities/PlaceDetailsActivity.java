package hr.ferit.mahmutaksakalli.androidsocialeventstarter.activities;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.MainActivity;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.R;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.model.PlaceInfo;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.model.database.EventInfo;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.recylerview.EventClickCallback;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.recylerview.EventsAdapter;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.recylerview.PlacesAdapter;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.recylerview.PlacesClickCallback;

public class PlaceDetailsActivity extends AppCompatActivity {

    private String placeRating;
    private String placeID;
    private String placeName;
    private String placeAddress;
    private String placeReference;
    public static final int INTENT_KEY = 999;

    private DatabaseReference mDatabase;

    @BindView(R.id.headtitle) TextView headTitle;
    @BindView(R.id.address)   TextView address;
    @BindView(R.id.rating)    TextView rating;
    @BindView(R.id.listView)  ImageButton listButton;
    @BindView(R.id.mapView)   ImageButton mapButton;
    @BindView(R.id.poster)    ImageView poster;
    @BindView(R.id.rvUpcomingEvents) RecyclerView rvUpcomingEvents;

    private EventClickCallback mClickCallback = new EventClickCallback() {
        @Override
        public void onClick(EventInfo event) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        ButterKnife.bind(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        setuptUI();
        setUpRecyclerView();

    }

    void setuptUI(){

        Intent receivedIntent = getIntent();
        placeID   = receivedIntent.getStringExtra(MainActivity.PLACE_ID);
        placeName = receivedIntent.getStringExtra(MainActivity.PLACE_NAME);
        placeAddress = receivedIntent.getStringExtra(MainActivity.PLACE_ADDRESS);
        placeRating  = receivedIntent.getStringExtra(MainActivity.PLACE_RATING);
        placeReference  = receivedIntent.getStringExtra(MainActivity.PLACE_PHOTO);

        headTitle.setText(placeName);
        address.setText(placeAddress);
        rating.setText(placeRating);

        String photoURL = new StringBuilder()
                    .append("https://maps.googleapis.com/maps/api/place/photo?")
                    .append("maxwidth=960&photoreference=").append(placeReference)
                    .append("&key=").append(MainActivity.KEY_API)
                    .toString();

        Picasso.get()
                .load(photoURL)
                .centerCrop()
                .fit()
                .error(R.drawable.cafe_icon)
                .placeholder(R.drawable.cafe_icon)
                .into(poster);
    }

    void setUpRecyclerView() {

        LinearLayoutManager linearLayout =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        DividerItemDecoration divider =
                new DividerItemDecoration(this, linearLayout.getOrientation());

        EventsAdapter adapter = new EventsAdapter(mDatabase, mClickCallback, placeID);

        rvUpcomingEvents.setLayoutManager(linearLayout);
        rvUpcomingEvents.addItemDecoration(divider);
        rvUpcomingEvents.setAdapter(adapter);

    }

    @OnClick(R.id.createEvent)
    public void onClickEvent(){
        Intent createIntent = new Intent(getApplicationContext(),CreateEventActivity.class);
        createIntent.putExtra(MainActivity.PLACE_ID, placeID);
        createIntent.putExtra(MainActivity.PLACE_NAME, placeName);
        startActivityForResult(createIntent, INTENT_KEY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(resultCode, resultCode, data);
        if( requestCode == INTENT_KEY && resultCode==RESULT_OK){
            this.displayToastMessage("New event created");

        }
    }

    private void displayToastMessage(String Text) {
        Toast T = Toast.makeText(PlaceDetailsActivity.this, Text, Toast.LENGTH_SHORT);
        T.show();
    }
}
