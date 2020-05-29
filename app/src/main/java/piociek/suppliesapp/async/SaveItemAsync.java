package piociek.suppliesapp.async;

import android.os.AsyncTask;

import piociek.suppliesapp.domain.Item;
import piociek.suppliesapp.repository.ItemRepository;
import piociek.suppliesapp.repository.SuppliesBEServiceRepository;

public class SaveItemAsync extends AsyncTask<Void, Void, Void> {

    private ItemRepository itemRepository;
    private Item item;

    public SaveItemAsync(ItemRepository itemRepository, Item item) {
        this.itemRepository = itemRepository;
        this.item = item;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        SuppliesBEServiceRepository
                .getInstance(itemRepository.getContext())
                .saveItem(itemRepository, item);
        return null;
    }
}
