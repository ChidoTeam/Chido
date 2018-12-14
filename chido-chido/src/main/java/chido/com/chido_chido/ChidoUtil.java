package chido.com.chido_chido;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;

import java.util.List;

public class ChidoUtil {
    private static final String PREFERENCE_NAME = "chido_preferences";
    public static final String USSD_IN_SESSION = "ussd_in_session";
    public static final String USSD_PHONE_NUMBER = "chido_ussd_phone_number";
    public static final String USSD_AMOUNT = "chido_ussd_amount";
    public static final String ENTER_PHONE_NUMBER = "chido_enter_phone_number";
    public static final String ENTER_AMOUNT = "chido_enter_amount";
    private static final String TAG = "ChidoUtil";
    public static final String USSDServiceID = "chido.com.chido/chido.com.chido_chido.USSDService";
    public static final String PHONE_NUMBER_REGEX = "^((256)|(250))[347][012345789][0-9]\\d{6}$";
    public static final String MTN_CO_PHONE_NUMBER_REGEX = "^((256))[3][129][0-9]\\d{6}$";
    public static final String MTN_PHONE_NUMBER_REGEX = "^((256))7[78][0-9]\\d{6}$";
    public static final String AIRTEL_PHONE_NUMBER_REGEX = "^((256))7[05][0-9]\\d{6}$";
    public static final String AFRICEL_PHONE_NUMBER_REGEX = "^((256))79[0-9]\\\\d{6}$";
    public static final String UTL_PHONE_NUMBER_REGEX = "^((256))71[0-9]\\d{6}$";
    public static final String MTN = "MTN";
    public static final String UTL = "UTL";
    public static final String AIRTEL = "AIRTEL";
    public static final String AFRICEL = "AFRICEL";
    public static final String UNKNOWN = "UNKNOWN";

    /**
     * @param key
     * @param defValue
     * @return
     */
    public static String getPref(String key, String defValue,Context context) {
        SharedPreferences mSharedPreferences;
        mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        return mSharedPreferences.getString(key, defValue);

    }


    /**
     * @param key
     * @param value
     */
    public static void savePref(String key, String value,Context context) {
        SharedPreferences mSharedPreferences;
        mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        mSharedPreferences.edit().putString(key, value).apply();
    }

    /**
     * @param key
     * @param value
     */
    public static void savePrefBoolean(String key, Boolean value,Context context) {
        SharedPreferences mSharedPreferences;
        mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        mSharedPreferences.edit().putBoolean(key, value).apply();
    }

    /**
     * @param key
     * @return
     */
    public static boolean getPrefBoolean(String key,Context context) {

        SharedPreferences mSharedPreferences;
        mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        return mSharedPreferences.getBoolean(key, false);

    }

    public static void logInstalledAccessiblityServices(Context context) {

        AccessibilityManager am = (AccessibilityManager) context
                .getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = am
                .getInstalledAccessibilityServiceList();
        for (AccessibilityServiceInfo service : runningServices) {
            Log.i(TAG, service.getId());
        }
    }

    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    public static void dismissDialog(ProgressDialog progress, Activity activity){
        if (!activity.isFinishing() && progress != null) {
            progress.dismiss();
        }
    }

    public static String getNetworkFromPhoneNumber(String phoneNumber){
        if(phoneNumber.matches(MTN_CO_PHONE_NUMBER_REGEX)){
            return MTN;
        }else if(phoneNumber.matches(MTN_PHONE_NUMBER_REGEX)){
            return MTN;
        }else if(phoneNumber.matches(AIRTEL_PHONE_NUMBER_REGEX)){
            return AIRTEL;
        }else if(phoneNumber.matches(AFRICEL_PHONE_NUMBER_REGEX)){
            return AFRICEL;
        }else if(phoneNumber.matches(UTL_PHONE_NUMBER_REGEX)){
            return UTL;
        }else{
            return UNKNOWN;
        }
    }

    public static String getNetworkFromOperator(String rawOperator){
        String operator = rawOperator.toLowerCase();
        if(operator.contains("mtn")){
            return MTN;
        }else if(operator.contains("airtel")){
            return AIRTEL;
        }else if(operator.contains("africel")){
            return AFRICEL;
        }else if(operator.contains("utl")){
            return UTL;
        }else{
            return UNKNOWN;
        }
    }
}
