package hr.ferit.mahmutaksakalli.androidsocialeventstarter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.ferit.mahmutaksakalli.androidsocialeventstarter.model.PlaceInfo;

public class PlacesAdapter  extends RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder>{

    private List<PlaceInfo> mPlaces;
    private NewsClickCallback mCallback;

    public NewsAdapter(List<PlaceInfo> places, NewsClickCallback onNewsClickListener){
        mPlaces = new ArrayList<>();
        this.refreshData(places);
        mCallback = onNewsClickListener;
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
        holder.title.setText(current.getTitle());
        holder.category.setText(current.getCategory());
        holder.description.setText(current.getDescription());
        holder.pubDate.setText(current.getPubDate());
        Picasso.get()
                .load(current.getEnclosure().getUrl())
                .centerCrop()
                .fit()
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.poster);
    }

    @Override
    public int getItemCount() {
        return mPlaces.size();
    }

    public void refreshData(List<PlaceInfo> news) {
        mPlaces.clear();
        mPlaces.addAll(news);
        this.notifyDataSetChanged();
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.poster) ImageView poster;
        @BindView(R.id.title)  TextView title;
        @BindView(R.id.description)  TextView description;
        @BindView(R.id.category) TextView category;
        @BindView(R.id.pubdate)  TextView pubDate;

        public PlaceViewHolder(View itemView, final NewsClickCallback callback) {
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
