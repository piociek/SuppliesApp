package piociek.suppliesapp.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {
    private static final String TAG = "PropertiesReader";

    private Context context;
    private Properties properties;

    public PropertiesReader(Context context) {
        this.context = context;
        properties = new Properties();
    }

    public Properties getProperties(String fileName) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(fileName);
            properties.load(inputStream);
        } catch (IOException e) {
            Log.e(TAG, "getProperties: Unable to load properties file: " + fileName);
        }
        return properties;
    }
}
