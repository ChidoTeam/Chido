package chido.com.chido_chido;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

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
    private String ussdString, amount, phoneNumber;
    ProgressDialog progressDialog;

    public WebService(Intent callIntent, AppCompatActivity activity, String phoneNumber, String amount) {
        super();
        this.callIntent = callIntent;
        this.activity = activity;
        this.amount = amount;
        this.phoneNumber = phoneNumber;

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
            urlConnection.setRequestProperty("Authorization", "someAuthString");
            if (postData != null) {
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(postData.toString());
                writer.flush();
            }

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                server_response = readStream(urlConnection.getInputStream());
                JSONObject jsonResponse = new JSONObject(server_response);
                JSONObject payload = jsonResponse.getJSONObject("payload");
                // ussdString = payload.getString("ussd_string")+ amount + "#";
                ussdString = payload.getString("ussd_string");

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressDialog.dismiss();
        // callIntent = new Intent(Intent.ACTION_CALL, ussdToCallableUri("*100*7*4*0758054848*1000#"));
        callIntent = new Intent(Intent.ACTION_CALL, ussdToCallableUri(ussdString));
        activity.startActivity(callIntent);


    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(activity,
                "Progress",
                "Loading...");
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

}

// Converting InputStream to String
