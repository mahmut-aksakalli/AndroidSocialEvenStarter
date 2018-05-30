package hr.ferit.mahmutaksakalli.androidsocialeventstarter.recylerview;

import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import hr.ferit.mahmutaksakalli.androidsocialeventstarter.model.database.EventInfo;

public class UpcomingEventsAdapter  extends RecyclerView.Adapter<EventViewHolder>{
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    private List<EventInfo> mEvents = new ArrayList<>();


}
