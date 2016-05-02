package com.permission.pleaserequest;

import android.Manifest;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        PleaseRequest.inside(this)
                .forPermissions(Manifest.permission.ACCESS_COARSE_LOCATION)
                .withExtraExplanation("Please give me the following permission.")
                .request(new PleaseRequest.GrantPermissionListener() {
            @Override
            public void grantedPermission(List<String> permissions) {

                Toast.makeText(TestActivity.this, "Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void rejected(List<String> permissions) {
                Toast.makeText(TestActivity.this, "Rejected", Toast.LENGTH_SHORT).show();

            }
        });


    }

}
