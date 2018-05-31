package hr.ferit.mahmutaksakalli.androidsocialeventstarter.activities;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.MainActivity;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.R;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.model.database.EventInfo;

public class CreateEventActivity extends AppCompatActivity
                implements DatePickerFragment.onDataPickerSetListener,
                            TimePickerFragment.onTimePickerSetListener{

    private String placeID;
    private String placeName;
    private int year, month, day;
    private int hourOfDay, minute;
    private boolean dateFlag = false, timeFlag = false;

    @BindView(R.id.date)        TextView dateTextView;
    @BindView(R.id.time)        TextView timeTextView;
    @BindView(R.id.placeName)   TextView placeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        ButterKnife.bind(this);

        Intent receivedIntent = getIntent();
        placeID   = receivedIntent.getStringExtra(MainActivity.PLACE_ID);
        placeName = receivedIntent.getStringExtra(MainActivity.PLACE_NAME);

        placeTextView.setText(placeName);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!vbvb");
    }

    @OnClick(R.id.date)
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @OnClick(R.id.time)
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onDateSelected(int year, int month, int day) {
        this.year = year;
        this.month= month;
        this.day = day;
        this.dateFlag = true;

        String dateString = new StringBuilder()
                .append(String.valueOf(day)).append("/")
                .append(String.valueOf(month)).append("/")
                .append(String.valueOf(year)).toString();

        dateTextView.setText(dateString);

    }

    @Override
    public void onTimeSelected(int hourOfDay, int minute) {
        this.hourOfDay = hourOfDay;
        this.minute= minute;
        this.timeFlag = true;

        String timeString = new StringBuilder()
                .append(String.valueOf(hourOfDay)).append(":")
                .append(String.valueOf(minute)).toString();

        timeTextView.setText(timeString);

    }

    @OnClick(R.id.publishEvent)
    public void onClickPublishEvent() {
        Date eventDate = null;
        /** Check inputs are empty or not */
        if(timeFlag != true || dateFlag != true || placeID.isEmpty()) {
            displayToastMessage("Fill all inputs!");

        } else {
            /** create Date object of event date */
            String dateString = new StringBuilder()
                    .append(String.valueOf(day)).append("/")
                    .append(String.valueOf(month)).append("/")
                    .append(String.valueOf(year)).append(" ")
                    .append(String.valueOf(hourOfDay)).append(":")
                    .append(String.valueOf(minute)).toString();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
            try {
                eventDate = formatter.parse(dateString);
            } catch (ParseException e){
                e.printStackTrace();
            }

            /** if date is past, show a warning*/
            if(eventDate.before(new Date())){
                displayToastMessage("enter a valid date");
            }else{
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                EventInfo event = new EventInfo(placeID, eventDate);
                mDatabase.child("events").child(placeID).push().setValue(event);

                setResult(RESULT_OK, new Intent());
                this.finish();
            }
        }

    }

    private void displayToastMessage(String Text) {
        Toast T = Toast.makeText(CreateEventActivity.this, Text, Toast.LENGTH_SHORT);
        T.show();
    }
}
