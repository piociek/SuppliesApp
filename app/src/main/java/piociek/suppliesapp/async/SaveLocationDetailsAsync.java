package piociek.suppliesapp.async;

import android.os.AsyncTask;

import piociek.suppliesapp.domain.LocationDetails;
import piociek.suppliesapp.repository.ItemRepository;
import piociek.suppliesapp.repository.SuppliesBEServiceRepository;

public class SaveLocationDetailsAsync extends AsyncTask<Void, Void, Void> {

    private ItemRepository itemRepository;
    private String itemId;
    private LocationDetails locationDetails;

    public SaveLocationDetailsAsync(ItemRepository itemRepository, String itemId, LocationDetails locationDetails) {
        this.itemRepository = itemRepository;
        this.itemId = itemId;
        this.locationDetails = locationDetails;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        SuppliesBEServiceRepository
                .getInstance(itemRepository.getContext())
                .saveLocationDetails(itemRepository, itemId, locationDetails);
        return null;
    }
}
