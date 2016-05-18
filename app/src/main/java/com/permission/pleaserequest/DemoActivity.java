package com.permission.pleaserequest;

import android.Manifest;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.instant.runtimepermission.MPermission;
import com.instant.runtimepermission.PermissionRequest;

import java.util.List;

public class DemoActivity extends AppCompatActivity {

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



        /*
        Raise the request as below
         */
        PermissionRequest.inside(this)
                .withRequestId("LocationAndStorage")
                .forPermissions(new MPermission(Manifest.permission.ACCESS_COARSE_LOCATION , "Please give me the location permission."),
                        new MPermission( Manifest.permission.WRITE_EXTERNAL_STORAGE ,"Please give me the write permission."))
                .request(new PermissionRequest.GrantPermissionListener() {
                    @Override
                    public void grantedPermission(List<String> permissions) {

                        if(permissions!=null && permissions.size() > 0){
                            for (int i = 0; i < permissions.size(); i++) {
                                Toast.makeText(DemoActivity.this, "Granted "+permissions.get(i), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void rejected(List<String> permissions) {

                        if(permissions!=null && permissions.size() > 0){
                            for (int i = 0; i < permissions.size(); i++) {
                                Toast.makeText(DemoActivity.this, "Rejected "+permissions.get(i), Toast.LENGTH_SHORT).show();
                            }
                        }


                    }
                });

    }

}
