package piociek.suppliesapp.view;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import lombok.Getter;
import piociek.suppliesapp.domain.Item;
import piociek.suppliesapp.repository.ItemRepository;

public class ItemViewModel extends AndroidViewModel {

    @Getter
    private ItemRepository itemRepository;
    @Getter
    private LiveData<List<Item>> liveDataItems;

    public ItemViewModel(@NonNull Application application) {
        super(application);
        itemRepository = new ItemRepository(application);
        liveDataItems = itemRepository.getItems();
    }

    public LiveData<Item> getLiveDataItemById(String id) {
        return itemRepository.getLiveDataItemById(id);
    }

    public LiveData<Item> getLiveDataItemByBarCode(String barCode) {
        return itemRepository.getLiveDataItemByBarCode(barCode);
    }

    public LiveData<List<String>> getLiveDataItemCategories() {
        return itemRepository.getLiveDataItemCategories();
    }
}
