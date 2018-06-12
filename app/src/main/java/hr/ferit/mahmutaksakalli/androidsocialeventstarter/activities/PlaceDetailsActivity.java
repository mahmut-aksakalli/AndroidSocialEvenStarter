package hr.ferit.mahmutaksakalli.androidsocialeventstarter.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.R;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.model.database.EventInfo;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.recylerview.EventClickCallback;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.recylerview.EventsAdapter;

public class PlaceDetailsActivity extends AppCompatActivity {

    private String placeRating;
    private String placeID;
    private String placeName;
    private String placeAddress;
    private String placeReference;
    public static final int INTENT_KEY = 999;

    private DatabaseReference mDatabase;

    @BindView(R.id.headtitle) TextView headTitle;
    @BindView(R.id.nothing)   TextView nothingTextview;
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

        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(ContextCompat.getColor(this, R.color.colorBg)));

        ButterKnife.bind(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        setuptUI();
        setUpRecyclerView();

    }

    void setuptUI(){

        Intent receivedIntent = getIntent();
        placeID   = receivedIntent.getStringExtra(PlaceListActivity.PLACE_ID);
        placeName = receivedIntent.getStringExtra(PlaceListActivity.PLACE_NAME);
        placeAddress = receivedIntent.getStringExtra(PlaceListActivity.PLACE_ADDRESS);
        placeRating  = receivedIntent.getStringExtra(PlaceListActivity.PLACE_RATING);
        placeReference  = receivedIntent.getStringExtra(PlaceListActivity.PLACE_PHOTO);

        headTitle.setText(placeName);
        address.setText(placeAddress);
        rating.setText(placeRating);

        String photoURL = new StringBuilder()
                    .append("https://maps.googleapis.com/maps/api/place/photo?")
                    .append("maxwidth=960&photoreference=").append(placeReference)
                    .append("&key=").append(PlaceListActivity.KEY_API)
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

        EventsAdapter adapter = new EventsAdapter(this,
                                        mDatabase, mClickCallback, placeID);

        rvUpcomingEvents.setLayoutManager(linearLayout);
        rvUpcomingEvents.addItemDecoration(divider);
        rvUpcomingEvents.setAdapter(adapter);

    }

    public void setInfo(int count){
        if(count == 0){
            nothingTextview.setVisibility(View.VISIBLE);
        } else {
            nothingTextview.setVisibility(View.GONE);
        }
    }
    @OnClick(R.id.createEvent)
    public void onClickEvent(){
        Intent createIntent = new Intent(getApplicationContext(),CreateEventActivity.class);
        createIntent.putExtra(PlaceListActivity.PLACE_ID, placeID);
        createIntent.putExtra(PlaceListActivity.PLACE_NAME, placeName);
        startActivityForResult(createIntent, INTENT_KEY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(resultCode, resultCode, data);
        if( requestCode == INTENT_KEY && resultCode==RESULT_OK){
            this.displayToastMessage("New event created");

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

    private void displayToastMessage(String Text) {
        Toast T = Toast.makeText(PlaceDetailsActivity.this, Text, Toast.LENGTH_SHORT);
        T.show();
    }
}
