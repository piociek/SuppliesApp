package piociek.suppliesapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import piociek.suppliesapp.adapter.ItemListRecyclerViewAdapter;
import piociek.suppliesapp.async.AsyncCompletionAware;
import piociek.suppliesapp.async.GetAllItemsAsync;
import piociek.suppliesapp.domain.Item;
import piociek.suppliesapp.fragments.ConnectionErrorFragment;
import piociek.suppliesapp.fragments.LoadingFragment;
import piociek.suppliesapp.view.ItemViewModel;

import static piociek.suppliesapp.constants.IntentExtra.BAR_CODE;
import static piociek.suppliesapp.constants.IntentExtra.ITEM_ID;
import static piociek.suppliesapp.constants.IntentExtra.SCAN_RESULT;
import static piociek.suppliesapp.util.Animation.animateTransitionBetweenViews;

public class MainActivity
        extends AppCompatActivity
        implements ItemListRecyclerViewAdapter.OnItemLongClickListener,
        AdapterView.OnItemSelectedListener,
        AsyncCompletionAware {

    private String CATEGORY_ALL;

    private ItemViewModel itemViewModel;
    private ItemListRecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;

    private View frameLayout;

    private List<Item> items = new ArrayList<>();
    private List<Item> shownItems = new ArrayList<>();
    private int spinnerCategorySelectedPosition = 0;
    private CharSequence categorySelected;
    private List<CharSequence> itemCategories = new ArrayList<>();

    private boolean progressBarFadedOut;
    private boolean categoriesLiveDataObtained;
    private boolean itemsLiveDataObtained;
    private boolean getAllItemsCallSuccessful;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);

        initUi();
        initItemListRecyclerView();
        initCategorySpinner();
        initProgressBarFragment();
        getAllItemsFromService();
    }

    private void initUi() {
        CATEGORY_ALL = getString(R.string.filter_show_all);
        categorySelected = CATEGORY_ALL;

        frameLayout = findViewById(R.id.fragment_container);
        recyclerView = findViewById(R.id.recycler_item_list);
        recyclerView.setVisibility(View.GONE);

        FloatingActionButton fabNewItem = findViewById(R.id.fab_new_item);
        fabNewItem.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditItemActivity.class);
            startActivity(intent);
        });

        FloatingActionButton fabNewItemByScan = findViewById(R.id.fab_new_item_by_scan);
        fabNewItemByScan.setOnClickListener(v -> {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            PackageManager packageManager = getPackageManager();
            List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
            boolean isIntentSafe = activities.size() > 0;
            if (isIntentSafe) {
                startActivityForResult(intent, 0);
            } else {
                Toast.makeText(this, getString(R.string.error_no_barcode_scanner_available), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initCategorySpinner() {
        Spinner spinnerFilterByCategory = findViewById(R.id.spinner_filter_by_category);
        itemCategories.add(CATEGORY_ALL); // initialized because list can't be empty in spinner
        ArrayAdapter<CharSequence> spinnerCategoryAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, itemCategories);
        spinnerFilterByCategory.setAdapter(spinnerCategoryAdapter);
        spinnerFilterByCategory.setOnItemSelectedListener(this);

        itemViewModel.getLiveDataItemCategories().observe(this, categories -> {
            itemCategories.clear();
            itemCategories.add(CATEGORY_ALL);
            itemCategories.addAll(categories);

            if (!itemCategories.get(spinnerCategorySelectedPosition).equals(categorySelected)) {
                spinnerCategorySelectedPosition = itemCategories.indexOf(categorySelected);
            }

            categoriesLiveDataObtained = true;
            fadeOutProgressBar();
        });
    }

    private void initItemListRecyclerView() {
        recyclerViewAdapter = new ItemListRecyclerViewAdapter(this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemViewModel.getLiveDataItems().observe(this, items -> {
            this.items.clear();
            this.items.addAll(items);
            Collections.sort(this.items, new Comparator<Item>() {
                @Override
                public int compare(Item o1, Item o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });

            itemsLiveDataObtained = true;
            fadeOutProgressBar();
            updateRecyclerViewDisplayedItems();
        });
    }

    private void initProgressBarFragment() {
        Fragment loadingFragment = new LoadingFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, loadingFragment).commit();
    }

    private void getAllItemsFromService() {
        new GetAllItemsAsync(this, itemViewModel.getItemRepository()).execute();
    }

    @Override
    public void onItemLongClick(int position) {
        Intent intent = new Intent(this, ItemDetailsActivity.class);
        intent.putExtra(ITEM_ID, shownItems.get(position).getId());
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intentResult) {
        super.onActivityResult(requestCode, resultCode, intentResult);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String scannedBarCode = intentResult.getStringExtra(SCAN_RESULT);
                itemViewModel.getLiveDataItemByBarCode(scannedBarCode).observe(this, item -> {
                    Intent intent;
                    if (item != null) {
                        intent = new Intent(this, ItemDetailsActivity.class);
                        intent.putExtra(ITEM_ID, item.getId());
                    } else {
                        intent = new Intent(this, EditItemActivity.class);
                        intent.putExtra(BAR_CODE, scannedBarCode);
                    }
                    startActivity(intent);
                });
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, getString(R.string.error_scan_cancelled), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerCategorySelectedPosition = position;
        categorySelected = itemCategories.get(position);
        updateRecyclerViewDisplayedItems();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        spinnerCategorySelectedPosition = 0;
        categorySelected = CATEGORY_ALL;
        updateRecyclerViewDisplayedItems();
    }

    private void updateRecyclerViewDisplayedItems() {
        shownItems.clear();
        if (spinnerCategorySelectedPosition == 0) {
            shownItems.addAll(this.items);
        } else {
            for (Item item : items) {
                if (item.getCategory().contentEquals(itemCategories.get(spinnerCategorySelectedPosition))) {
                    shownItems.add(item);
                }
            }
        }
        recyclerViewAdapter.setItems(shownItems);
    }

    private void fadeOutProgressBar() {
        if (!progressBarFadedOut && getAllItemsCallSuccessful &&
                categoriesLiveDataObtained && itemsLiveDataObtained) {
            animateTransitionBetweenViews(recyclerView, frameLayout);
            progressBarFadedOut = true;
        }
    }

    @Override
    public void onSuccess() {
        getAllItemsCallSuccessful = true;
        fadeOutProgressBar();
        Toast.makeText(this, getString(R.string.info_synchronization_completed), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure() {
        Fragment connectionErrorFragment = new ConnectionErrorFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, connectionErrorFragment).commit();
    }
}
