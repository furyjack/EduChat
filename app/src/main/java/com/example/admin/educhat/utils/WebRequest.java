package com.example.admin.educhat.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import android.util.Log;

import com.example.admin.educhat.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;


public class WebRequest extends AsyncTask<String, String, String> {
    Context context;
    Handler handler;
    int status;
    String finalURL, method = "";
    String token="";

    public WebRequest(Context context, Handler handler, String finalURL, String method) {
        this.context = context;
        this.handler = handler;
        this.finalURL = finalURL;
        this.method = method;
        new PreferenceManager(context);
        token = PreferenceManager.getPrefString(Constants.TOKEN);
    }

    @Override
    protected String doInBackground(String... params) {
        String jsonResponse;
        String jsonData = params[0];
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(finalURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(method);
            if (token != null) {
                Log.d("Token", token);
                urlConnection.setRequestProperty("ENSESSID", token);
            }
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");


            if (method.equals("POST") || method.equals("PUT")) {
                urlConnection.setDoOutput(true);
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
                writer.write(jsonData);
                writer.close();
            }
            if (method.equals("DELETE")) {
                urlConnection.setDoInput(true);
                urlConnection.setInstanceFollowRedirects(false);
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
                writer.write(jsonData);
                writer.close();
            }
            token = urlConnection.getHeaderField("ENSESSID");
            // Log.d("TOKEN", token);
            PreferenceManager.setPrefString(Constants.TOKEN, token);
            status = urlConnection.getResponseCode();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                buffer.append(inputLine + "\n");
                if (buffer.length() == 0) {
                    return null;
                }
                jsonResponse = buffer.toString();
                return jsonResponse;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Message msg = Message.obtain();
        msg.obj = result;
        msg.arg1 = status;
        Log.d("STATUS", ""+msg.arg1);
        msg.setTarget(handler);
        msg.sendToTarget();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
}
