package chido.com.chido_chido;



import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
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

public class USSDTimer extends AsyncTask<String, Void, String> {
    private AppCompatActivity activity;

    public USSDTimer( AppCompatActivity activity) {
        super();
        this.activity = activity;

    }

    @Override
    protected String doInBackground(String... strings) {
   /*     Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // yourMethod();
            }
        }, 1000*60); // one minute*/
        try {
            Thread.sleep(1000*60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        ChidoUtil.savePrefBoolean(ChidoUtil.USSD_IN_SESSION,false,activity);
        activity.stopService(new Intent(activity, ProgressService.class));

    }

    @Override
    protected void onPreExecute() {
     ChidoUtil.savePrefBoolean(ChidoUtil.USSD_IN_SESSION,true,activity);
    }

}

// Converting InputStream to String
