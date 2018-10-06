package chido.com.chido_chido;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoadAirtimeActivity extends AppCompatActivity {
    private Intent callIntent;
    private TelephonyManager mTelephonyManager;
    private Button btnProceed;
    private EditText amount, phoneNumber;
    private final String URL = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_airtime);
        amount = findViewById(R.id.amount);
        phoneNumber = findViewById(R.id.phone);
        btnProceed = findViewById(R.id.btnProceed);
        mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);


    }

    private Uri ussdToCallableUri(String ussd) {

        String uriString = "";

        if (!ussd.startsWith("tel:"))
            uriString += "tel:";

        for (char c : ussd.toCharArray()) {

            if (c == '#')
                uriString += Uri.encode("#");
            else
                uriString += c;
        }

        return Uri.parse(uriString);
    }


    private void setOnClickListeners() {
        //do something after button is pressed
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                     if (amount.getText().toString().isEmpty()) {
                         final AlertDialog alertDialog = new AlertDialog.Builder(LoadAirtimeActivity.this).create();
                         alertDialog.setTitle("");
                         alertDialog.setMessage(getString(R.string.enter_amount));
                         alertDialog.show();
                     }else if (phoneNumber.getText().toString().isEmpty()) {
                         final AlertDialog alertDialog = new AlertDialog.Builder(LoadAirtimeActivity.this).create();
                         alertDialog.setTitle("");
                         alertDialog.setMessage(getString(R.string.enter_phone_number));
                         alertDialog.show();
                    } else {
                         if (ContextCompat.checkSelfPermission( LoadAirtimeActivity.this, "android.permission.CALL_PHONE") != 0) {
                             if (!ActivityCompat.shouldShowRequestPermissionRationale( LoadAirtimeActivity.this, "android.permission.CALL_PHONE")) {
                                 ActivityCompat.requestPermissions( LoadAirtimeActivity.this, new String[]{"android.permission.CALL_PHONE"}, 1);
                             }
                         } else {
                              new WebService(callIntent,LoadAirtimeActivity.this,phoneNumber.getText().toString(),amount.getText().toString()).execute(URL);

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

}
