package piociek.suppliesapp.repository;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

import lombok.Getter;
import piociek.suppliesapp.domain.Item;

public class ItemRepository {

    private ItemDao itemDao;
    @Getter
    private LiveData<List<Item>> items;
    @Getter
    private Context context;

    public ItemRepository(Application application) {
        ItemDatabase db = ItemDatabase.getDatabase(application);
        itemDao = db.itemDao();
        items = itemDao.getAllItems();
        context = application.getApplicationContext();
    }

    public LiveData<Item> getLiveDataItemById(String id) {
        return itemDao.getLiveDataItemById(id);
    }

    public LiveData<Item> getLiveDataItemByBarCode(String barCode) {
        return itemDao.getLiveDataItemByBarCode(barCode);
    }

    public LiveData<List<String>> getLiveDataItemCategories() {
        return itemDao.getLiveDataItemCategories();
    }

    public void insert(Item item) {
        itemDao.insert(item);
    }

    public void deleteAll() {
        itemDao.deleteAll();
    }
}
