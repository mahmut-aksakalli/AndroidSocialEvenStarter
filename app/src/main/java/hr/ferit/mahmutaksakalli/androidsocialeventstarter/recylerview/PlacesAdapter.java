package hr.ferit.mahmutaksakalli.androidsocialeventstarter.recylerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.R;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.model.PlaceInfo;

public class PlacesAdapter  extends RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder>{

    private List<PlaceInfo> mPlaces;
    private PlacesClickCallback mCallback;

    public PlacesAdapter(List<PlaceInfo> places,PlacesClickCallback onPlacesClickListener){
        mPlaces = new ArrayList<>();
        this.refreshData(places);
        mCallback = onPlacesClickListener;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_new, parent, false);
        return new PlaceViewHolder(view, mCallback);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        PlaceInfo current = mPlaces.get(position);
        String dist = String.valueOf(current.getDistanceTo())+" meters";
        StringBuilder photoURL = new StringBuilder();
        photoURL.append("https://maps.googleapis.com/maps/api/place/photo?")
                .append("maxwidth=960&photoreference=").append(current.getPhotoURL())
                .append("&key=AIzaSyCLhS2ls4zHqefSBqgCnqwGbM4XyniJNq0");

        holder.title.setText(current.getName());
        holder.distance.setText(dist);
        Picasso.get()
                .load(photoURL.toString())
                .centerCrop()
                .fit()
                .error(R.drawable.cafe_icon)
                .placeholder(R.drawable.cafe_icon)
                .into(holder.poster);

        if(current.getRating() == null){
            holder.score.setText("0.0");
        }else{
            holder.score.setText(String.valueOf(current.getRating()));
        }
    }

    @Override
    public int getItemCount() {
        return mPlaces.size();
    }

    public void refreshData(List<PlaceInfo> places) {
        mPlaces.clear();
        mPlaces.addAll(places);
        this.notifyDataSetChanged();
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.poster) ImageView poster;
        @BindView(R.id.title) TextView title;
        @BindView(R.id.distance)  TextView distance;
        @BindView(R.id.score) TextView score;

        public PlaceViewHolder(View itemView, final PlacesClickCallback callback) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onClick(mPlaces.get(getAdapterPosition()));
                }
            });
        }
    }

}
