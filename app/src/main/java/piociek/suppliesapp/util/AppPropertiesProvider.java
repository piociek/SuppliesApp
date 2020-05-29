package piociek.suppliesapp.util;

import android.content.Context;
import android.content.Intent;

import java.util.Properties;

public class AppPropertiesProvider {

    private static final String DEFAULT_PROPERTIES_FILE = "app.properties";

    private static Properties appProperties;

    private AppPropertiesProvider() {
    }

    private static void initAppProperties(final Context context) {
        if (appProperties == null) {
            PropertiesReader propertiesReader = new PropertiesReader(context);
            appProperties = propertiesReader.getProperties(DEFAULT_PROPERTIES_FILE);
        }
    }

    public static String getProperty(final Context context, String propertyName) {
        if (appProperties == null) {
            initAppProperties(context);
        }
        return appProperties.getProperty(propertyName);
    }

    public static int getPropertyAsInt(final Context context, String propertyName) {
        if (appProperties == null) {
            initAppProperties(context);
        }
        return Integer.parseInt(appProperties.getProperty(propertyName));
    }
}
