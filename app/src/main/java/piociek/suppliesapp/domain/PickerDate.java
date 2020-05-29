package piociek.suppliesapp.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PickerDate {
    private Integer day;
    private Integer month;
    private Integer year;

    @Override
    public String toString() {
        return year + "-" + toDoubleDigitString(month) + "-" + toDoubleDigitString(day);
    }

    private String toDoubleDigitString(int i) {
        return i < 10 ? "0" + i : String.valueOf(i);
    }

}
