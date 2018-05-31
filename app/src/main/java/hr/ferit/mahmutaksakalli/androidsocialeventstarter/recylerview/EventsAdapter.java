package hr.ferit.mahmutaksakalli.androidsocialeventstarter.recylerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.R;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.model.database.EventInfo;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder>{
    private DatabaseReference mDatabaseReference;
    private EventClickCallback mCallback;
    private String placeID;
    private List<EventInfo> mEvents = new ArrayList<>();

    public EventsAdapter(DatabaseReference ref, EventClickCallback Callback, String placeID){
        this.mDatabaseReference = ref;
        this.mCallback = Callback;
        this.placeID = placeID;

        ValueEventListener dataListener = new ValueEventListener() {
            List<EventInfo> eventList = new ArrayList<>();

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventList.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    EventInfo data = postSnapshot.getValue(EventInfo.class);
                    eventList.add(data);
                }

                refreshData(eventList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("error", "loadPost:onCancelled", databaseError.toException());
            }
        };

        mDatabaseReference.child("events").child(this.placeID).addValueEventListener(dataListener);
    }

    @NonNull
    @Override
    public EventsAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventsAdapter.EventViewHolder(view, mCallback);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsAdapter.EventViewHolder holder, int position) {
        EventInfo current = mEvents.get(position);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        String eventDate = formatter.format(current.date);
        holder.title.setText(eventDate);

    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public void refreshData(List<EventInfo> events) {

        Collections.sort(events, new Comparator<EventInfo>() {
            @Override
            public int compare(EventInfo o1, EventInfo o2) {
                return o1.date.compareTo(o2.date);
            }
        });

        mEvents.clear();
        mEvents.addAll(events);
        this.notifyDataSetChanged();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title) TextView title;

        public EventViewHolder(View itemView, final EventClickCallback callback) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onClick(mEvents.get(getAdapterPosition()));
                }
            });
        }
    }

}
