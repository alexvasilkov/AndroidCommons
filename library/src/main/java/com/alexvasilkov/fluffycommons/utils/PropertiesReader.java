package com.alexvasilkov.fluffycommons.utils;

import android.content.Context;
import android.util.Log;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class PropertiesReader {

    /**
     * Reads properties from given *.properties file from assets folder and returns it as map
     */
    public static Map<String, String> read(Context context, String fileName) {
        Map<String, String> map = new HashMap<String, String>();
        InputStream in = null;
        try {
            in = context.getAssets().open(fileName);
            Properties props = new Properties();
            props.load(in);

            String name;
            for (String key : props.stringPropertyNames()) {
                name = key.toLowerCase(Locale.ENGLISH);
                map.put(name, props.getProperty(key));
            }

            return map;
        } catch (Exception e) {
            Log.e(PropertiesReader.class.getSimpleName(), "Error reading properties file: " + fileName, e);
            return map;
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (Exception ignored) {
                }
        }
    }

    private PropertiesReader() {
    }

}
