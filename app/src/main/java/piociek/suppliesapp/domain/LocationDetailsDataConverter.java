package piociek.suppliesapp.domain;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class LocationDetailsDataConverter {

    private final Gson gson = new Gson();
    private final Type type = new TypeToken<List<LocationDetails>>() {
    }.getType();

    @TypeConverter
    public String fromLocationDetails(List<LocationDetails> locationDetails) {
        if (locationDetails == null) {
            return null;
        }
        return gson.toJson(locationDetails, type);
    }

    @TypeConverter
    public List<LocationDetails> toLocationDetails(String json) {
        if (json == null) {
            return null;
        }
        return gson.fromJson(json, type);
    }
}
