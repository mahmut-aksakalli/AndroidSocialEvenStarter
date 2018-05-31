package hr.ferit.mahmutaksakalli.androidsocialeventstarter.model.database;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class EventInfo {

    public String placeID;
    public Date   date;

    public EventInfo()  {
        // Default constructor required for calls to DataSnapshot.getValue(---.class)
    }
    public EventInfo(String placeID, Date date) {
        this.placeID = placeID;
        this.date = date;
    }
}
