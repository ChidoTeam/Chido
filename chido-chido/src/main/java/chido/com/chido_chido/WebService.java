package chido.com.chido_chido;

import android.app.AlertDialog;
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
    private ServerResponseObject serverResponseObject;
    private String amount, phoneNumber;
    ProgressDialog progressDialog;

    public WebService(Intent callIntent, AppCompatActivity activity, String phoneNumber, String amount) {
        super();
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
        progressDialog.dismiss();
        // callIntent = new Intent(Intent.ACTION_CALL, ussdToCallableUri("*100*7*4*0758054848*1000#"));
        if (serverResponseObject.isSuccess() ) {
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

}

// Converting InputStream to String
