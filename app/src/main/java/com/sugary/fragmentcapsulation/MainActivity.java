package com.sugary.fragmentcapsulation;

import android.os.Bundle;

import com.sugary.fragmentcapsulation.base.BasicActivity;

public class MainActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_main;
    }
}
