package com.example.aniru.federalmoneyserver;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.net.URL;

import com.example.aniru.Service.IMyAidlInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyService extends Service {



    public final String TAG = "Service";
    int flag = 0;
    int arr[] = new int[12];



    // Implementing stub
    private final IMyAidlInterface.Stub mBinder = new IMyAidlInterface.Stub()
    {



        //Implement method in Interface
        public int[] monthlyAvgCash(int year)
        {
            Log.i(TAG,"MonthlyAvg");

           // String QUERY = "SELECT avg(open_day)"
            int[] result = new int[12];

            String q = "SELECT avg(open_today) from t1 WHERE year="+year+" group by month"; // query grouping by months and getting average
           String URL = "http://api.treasury.io/cc7znvq/47d80ae900e04f2/sql/?q="+q;

            StringBuilder sb = new StringBuilder();
            HttpURLConnection httpUrlConnection = null;
            try {
                // establishing http connection
                httpUrlConnection = (HttpURLConnection) new URL(URL)
                        .openConnection();

                InputStream stream = httpUrlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line = ""; // reading line by line
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                ;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONArray data = null;


            try {
                data = new JSONArray(sb.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < 12; i++) {
                JSONObject Json = null;
                try {
                    Json = data.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    result[i] = Json.getInt("avg(open_today)");
                } catch (JSONException e) {
                    e.printStackTrace(); // performed in seperate try catch blocks for easier tracking of errors if any(Errors if together were harder for me to track)
                }
            }
            return result;
        }

        public int[] dailyCash(int year, int month, int day, int num)
        {
            int[] result = new int[num];

            StringBuilder sb = new StringBuilder();

            if (day + num <= 30) {

                String query = "SELECT \"open_today\" from t1 WHERE year='"+year+"' AND month ='"+month+"' AND day>='"+day+"' AND day<='"+(day+num)+"'"; // checking fpr days in the range
                //String query = "SELECT * FROM t1";
                String URL = "http://api.treasury.io/cc7znvq/47d80ae900e04f2/sql/?q=" + query;


                HttpURLConnection httpUrlConnection = null;
                try {

                    httpUrlConnection = (HttpURLConnection) new URL(URL)
                            .openConnection();

                    InputStream stream = httpUrlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    ;

                    Log.i(TAG, sb + "");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, sb.toString());
                JSONArray data = null;
                try {
                    data = new JSONArray(sb.toString()); // converting the String(in Json format) to actual JSON
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < num; i++) {
                    JSONObject Json = null;
                    try {
                        Json = data.getJSONObject(i); // parsing over JSON objects
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        result[i] = Json.getInt("open_today");// getting the "open_today" fields
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return result;
            }

            return result;
        }

    };

        // Return the Stub defined above
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    } // return IBinder object
}
