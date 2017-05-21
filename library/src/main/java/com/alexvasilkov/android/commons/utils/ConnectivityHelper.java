package com.alexvasilkov.android.commons.utils;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

import java.util.HashMap;

/**
 * Connectivity helper provides simple API to listen for network connectivity state.<br/>
 * Method {@link #isConnected(Context)} can be used to directly check connection.<br/>
 * Requires <code>android.permission.ACCESS_NETWORK_STATE</code> permission.
 */
@SuppressWarnings({ "WeakerAccess", "unused" }) // Public API
public class ConnectivityHelper {

    private static final HashMap<ConnectivityListener, ConnectivityReceiver> receiversMap =
            new HashMap<>();

    private ConnectivityHelper() {}

    /**
     * Be sure to remove receiver at appropriate time (i.e. in Activity.onPause()).
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public static void register(@NonNull Context context, @NonNull ConnectivityListener listener) {
        if (receiversMap.containsKey(listener)) {
            throw new RuntimeException("Connectivity listener " + listener
                    + " is already registered");
        }

        final ConnectivityReceiver receiver = new ConnectivityReceiver(listener);
        receiversMap.put(listener, receiver);
        context.registerReceiver(receiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        receiver.notify(isConnected(context));
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public static void unregister(@NonNull Context context,
            @NonNull ConnectivityListener listener) {

        final ConnectivityReceiver receiver = receiversMap.remove(listener);
        if (receiver != null) {
            context.unregisterReceiver(receiver);
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public static boolean isConnected(Context context) {
        final ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    public interface ConnectivityListener {
        void onConnectivityChange(boolean isConnected);
    }

    private static class ConnectivityReceiver extends BroadcastReceiver {

        private final ConnectivityListener listener;
        private Boolean lastConnectedStatus;

        ConnectivityReceiver(ConnectivityListener listener) {
            this.listener = listener;
        }

        void notify(boolean isConnected) {
            if (lastConnectedStatus == null || lastConnectedStatus != isConnected) {
                lastConnectedStatus = isConnected;
                listener.onConnectivityChange(lastConnectedStatus);
            }
        }

        @Override
        @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
        public void onReceive(@NonNull Context context, @NonNull Intent intent) {
            notify(isConnected(context));
        }

    }

}
