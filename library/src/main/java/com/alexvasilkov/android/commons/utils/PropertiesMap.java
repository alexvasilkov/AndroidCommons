package com.alexvasilkov.android.commons.utils;

import android.content.Context;
import android.util.Log;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * Helper class to read various properties from assets/*.properties file
 */
public class PropertiesMap {

    private final Map<String, String> map = new HashMap<String, String>();

    public static PropertiesMap get(Context context, String fileName) {
        return new PropertiesMap().read(context, fileName);
    }

    private PropertiesMap() {
    }

    /**
     * Reads properties from given *.properties file from assets folder and stores it as a map
     */
    private PropertiesMap read(Context context, String fileName) {
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
        } catch (Exception e) {
            Log.e(PropertiesMap.class.getSimpleName(), "Error reading properties file: " + fileName, e);
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (Exception ignored) {
                }
        }

        return this;
    }

    public Map<String, String> asMap() {
        return map;
    }

    public String getString(String key) {
        return getString(key, null, false);
    }

    public String getString(String key, String defaultValue) {
        return getString(key, defaultValue, false);
    }

    public String getStringRequired(String key) {
        return getString(key, null, true);
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return getBoolean(key, defaultValue, false);
    }

    public boolean getBooleanRequired(String key) {
        return getBoolean(key, false, true);
    }

    public int getInt(String key) {
        return getInt(key, 0, false);
    }

    public int getInt(String key, int defaultValue) {
        return getInt(key, defaultValue, false);
    }

    public int getIntRequired(String key) {
        return getInt(key, 0, true);
    }


    /* Helper methods */

    private String getString(String key, String defaultValue, boolean required) {
        String value = map.get(key);
        if (value == null || value.length() == 0) {
            if (required) {
                throw new NullPointerException("Property value for key '" + key + "' is required");
            } else {
                return defaultValue == null ? value : defaultValue;
            }
        } else {
            return value;
        }
    }

    private boolean getBoolean(String key, boolean defaultValue, boolean required) {
        String value = getString(key, null, required);
        if (value == null || value.length() == 0) return defaultValue;

        if ("true".equalsIgnoreCase(value)) {
            return true;
        } else if ("false".equalsIgnoreCase(value)) {
            return false;
        } else {
            throw new IllegalArgumentException("Boolean property value '"
                    + value + "' for key '" + key + "' is not valid");
        }
    }

    private int getInt(String key, int defaultValue, boolean required) {
        String value = getString(key, null, required);
        if (value == null || value.length() == 0) return defaultValue;

        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Integer property value '"
                    + value + "' for key '" + key + "' is not valid");
        }
    }

}
