package piociek.suppliesapp.util;

public class ItemUtils {

    public static String getRandomId(){
        return String.valueOf(System.currentTimeMillis());
    }

    public static String getTextFromNullableOrDefault(String nullable, String defaultValue) {
        return nullable == null ? defaultValue : nullable;
    }
}
