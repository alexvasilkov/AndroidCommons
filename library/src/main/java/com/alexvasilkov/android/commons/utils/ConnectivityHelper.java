package com.alexvasilkov.android.commons.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import java.util.HashMap;

/**
 * Connectivity helper provides simple API to listen for network connected / disconnected state.
 * <p/>
 * You can also use {@link #isConnected()} method but you'll need to register default listener first
 * with {@link #registerDefault(android.content.Context)}.
 * <p/>
 * Requires <code>android.permission.ACCESS_NETWORK_STATE</code> permission
 */
public final class ConnectivityHelper {

    private static final HashMap<String, ConnectivityListener> sReceiversMap = new HashMap<>();
    private static boolean sIsRegistered = false;
    private static boolean sIsConnected = true;

    /**
     * Be sure to remove receiver at appropriate time (i.e. in Activity.onPause()).
     */
    public static synchronized void register(Context context, ConnectivityListener listener) {
        synchronized (sReceiversMap) {
            sReceiversMap.put(context.toString(), listener);
        }
        registerIfNeeded(context);
    }

    public static synchronized void unregister(Context context) {
        sReceiversMap.remove(context.toString());
    }

    public static synchronized void registerDefault(Context appContext) {
        registerIfNeeded(appContext);
    }

    private static void registerIfNeeded(Context context) {
        if (!sIsRegistered) {
            ConnectivityReceiver receiver = new ConnectivityReceiver(new ConnectivityListener() {
                @Override
                public void onConnectionLost() {
                    synchronized (sReceiversMap) {
                        for (ConnectivityListener l : sReceiversMap.values()) {
                            l.onConnectionLost();
                        }
                    }
                }

                @Override
                public void onConnectionEstablished() {
                    synchronized (sReceiversMap) {
                        for (ConnectivityListener l : sReceiversMap.values()) {
                            l.onConnectionEstablished();
                        }
                    }
                }
            });
            context.registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            sIsRegistered = true;
        }
    }

    public static boolean isConnected() {
        return sIsConnected;
    }

    public interface ConnectivityListener {
        /**
         * Called on the UI thread when connection established (network is available).
         */
        void onConnectionEstablished();

        /**
         * Called on the UI thread when connection lost (network is unavailable).
         */
        void onConnectionLost();
    }

    private static class ConnectivityReceiver extends BroadcastReceiver {

        private final ConnectivityListener mConnectivityListener;

        private ConnectivityReceiver(ConnectivityListener connectivityListener) {
            if (connectivityListener == null) throw new NullPointerException();
            this.mConnectivityListener = connectivityListener;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onReceive(@NonNull Context context, @NonNull Intent intent) {
            if (!ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) return;

            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if (isConnected != sIsConnected) {
                sIsConnected = isConnected;

                if (isConnected) {
                    mConnectivityListener.onConnectionEstablished();
                } else {
                    mConnectivityListener.onConnectionLost();
                }
            }
        }

    }

    private ConnectivityHelper() {
    }

}
