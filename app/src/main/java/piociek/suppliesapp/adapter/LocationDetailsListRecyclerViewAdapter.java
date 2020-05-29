package piociek.suppliesapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import piociek.suppliesapp.R;
import piociek.suppliesapp.domain.LocationDetails;

import java.util.List;

public class LocationDetailsListRecyclerViewAdapter extends RecyclerView.Adapter<LocationDetailsListRecyclerViewAdapter.ViewHolder> {

    private List<LocationDetails> locationDetails;
    private LocationDetailsListRecyclerViewAdapter.OnItemLongClickListener onItemLongClickListener;

    public LocationDetailsListRecyclerViewAdapter(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setLocationDetails(List<LocationDetails> locationDetails) {
        this.locationDetails = locationDetails;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_location_details, parent, false);
        return new ViewHolder(view, onItemLongClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.locationCount.setText(String.valueOf(locationDetails.get(position).getCount()));
        holder.locationName.setText(String.valueOf(locationDetails.get(position).getName()));
        holder.locationExpDate.setText(String.valueOf(locationDetails.get(position).getExpDate()));
    }

    @Override
    public int getItemCount() {
        return locationDetails == null ? 0 : locationDetails.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView locationCount;
        TextView locationName;
        TextView locationExpDate;
        OnItemLongClickListener onItemLongClickListener;

        public ViewHolder(@NonNull View itemView, OnItemLongClickListener onItemLongClickListener) {
            super(itemView);
            locationCount = itemView.findViewById(R.id.list_location_count);
            locationName = itemView.findViewById(R.id.list_location_name);
            locationExpDate = itemView.findViewById(R.id.list_location_exp_date);

            this.onItemLongClickListener = onItemLongClickListener;

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            onItemLongClickListener.onItemLongClick(getAdapterPosition());
            return true;
        }
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }
}
