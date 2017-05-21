package com.alexvasilkov.android.commons.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;

@SuppressWarnings({ "WeakerAccess", "unused" }) // Public API
public final class TelephonyHelper {

    public static boolean canPerformCall(@NonNull Context context) {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            return false;
        }

        final TelephonyManager manager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);

        return manager.getSimState() == TelephonyManager.SIM_STATE_READY
                && manager.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }

    public static boolean canSendSms(Context context) {
        if (!canPerformCall(context)) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context);
            return defaultSmsPackageName != null;
        } else {
            return true;
        }
    }

    private TelephonyHelper() {}

}
