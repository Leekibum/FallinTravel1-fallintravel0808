package com.probum.fallintravel;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class AdditionActivity extends AppCompatActivity {

    ArrayList<Item> items=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addition);

        findIds();
    }

    void findIds(){




    }
}
