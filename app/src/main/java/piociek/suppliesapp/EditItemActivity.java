package piociek.suppliesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import piociek.suppliesapp.async.SaveItemAsync;
import piociek.suppliesapp.util.Animation;
import piociek.suppliesapp.domain.Item;
import piociek.suppliesapp.fragments.LoadingFragment;
import piociek.suppliesapp.util.ItemUtils;
import piociek.suppliesapp.view.ItemViewModel;

import java.util.ArrayList;
import java.util.List;

import static piociek.suppliesapp.constants.IntentExtra.BAR_CODE;
import static piociek.suppliesapp.constants.IntentExtra.ITEM_ID;

public class EditItemActivity
        extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    private String NEW_CATEGORY;

    private ItemViewModel itemViewModel;

    private Item item = new Item();
    private List<CharSequence> itemCategories = new ArrayList<>();

    private LinearLayout viewContent;
    private EditText name;
    private EditText barCode;
    private EditText packaging;
    private EditText category;
    private Spinner spinnerCategory;

    private int spinnerCategoryPosition = 0;

    private boolean progressBarFadedOut;
    private boolean categoriesLiveDataObtained;
    private boolean itemDataObtained;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);

        initUi();
        initCategoriesSpinner();
        handleIncomingIntent();
    }

    private void initUi() {
        NEW_CATEGORY = getString(R.string.add_new_category);

        viewContent = findViewById(R.id.edit_item_content);
        Button saveItemButton = findViewById(R.id.edit_save_item);
        Button saveItemAndGoToLocationDetailsButton = findViewById(R.id.edit_save_and_go_location_details);
        name = findViewById(R.id.edit_item_name);
        barCode = findViewById(R.id.edit_item_bar_code);
        packaging = findViewById(R.id.edit_item_packaging);
        category = findViewById(R.id.edit_item_category);
        spinnerCategory = findViewById(R.id.spinner_select_category);

        saveItemButton.setOnClickListener(v -> {
            getItemDetailsFromFields();
            new SaveItemAsync(itemViewModel.getItemRepository(), item).execute();
            finish();
        });

        saveItemAndGoToLocationDetailsButton.setOnClickListener(v -> {
            getItemDetailsFromFields();
            new SaveItemAsync(itemViewModel.getItemRepository(), item).execute();
            Intent intent = new Intent(this, EditLocationDetailsActivity.class);
            intent.putExtra(ITEM_ID, this.item.getId());
            startActivity(intent);
            finish();
        });
    }

    private void initCategoriesSpinner() {
        itemCategories.add(NEW_CATEGORY);
        ArrayAdapter<CharSequence> spinnerCategoryAdapter =
                new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, itemCategories);
        spinnerCategory.setAdapter(spinnerCategoryAdapter);
        spinnerCategory.setOnItemSelectedListener(this);

        itemViewModel.getLiveDataItemCategories().observe(this, categories -> {
            itemCategories.clear();
            itemCategories.add(NEW_CATEGORY);
            itemCategories.addAll(categories);

            categoriesLiveDataObtained = true;
            initItem();
        });
    }

    private void handleIncomingIntent() {
        if (getIntent().hasExtra(ITEM_ID)) {
            viewContent.setVisibility(View.GONE);
            Fragment loadingFragment = new LoadingFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, loadingFragment).commit();
            String id = getIntent().getStringExtra(ITEM_ID);

            itemViewModel.getLiveDataItemById(id).observe(this, this::setItem);
        } else if (getIntent().hasExtra(BAR_CODE)) {
            String barCode = getIntent().getStringExtra(BAR_CODE);
            setItem(Item.builder()
                    .id(ItemUtils.getRandomId())
                    .barCode(barCode)
                    .build());
        } else {
            setItem(Item.builder()
                    .id(ItemUtils.getRandomId())
                    .build());
        }
    }

    private void setItem(Item item){
        itemDataObtained = true;
        this.item = item;
        initItem();
    }

    private void initItem() {
        if (categoriesLiveDataObtained && itemDataObtained){
            name.setText(item.getName());
            barCode.setText(item.getBarCode());
            packaging.setText(item.getPackaging());
            if (item.getCategory() != null){
                spinnerCategoryPosition = itemCategories.indexOf(item.getCategory());
            }
            spinnerCategory.setSelection(spinnerCategoryPosition);

            fadeOutProgressBar();
        }
    }

    private void getItemDetailsFromFields() {
        item.setName(name.getText().toString());
        item.setBarCode(barCode.getText().toString());
        item.setCategory(
                spinnerCategoryPosition == 0 ?
                        category.getText().toString()
                        :
                        itemCategories.get(spinnerCategoryPosition).toString());
        item.setPackaging(packaging.getText().toString());
    }

    private void fadeOutProgressBar(){
        if (!progressBarFadedOut && categoriesLiveDataObtained && itemDataObtained){
            Animation.animateTransitionBetweenViews(viewContent, findViewById(R.id.fragment_container));
            progressBarFadedOut = true;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerCategoryPosition = position;
        if (position == 0) {
            category.setVisibility(View.VISIBLE);
        } else {
            category.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        category.setVisibility(View.VISIBLE);
    }
}
