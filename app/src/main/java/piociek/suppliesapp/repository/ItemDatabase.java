package piociek.suppliesapp.repository;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import piociek.suppliesapp.domain.Item;

@Database(entities = {Item.class}, version = 1, exportSchema = false)
public abstract class ItemDatabase extends RoomDatabase {

    public abstract ItemDao itemDao();

    private static volatile ItemDatabase instance;

    static ItemDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (ItemDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            ItemDatabase.class,
                            "items").build();
                }
            }
        }
        return instance;
    }
}
