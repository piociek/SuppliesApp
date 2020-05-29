package piociek.suppliesapp.async;

import android.os.AsyncTask;

import piociek.suppliesapp.domain.Item;
import piociek.suppliesapp.repository.ItemRepository;
import piociek.suppliesapp.repository.SuppliesBEServiceRepository;

public class DeleteLocationDetailsAsync extends AsyncTask<Void, Void, Void> {

    private ItemRepository itemRepository;
    private String itemId;
    private String locationDetailsId;

    public DeleteLocationDetailsAsync(ItemRepository itemRepository, String itemId, String locationDetailsId) {
        this.itemRepository = itemRepository;
        this.itemId = itemId;
        this.locationDetailsId = locationDetailsId;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        SuppliesBEServiceRepository
                .getInstance(itemRepository.getContext())
                .deleteLocationDetails(itemRepository, itemId, locationDetailsId);
        return null;
    }
}
