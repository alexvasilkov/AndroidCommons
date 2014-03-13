package com.azcltd.fluffycommons.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

import java.util.HashMap;

public final class ConnectivityHelper {

    private static HashMap<String, ConnectivityReceiver> sReceiversMap = new HashMap<String, ConnectivityReceiver>();
    private static boolean sIsConnected = true;

    /**
     * Be sure to remove receiver at appropriate time (i.e. in Activity.onPause()).
     */
    public static synchronized void register(Context context, ConnectivityListener listener) {
        ConnectivityReceiver receiver = new ConnectivityReceiver(listener);
        sReceiversMap.put(context.toString(), receiver);

        context.registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public static synchronized void unregister(Context context) {
        ConnectivityReceiver receiver = sReceiversMap.remove(context.toString());
        if (receiver != null) context.unregisterReceiver(receiver);
    }

    public static synchronized void registerDefault(Context appContext) {
        register(appContext, new ConnectivityListener() {
            @Override
            public void onConnectionLost() {
                if (sIsConnected) {
                    sIsConnected = false;
                }
            }

            @Override
            public void onConnectionEstablished() {
                if (!sIsConnected) {
                    sIsConnected = true;
                }
            }
        });
    }

    public static boolean isConnected() {
        return sIsConnected;
    }

    public static interface ConnectivityListener {
        /**
         * Called on the UI thread when connection established (network is available).
         */
        void onConnectionEstablished();

        /**
         * Called on the UI thread when connection lost (network is unavailable).
         */
        void onConnectionLost();
    }

    public static class ConnectivityReceiver extends BroadcastReceiver {

        private final ConnectivityListener mConnectivityListener;

        private ConnectivityReceiver(ConnectivityListener connectivityListener) {
            if (connectivityListener == null) throw new NullPointerException();
            this.mConnectivityListener = connectivityListener;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) return;

            if (intent.hasExtra(ConnectivityManager.EXTRA_NETWORK_INFO)) {
                NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

                State networkState = networkInfo.getState();
                boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

                if (noConnectivity) {
                    mConnectivityListener.onConnectionLost();
                } else if (networkState == State.CONNECTED) {
                    mConnectivityListener.onConnectionEstablished();
                }
            }
        }

    }

    private ConnectivityHelper() {}

}
