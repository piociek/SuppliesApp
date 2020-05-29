package piociek.suppliesapp.domain;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(tableName = "items")
public class Item {

    @PrimaryKey
    @ColumnInfo
    @NonNull
    private String id;
    @ColumnInfo
    private String barCode;
    @ColumnInfo
    private String category;
    @ColumnInfo
    private String name;
    @ColumnInfo
    private String packaging;
    @ColumnInfo
    @TypeConverters(LocationDetailsDataConverter.class)
    private List<LocationDetails> locationDetails;

    public int getTotalCount() {
        if (locationDetails != null) {
            int count = 0;
            for (LocationDetails ld : locationDetails) {
                count += ld.getCount();
            }
            return count;
        }
        return 0;
    }

    public String getShortestExpDate() {
        if (locationDetails != null && locationDetails.size() > 0) {
            PickerDate shorter = null;
            for (LocationDetails ld : locationDetails) {
                if (shorter == null) {
                    shorter = ld.getExpDate();
                }
                if (shorter.getYear() < ld.getExpDate().getYear()
                        && shorter.getMonth() < ld.getExpDate().getMonth()
                        && shorter.getDay() < ld.getExpDate().getDay()) {
                    shorter = ld.getExpDate();
                }
            }
            return shorter.toString();
        }
        return "";
    }
}
