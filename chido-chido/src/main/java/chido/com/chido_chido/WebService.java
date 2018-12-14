package chido.com.chido_chido;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WebService extends AsyncTask<String, Void, String> {
    String server_response;
    private Intent callIntent;
    private AppCompatActivity activity;
    private ServerResponseObject serverResponseObject;
    private String amount, phoneNumber;
    ProgressDialog progressDialog;
    private USSDTimer ussdTimer;
    private TelephonyInfo telephonyInfo;

    public WebService(Intent callIntent,USSDTimer ussdTimer, AppCompatActivity activity, String phoneNumber, String amount,TelephonyInfo telephonyInfo) {
        super();
        this.ussdTimer = ussdTimer;
        this.callIntent = callIntent;
        this.activity = activity;
        this.amount = amount;
        this.phoneNumber = phoneNumber;
        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("Progress");
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        serverResponseObject = new ServerResponseObject();
        telephonyInfo = telephonyInfo;

    }

    @Override
    protected String doInBackground(String... strings) {

        URL url;
        HttpURLConnection urlConnection = null;

        try {
            JSONObject postData = new JSONObject();
            postData.put("amount", Integer.parseInt(amount));
            postData.put("phone_number", phoneNumber);

            url = new URL(strings[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("authorization", "Basic MjU2NzE1NzI2Mjg3OmphbTE5ODk=");
            urlConnection.setRequestProperty("Connection", "close");
            if (postData != null) {
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(postData.toString());
                writer.flush();
            }

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                server_response = readStream(urlConnection.getInputStream());
                JSONObject jsonResponse = new JSONObject(server_response);
                serverResponseObject.setSuccess(jsonResponse.getBoolean("success"));
                if(serverResponseObject.isSuccess()){
                    JSONObject payload = jsonResponse.getJSONObject("payload");
                    serverResponseObject.setUssdString(payload.getString("ussd_string"));
                    ChidoUtil.savePref(ChidoUtil.USSD_PHONE_NUMBER,payload.getString("phone_number").replaceFirst("256","0"),activity);
                }else{
                    serverResponseObject.setMessage(jsonResponse.getString("message"));
                }


            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
            alertDialog.setTitle("");
            alertDialog.setMessage("check your internet");
            alertDialog.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        ChidoUtil.dismissDialog( progressDialog, activity);

        /**
         * uncomment and edit to test
         */
//        callIntent = new Intent(Intent.ACTION_CALL, ussdToCallableUri("*160*6*1#"));
//   callIntent = new Intent(Intent.ACTION_CALL, ussdToCallableUri("*100*7*5*0758054848*100#"));
//        activity.startActivity(callIntent);

        // if ussd is in session cancel  time
        if(ChidoUtil.getPrefBoolean(ChidoUtil.USSD_IN_SESSION,activity)) {
          ussdTimer.cancel(true);
        }
         ussdTimer.cancel(true);
        ussdTimer = new USSDTimer(activity);
         ussdTimer.execute();
  ;


        if (serverResponseObject.isSuccess() ) {
            Log.e("WEBSERVICE", "web " + serverResponseObject.getUssdString() );

            callIntent = new Intent(Intent.ACTION_CALL, ussdToCallableUri(serverResponseObject.getUssdString()));
            activity.startActivity(callIntent);
        }else{
            final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage(serverResponseObject.getMessage());
            alertDialog.show();
        }


    }

    @Override
    protected void onPreExecute() {
       progressDialog.show();
    }


    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
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

    private final static String simSlotName[] = {
            "extra_asus_dial_use_dualsim",
            "com.android.phone.extra.slot",
            "slot",
            "simslot",
            "sim_slot",
            "subscription",
            "Subscription",
            "phone",
            "com.android.phone.DialingMode",
            "simSlot",
            "slot_id",
            "simId",
            "simnum",
            "phone_type",
            "slotId",
            "slotIdx"
    };

private void makeCall(){
    callIntent = new Intent(Intent.ACTION_CALL, ussdToCallableUri(serverResponseObject.getUssdString()));
    activity.startActivity(callIntent);
    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    callIntent.putExtra("com.android.phone.force.slot", true);
    callIntent.putExtra("Cdma_Supp", true);

    //Add all slots here, according to device.. (different device require different key so put all together)
    int slot = getSlotToUse();
    if(slot!= 99) {
        for (String s : simSlotName)
            callIntent.putExtra(s, slot); //0 or 1 according to sim.......
    }
    //works only for API >= 21
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//        callIntent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", (Parcelable) " here You have to get phone account handle list by using telecom manger for both sims:- using this method getCallCapablePhoneAccounts()");

    activity.startActivity(callIntent);
}

private int getSlotToUse(){
    if(!telephonyInfo.isDualSIM()){
        return 0;
    }else{
        String Network = ChidoUtil.getNetworkFromPhoneNumber(phoneNumber);
        String sim1Network = ChidoUtil.getNetworkFromOperator(telephonyInfo.getOperatorSIM1());
        String sim2Network = ChidoUtil.getNetworkFromOperator(telephonyInfo.getOperatorSIM2());
        if(Network.equalsIgnoreCase(sim1Network)){
            return 0;
        }else if (Network.equalsIgnoreCase(sim2Network)){
            return 1;
        }else{
            return 99;
        }
    }

}
}



// Converting InputStream to String
