package com.alexvasilkov.android.commons.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Telephony;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.SparseArray;

public final class TelephonyHelper {

    private static SparseArray<SimStateListener> sPhoneListenersMap = new SparseArray<SimStateListener>();
    private static int sPhoneListenerLastId;

	public static synchronized int register(Context context, OnSimStateListener listener) {
		SimStateListener phoneListener = new SimStateListener(listener);
		sPhoneListenersMap.put(++sPhoneListenerLastId, phoneListener);

		((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).listen(phoneListener,
				PhoneStateListener.LISTEN_SERVICE_STATE);
		return sPhoneListenerLastId;
	}

	public static synchronized void unregister(Context context, int listenerId) {
		SimStateListener phoneListener = sPhoneListenersMap.get(listenerId);
		if (phoneListener == null) return;
		((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).listen(phoneListener, PhoneStateListener.LISTEN_NONE);
	}

    public static boolean canPerformCall(Context context) {
        boolean canPerformCall = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
        if (!canPerformCall) return false;

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return TelephonyManager.SIM_STATE_READY == tm.getSimState() && (TelephonyManager.PHONE_TYPE_NONE != tm.getPhoneType());
    }

    public static boolean canSendSms(Context context) {
        if (!canPerformCall(context)) return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context);
            return defaultSmsPackageName != null;
        } else {
            return true;
        }
    }


    private static class SimStateListener extends PhoneStateListener {

		private OnSimStateListener mListener;
		private ServiceState mServiceState;

		private SimStateListener(OnSimStateListener listener) {
			this.mListener = listener;
		}

		@Override
		public void onServiceStateChanged(ServiceState serviceState) {
			if (mServiceState == null || !mServiceState.equals(serviceState)) {
				mServiceState = serviceState;
				boolean connected = ServiceState.STATE_IN_SERVICE == serviceState.getState();
				mListener.onSimState(connected);
			}
		}

	}

	public static interface OnSimStateListener {
		void onSimState(boolean connected);
	}

	private TelephonyHelper() {}

}
