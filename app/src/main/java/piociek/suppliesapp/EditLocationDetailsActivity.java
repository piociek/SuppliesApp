package piociek.suppliesapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import piociek.suppliesapp.async.DeleteLocationDetailsAsync;
import piociek.suppliesapp.async.SaveLocationDetailsAsync;
import piociek.suppliesapp.domain.Item;
import piociek.suppliesapp.domain.LocationDetails;
import piociek.suppliesapp.domain.PickerDate;
import piociek.suppliesapp.fragments.LoadingFragment;
import piociek.suppliesapp.util.Animation;
import piociek.suppliesapp.util.ItemUtils;
import piociek.suppliesapp.view.ItemViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static piociek.suppliesapp.constants.IntentExtra.ITEM_ID;
import static piociek.suppliesapp.constants.IntentExtra.LOCATION_DETAILS_ID;

public class EditLocationDetailsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private String NEW_LOCATION_DETAILS_NAME;

    private ItemViewModel itemViewModel;
    private String itemId;
    private String locationDetailsId;
    private LocationDetails locationDetails;

    private LinearLayout viewContent;
    private Spinner spinnerName;
    private EditText count;
    private EditText name;
    private DatePicker datePicker;

    private List<CharSequence> locationDetailNames = new ArrayList<>();
    private int spinnerLocationNamePosition = 0;
    private boolean progressBarFadedOut;
    private boolean itemLocationsDataObtained;
    private boolean locationDataObtained;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item_location);

        if (!getIntent().hasExtra(ITEM_ID)) {
            Toast.makeText(this, getString(R.string.error_missing_id), Toast.LENGTH_SHORT).show();
            finish();
        } else {
            itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);

            handleIncomingIntent();
            initUi();
            initLocationNamesSpinner();
        }
    }

    private void initUi() {
        NEW_LOCATION_DETAILS_NAME = getString(R.string.add_new_location_details_name);

        viewContent = findViewById(R.id.edit_location_content);
        viewContent.setVisibility(View.GONE);
        Fragment loadingFragment = new LoadingFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, loadingFragment).commit();

        count = findViewById(R.id.edit_location_count);
        name = findViewById(R.id.edit_location_name);
        datePicker = findViewById(R.id.date_picker_exp_date);

        Button saveLocationDetailsButton = findViewById(R.id.button_save_item_location);
        // TODO Add validation and redo!
        saveLocationDetailsButton.setOnClickListener(v -> {
            int setCount = Integer.parseInt(count.getText().toString());
            if (saveLocationDetails(setCount)) {
                LocationDetails locationDetails = LocationDetails.builder()
                        .id(locationDetailsId == null ? ItemUtils.getRandomId() : locationDetailsId)
                        .name(
                                spinnerLocationNamePosition == 0 ?
                                        name.getText().toString()
                                        :
                                        locationDetailNames.get(spinnerLocationNamePosition).toString())
                        .count(setCount)
                        .expDate(
                                PickerDate.builder()
                                        .day(datePicker.getDayOfMonth())
                                        .month(datePicker.getMonth() + 1)
                                        .year(datePicker.getYear())
                                        .build()
                        )
                        .build();
                new SaveLocationDetailsAsync(itemViewModel.getItemRepository(), itemId, locationDetails).execute();
            } else if (deleteLocationDetails(setCount, locationDetailsId)) {
                new DeleteLocationDetailsAsync(itemViewModel.getItemRepository(), itemId, locationDetailsId).execute();
            } else {
                // show validation errors
            }
            finish();
        });
    }

    private void initLocationNamesSpinner() {
        locationDetailNames.add(NEW_LOCATION_DETAILS_NAME); // initialized because list can't be empty in spinner
        spinnerName = findViewById(R.id.spinner_select_location_name);
        ArrayAdapter<CharSequence> spinnerCategoryAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, locationDetailNames);
        spinnerName.setAdapter(spinnerCategoryAdapter);
        spinnerName.setOnItemSelectedListener(this);

        itemViewModel.getLiveDataItems().observe(this, items -> {
            locationDetailNames.clear();
            locationDetailNames.add(NEW_LOCATION_DETAILS_NAME);

            Set<String> namesSet = new TreeSet<>();
            for (Item item : items) {
                if (item.getLocationDetails() != null) {
                    for (LocationDetails locationDetails : item.getLocationDetails()) {
                        if (locationDetails != null) {
                            namesSet.add(locationDetails.getName());
                        }
                    }
                }
            }
            List<String> namesList = new ArrayList<>(namesSet);
            Collections.sort(namesList);
            locationDetailNames.addAll(namesList);

            itemLocationsDataObtained = true;
            initLocationDetails();
        });
    }

    private void handleIncomingIntent() {
        itemId = getIntent().getStringExtra(ITEM_ID);

        if (getIntent().hasExtra(LOCATION_DETAILS_ID)) {
            locationDetailsId = getIntent().getStringExtra(LOCATION_DETAILS_ID);
            itemViewModel.getLiveDataItemById(itemId).observe(this,
                    item -> {
                        for (LocationDetails ld : item.getLocationDetails()) {
                            if (ld.getId().equals(locationDetailsId)) {
                                setLocationDetails(ld);
                                break;
                            }
                        }
                    });
        } else {
            setLocationDetails(new LocationDetails());
        }
    }

    private boolean saveLocationDetails(int count) {
        return count > 0;
    }

    private boolean deleteLocationDetails(int count, String locationDetailsId) {
        return count <= 0 && locationDetailsId != null;
    }

    private void setLocationDetails(LocationDetails locationDetails) {
        this.locationDetails = locationDetails;
        locationDataObtained = true;
        initLocationDetails();
    }

    private void initLocationDetails() {
        if (locationDataObtained && itemLocationsDataObtained) {
            if (locationDetails.getCount() != null) {
                count.setText(String.valueOf(locationDetails.getCount()));
            }
            if (locationDetails.getName() != null) {
                spinnerLocationNamePosition = locationDetailNames.indexOf(locationDetails.getName());
                spinnerName.setSelection(spinnerLocationNamePosition);
            }
            if (locationDetails.getExpDate() != null) {
                datePicker.updateDate(
                        locationDetails.getExpDate().getYear(),
                        locationDetails.getExpDate().getMonth() - 1,
                        locationDetails.getExpDate().getDay());
            }
            fadeOutProgressBar();
        }
    }

    private void fadeOutProgressBar() {
        if (!progressBarFadedOut && itemLocationsDataObtained && locationDataObtained) {
            Animation.animateTransitionBetweenViews(viewContent, findViewById(R.id.fragment_container));
            progressBarFadedOut = true;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerLocationNamePosition = position;
        if (position == 0) {
            name.setVisibility(View.VISIBLE);
        } else {
            name.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        name.setVisibility(View.VISIBLE);
    }
}
