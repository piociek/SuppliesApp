package piociek.suppliesapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import piociek.suppliesapp.adapter.LocationDetailsListRecyclerViewAdapter;
import piociek.suppliesapp.domain.Item;
import piociek.suppliesapp.util.ItemUtils;
import piociek.suppliesapp.view.ItemViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static piociek.suppliesapp.constants.IntentExtra.ITEM_ID;
import static piociek.suppliesapp.constants.IntentExtra.LOCATION_DETAILS_ID;
import static piociek.suppliesapp.util.ItemUtils.getTextFromNullableOrDefault;

public class ItemDetailsActivity
        extends AppCompatActivity
        implements LocationDetailsListRecyclerViewAdapter.OnItemLongClickListener {

    private ItemViewModel itemViewModel;
    private LocationDetailsListRecyclerViewAdapter recyclerViewAdapter;
    private Item item;
    private String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        if (!getIntent().hasExtra(ITEM_ID)) {
            Toast.makeText(this, getString(R.string.error_missing_id), Toast.LENGTH_SHORT).show();
            finish();
        } else {
            itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
            handleIncomingIntent();
            initUi();
            initLocationDetailsRecyclerView();
        }
    }

    private void initUi() {
        LinearLayout descriptionBox = findViewById(R.id.description_box);
        descriptionBox.setOnLongClickListener(v -> {
            Intent intent = new Intent(this, EditItemActivity.class);
            intent.putExtra(ITEM_ID, id);
            startActivity(intent);
            return true;
        });

        FloatingActionButton fabNewLocationDetails = findViewById(R.id.fab_new_item_location);
        fabNewLocationDetails.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditLocationDetailsActivity.class);
            intent.putExtra(ITEM_ID, id);
            startActivity(intent);
        });
    }

    private void handleIncomingIntent(){
        id = getIntent().getStringExtra(ITEM_ID);
        itemViewModel.getLiveDataItemById(id).observe(this,
                item -> {
                    this.item = item;
                    initItemDetails(item);
                    recyclerViewAdapter.setLocationDetails(item.getLocationDetails());
                });
    }

    private void initItemDetails(Item item) {
        if (item != null) {
            TextView name = findViewById(R.id.item_name);
            name.setText(getTextFromNullableOrDefault(item.getName(), ""));
            TextView barCode = findViewById(R.id.item_bar_code);
            barCode.setText(getTextFromNullableOrDefault(item.getBarCode(), ""));
            TextView category = findViewById(R.id.item_category);
            category.setText(getTextFromNullableOrDefault(item.getCategory(), ""));
            TextView count = findViewById(R.id.item_count);
            count.setText(String.valueOf(item.getTotalCount()));
            TextView packaging = findViewById(R.id.item_packaging);
            packaging.setText(getTextFromNullableOrDefault(item.getPackaging(), ""));
            TextView shortestExpDate = findViewById(R.id.item_shortest_exp_date);
            shortestExpDate.setText(item.getShortestExpDate());
        }
    }

    private void initLocationDetailsRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_item_location_details_list);
        recyclerViewAdapter = new LocationDetailsListRecyclerViewAdapter(this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onItemLongClick(int position) {
        Intent intent = new Intent(this, EditLocationDetailsActivity.class);
        intent.putExtra(ITEM_ID, id);
        intent.putExtra(LOCATION_DETAILS_ID, item.getLocationDetails().get(position).getId());
        startActivity(intent);
    }
}
