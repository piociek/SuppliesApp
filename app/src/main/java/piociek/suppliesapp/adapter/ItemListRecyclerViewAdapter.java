package piociek.suppliesapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import piociek.suppliesapp.R;
import piociek.suppliesapp.domain.Item;

import java.util.List;

public class ItemListRecyclerViewAdapter extends RecyclerView.Adapter<ItemListRecyclerViewAdapter.ViewHolder> {

    private List<Item> items;
    private OnItemLongClickListener onItemLongClickListener;

    public ItemListRecyclerViewAdapter(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setItems(List<Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view, onItemLongClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textName.setText(items.get(position).getName());
        holder.textCount.setText(String.valueOf(items.get(position).getTotalCount()));
        holder.textCategory.setText(items.get(position).getCategory());
        holder.textPackaging.setText(items.get(position).getPackaging());
        holder.textExpDate.setText(items.get(position).getShortestExpDate());
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView textName;
        TextView textCount;
        TextView textCategory;
        TextView textPackaging;
        TextView textExpDate;
        private OnItemLongClickListener onItemLongClickListener;

        public ViewHolder(@NonNull View itemView, OnItemLongClickListener onItemLongClickListener) {
            super(itemView);
            textName = itemView.findViewById(R.id.list_item_name);
            textCount = itemView.findViewById(R.id.list_item_count);
            textCategory = itemView.findViewById(R.id.list_item_category);
            textPackaging = itemView.findViewById(R.id.list_item_packaging);
            textExpDate = itemView.findViewById(R.id.list_item_exp_date);
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
