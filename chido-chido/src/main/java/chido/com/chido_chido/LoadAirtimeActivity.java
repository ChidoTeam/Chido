package chido.com.chido_chido;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class LoadAirtimeActivity extends AppCompatActivity {

    private static final String TAG = "LoadAirtimeActivity";
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    private static final int SMS_PERMISSION_CODE = 232;
    private Intent callIntent;
    private USSDTimer ussdTimer = new USSDTimer(LoadAirtimeActivity.this);
    ;
    private Button btnProceed;
    private EditText amount, phoneNumber;
    TelephonyManager mTelephonyManager;
    private final String URL = "http://52.40.167.195:9097/api/android_customer/transaction/load_chido/sync/initiate_payment";
    private TelephonyInfo telephonyInfo;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_airtime);
        amount = findViewById(R.id.amount);
        phoneNumber = findViewById(R.id.phone);
        btnProceed = findViewById(R.id.btnProceed);
        mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        ChidoUtil.logInstalledAccessiblityServices(LoadAirtimeActivity.this);
        SmsListener smsBroadcastReceiver = new SmsListener();
        registerReceiver(smsBroadcastReceiver, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
       boolean perm = isSmsPermissionGranted();
        if(!perm){
            showRequestPermissionsInfoAlertDialog();
        }
        setOnClickListeners();



    }

    private void setOnClickListeners() {
        //do something after button is pressed
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    // check amount
                     if (amount.getText().toString().isEmpty()) {
                         final AlertDialog alertDialog = new AlertDialog.Builder(LoadAirtimeActivity.this).create();
                         alertDialog.setTitle("");
                         alertDialog.setMessage(getString(R.string.enter_amount));
                         alertDialog.show();
                     // check phone number
                     }else if (phoneNumber.getText().toString().isEmpty()) {
                         final AlertDialog alertDialog = new AlertDialog.Builder(LoadAirtimeActivity.this).create();
                         alertDialog.setTitle("");
                         alertDialog.setMessage(getString(R.string.enter_phone_number));
                         alertDialog.show();
                    } else {
                         //check permissions
                         if (

                                  ActivityCompat.checkSelfPermission(LoadAirtimeActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                                 || ActivityCompat.checkSelfPermission(LoadAirtimeActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                                       //  || ActivityCompat.checkSelfPermission(LoadAirtimeActivity.this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED
                                         || ActivityCompat.checkSelfPermission(LoadAirtimeActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                               //          && ActivityCompat.checkSelfPermission(LoadAirtimeActivity.this, Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED
                           ) {

                          //   if (!ActivityCompat.shouldShowRequestPermissionRationale( LoadAirtimeActivity.this, "android.permission.CALL_PHONE")) {
                                 ActivityCompat.requestPermissions( LoadAirtimeActivity.this,
                                         new String[]{Manifest.permission.CALL_PHONE
                                         ,Manifest.permission.READ_PHONE_STATE
                                     //    ,Manifest.permission.READ_PHONE_NUMBERS
                                         ,Manifest.permission.READ_SMS
                                 //        ,"android.permission.SYSTEM_ALERT_WINDOW"
                                 }, 1);
                            // }


                         } else {
                             telephonyInfo = TelephonyInfo.getInstance(LoadAirtimeActivity.this);

                             /*
                             String mPhoneNumber = mTelephonyManager.getLine1Number();
                             String mPhoneNumber = mTelephonyManager.getCallCapablePhoneAccounts();
                             String operatorName = mTelephonyManager.getNetworkOperatorName();
                             String operator = mTelephonyManager.getNetworkOperator();
                             String simOperator = mTelephonyManager.getSimOperator();
                             String simOperatorName = mTelephonyManager.getSimOperatorName();
                             String imeiSIM1 = telephonyInfo.getImsiSIM1();
                             String imeiSIM2 = telephonyInfo.getImsiSIM2();
                             boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
                             boolean isSIM2Ready = telephonyInfo.isSIM2Ready();
                             boolean isDualSIM = telephonyInfo.isDualSIM();
                             Log.e("Telephony:",mPhoneNumber + " " + operatorName);
                             */

                             //format phone number
                             String phone = phoneNumber.getText().toString();
                             if(phone.startsWith("0")){
                                 phone = "256" + phoneNumber.getText().toString().substring(1);
                             } else if(phone.startsWith("7")){
                                 phone = "256" + phoneNumber.getText().toString();
                             }

                             String amountString = amount.getText().toString();
                             // validate phone number
                             if(!phone.matches(ChidoUtil.PHONE_NUMBER_REGEX)){
                                 final AlertDialog alertDialog = new AlertDialog.Builder(LoadAirtimeActivity.this).create();
                                 alertDialog.setTitle("");
                                 alertDialog.setMessage("invalid phone number");
                                 alertDialog.show();

                             }else {

                                 // check for mtn
                                 if(phone.matches(ChidoUtil.MTN_PHONE_NUMBER_REGEX) || phone.matches(ChidoUtil.MTN_CO_PHONE_NUMBER_REGEX)){


                                     //check if accesibility is enabled
                                     if(isAccessibilityEnabled()){

                                         Log.e(TAG,"USSD sERVICE IS ENABLED");

                                        // ChidoUtil.savePref(ChidoUtil.USSD_PHONE_NUMBER,phone.replaceFirst("256","0"),LoadAirtimeActivity.this);
                                         ChidoUtil.savePref(ChidoUtil.USSD_AMOUNT,amountString,LoadAirtimeActivity.this);
                                         startService(new Intent(LoadAirtimeActivity.this, USSDService.class));
                                         fetchAndStartUssd( phone, amountString);
                                     }else{
                                         new AlertDialog.Builder(LoadAirtimeActivity.this)
                                                 .setTitle("Enable Service!")
                                                 .setMessage("Please Enable " +ChidoUtil.getApplicationName(LoadAirtimeActivity.this)+ " Airtime Service")
                                                 .setIcon(android.R.drawable.ic_dialog_alert)
                                                 .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                                     public void onClick(DialogInterface dialog, int whichButton) {
                                                         openAccesibilityService();
                                                     }})
                                                 .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                                                     public void onClick(DialogInterface dialog, int whichButton) {
                                                         openAccesibilityService();
                                                     }}).show();
                                     }
                                 }else{
                                     fetchAndStartUssd( phone,amountString);
                                 }

                             }
                             /*
                             callIntent =
                              new Intent(Intent.ACTION_CALL, ussdToCallableUri("*100*7*4*0758054848*1000#"));
                             LoadAirtimeActivity.this.startActivity(LoadAirtimeActivity.this.callIntent);
                             */
                         }


                    }
            }
        });

    }

    public boolean isAccessibilityEnabled() {
        AccessibilityManager am = (AccessibilityManager)getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = am
                .getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
        for (AccessibilityServiceInfo service : runningServices) {
            if (ChidoUtil.USSDServiceID.equals(service.getId())) {
                return true;
            }
        }

        return false;
    }

    private void fetchAndStartUssd(String phone,String amount){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else {
         //   startService(new Intent(LoadAirtimeActivity.this, ProgressService.class));
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        startService(new Intent(LoadAirtimeActivity.this, USSDService.class));
        new WebService(callIntent, ussdTimer,LoadAirtimeActivity.this, phone, amount,telephonyInfo).execute(URL);
    }

    private void openAccesibilityService(){
        Toast.makeText(this, "Please Enable " +ChidoUtil.getApplicationName(LoadAirtimeActivity.this)+ " Airtime Service", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {

            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK) {
                startService(new Intent(LoadAirtimeActivity.this, ProgressService.class));
            } else { //Permission is not available
                Toast.makeText(this,
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show();

                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public boolean isSmsPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request runtime SMS permission
     */
    private void requestReadAndSendSmsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {
            // You may display a non-blocking explanation here, read more in the documentation:
            // https://developer.android.com/training/permissions/requesting.html
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, SMS_PERMISSION_CODE);
    }


    public void showRequestPermissionsInfoAlertDialog() {
        showRequestPermissionsInfoAlertDialog(true);
    }

    public void showRequestPermissionsInfoAlertDialog(final boolean makeSystemRequest) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("RECeive SMS"); // Your own title
        builder.setMessage("In order to work..."); // Your own message

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // Display system runtime permission request?
                if (makeSystemRequest) {
                    requestReadAndSendSmsPermission();
                }
            }
        });

        builder.setCancelable(false);
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SMS_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // SMS related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


}
