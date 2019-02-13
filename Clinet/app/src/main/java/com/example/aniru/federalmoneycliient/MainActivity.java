package com.example.aniru.federalmoneycliient;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.aniru.Service.IMyAidlInterface;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<String> a= new ArrayList<>();
    private EditText year;
    private EditText month;
    private EditText day;
    private EditText num;
    MyThread mThread;
    Handler UIHandler = new Handler(Looper.getMainLooper()); // Getting a reference to loop handler
    private IMyAidlInterface mIMyAidlInterface; // reference to AIDL

    private Button button1 = null;
    private Button button2 = null;


    private boolean mIsBound;
    protected static final String TAG = "ServiceUser";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        button1 = (Button)findViewById(R.id.button1);
        button1.setOnClickListener(this);
        button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(this);


        year = (EditText) findViewById(R.id.enter_year);
        month = (EditText) findViewById(R.id.enter_month);
        day = (EditText) findViewById(R.id.enter_day);
        num = (EditText) findViewById(R.id.enter_number);

        mThread = new MyThread();
        mThread.start(); // starting off worker thread and hence its handler

        // Performing the binding

        Intent i = new Intent(IMyAidlInterface.class.getName());
        ResolveInfo info = getPackageManager().resolveService(i, Context.BIND_AUTO_CREATE);
        i.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));
        bindService(i, myConnection, Context.BIND_AUTO_CREATE);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button1: // MonthlyAvg
                Log.i(TAG, "MonthAvg");
                final int year_avg = Integer.parseInt(year.getText().toString()) ;
                mThread.mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        final StringBuilder result_display = new StringBuilder();

                        int[] result = new int[12];
                        try {
                            result = mIMyAidlInterface.monthlyAvgCash(year_avg); // Passing the years as input
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }


                        for (int i=0; i<result.length; i++){
                            result_display.append(" "+ result[i]); // fetching the results of length 12
                        }
                        UIHandler.post(new Runnable() { // Handler for UI thread; post method
                            @Override
                            public void run()
                            {
                                a.add(result_display+""); // Add to list to display in seoond activity

                                Intent i = new Intent(MainActivity.this, display.class);
                                Bundle b = new Bundle();
                                b.putStringArrayList("arrList",a);
                                i.putExtras(b);

                                startActivity(i); // starting off second activity

                            }
                        });
                    }
                });
                break;
            case R.id.button2: // DailyCash
                Log.i(TAG, "Daily Cash");

                final StringBuilder result_display = new StringBuilder();
                final int year_cash = Integer.parseInt(year.getText().toString());
                final int month_cash = Integer.parseInt(month.getText().toString());
                final int day_cash = Integer.parseInt(day.getText().toString());
                final int num_cash = Integer.parseInt(num.getText().toString());
                mThread.mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Handle call
                        int[] result = new int[num_cash + 1]; // Array is of sixe num
                        try {
                            result = mIMyAidlInterface.dailyCash(year_cash, month_cash, day_cash,num_cash); // Passing all inputs
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }



                        for (int i=0; i<result.length; i++){
                            result_display.append(" "+ result[i]);
                        }
                        UIHandler.post(new Runnable() {
                            @Override
                            public void run()
                            {
                                a.add(result_display+""); // Appending to list

                                Intent i = new Intent(MainActivity.this, display.class);
                                Bundle b = new Bundle();
                                b.putStringArrayList("arrList",a);
                                i.putExtras(b);

                                startActivity(i);

                            }
                        });
                    }
                });
                break;

        }
    }

    private ServiceConnection myConnection = new ServiceConnection() { // Establishing connection
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIMyAidlInterface = IMyAidlInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            mIMyAidlInterface = null;

            mIsBound = false;

        }
    };

    public class MyThread extends Thread{ // A thread to communicate with Service
        Handler mHandler;

        @Override
        public void run() {
            Looper.prepare();
            mHandler = new Handler();
            Looper.loop();
        }
    }
}

