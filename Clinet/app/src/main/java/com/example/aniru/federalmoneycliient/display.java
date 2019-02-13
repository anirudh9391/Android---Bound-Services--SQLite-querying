package com.example.aniru.federalmoneycliient;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class display extends Activity
{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disp);

        ListView lv1 = (ListView) findViewById(R.id.list1);

        ArrayList<String> arr1 = new ArrayList<>();

        arr1= getIntent().getStringArrayListExtra("arrList");
        Log.i("AAAAAAAAA",arr1+"");

        ArrayAdapter<String> itemsAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arr1);
        lv1.setAdapter(itemsAdapter1);
    }
}
