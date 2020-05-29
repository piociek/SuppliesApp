package piociek.suppliesapp.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import piociek.suppliesapp.domain.Item;

import java.util.List;

@Dao
public interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Item item);

    @Query("DELETE from items")
    void deleteAll();

    @Query("SELECT * from items")
    LiveData<List<Item>> getAllItems();

    @Query("SELECT * from items where id like :id")
    LiveData<Item> getLiveDataItemById(String id);

    @Query("SELECT * from items where barCode like :barCode")
    LiveData<Item> getLiveDataItemByBarCode(String barCode);

    @Query("SELECT * from items where id like :id")
    Item getItemById(String id);

    @Query("SELECT DISTINCT category from items ORDER BY LOWER(category) ASC")
    LiveData<List<String>> getLiveDataItemCategories();
}
