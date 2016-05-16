/*
package com.permission.pleaserequest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

*/
/**
 * User :   Sneha Khadatare
 * Date :   4/29/2016
 * Time :   4:08 PM IST
 *
 * I am calling it fake because it is just to get the callback from new permission framework, not used as Activity.
 *//*

public class PermissionBackgroundActivity extends AppCompatActivity {

    @SuppressWarnings("unused")
    private static final String TAG = FakeActivity.class.getSimpleName();


    private static final String NEEDED_PERMISSIONS = "needed_permissions";
    private static final String SHOW_EXPLANATION_FOR = "explanation_for";
    private static final String EXPLANATION_MESSAGES_TO_SHOW = "explanation_message";

    private static final int REQUEST_CODE_FOR_PERMISSION = 1;

    private ArrayList<RuntimePermission> mRuntimePermissions ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //No need to touch my Fake Activity
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        if (savedInstanceState != null) {
            mRuntimePermissions = savedInstanceState.getParcelableArrayList(Constants.PERMISSIONS);
        }

        getNeededPermissions();
    }


    //To handle rotation
    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putParcelableArrayList(Constants.PERMISSIONS, mRuntimePermissions);
    }


    //Get Needed permission passed by the user of library
    private void getNeededPermissions() {

        Map<String, List<String>> map = extractAllPermissionsFromReceivedData(mRuntimePermissions);

        List<String> neededPermissions = map.get(NEEDED_PERMISSIONS);
        final List<String> showRationaleFor = map.get(SHOW_EXPLANATION_FOR);
        List<String> rationalMessagesToShow = map.get(EXPLANATION_MESSAGES_TO_SHOW);

        if (showRationaleFor.size() > 0 && rationalMessagesToShow != null && rationalMessagesToShow.size() > 0) {


            showMessageOKCancel(buildExplanationMessageToShow(rationalMessagesToShow), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(PermissionBackgroundActivity.this, showRationaleFor.toArray(new String[showRationaleFor.size()]), REQUEST_CODE_FOR_PERMISSION);
                    dialog.dismiss();
                }
            });


        } else if (neededPermissions.size() > 0) {
            ActivityCompat.requestPermissions(this, neededPermissions.toArray(new String[showRationaleFor.size()]), REQUEST_CODE_FOR_PERMISSION);
        } else {
            //All permissions are already granted so finish this with calling listener with grant message
            int[] result = new int[mPermissions.length];
            Arrays.fill(result, PackageManager.PERMISSION_GRANTED);
            broadcastTheResults(mPermissions, result);
            finish();
            //No Animation for finish, it looks bad with fake things
            this.overridePendingTransition(0,0);

        }


    }


    //This method separate out all needed permissions and their messages in separate maps
    private void extractAllPermissionsFromReceivedData(ArrayList<RuntimePermission> requestedPermissions) {

        ActivityCompat.requestPermissions(this, requestedPermissions.toArray(new String[requestedPermissions.size()]), REQUEST_CODE_FOR_PERMISSION);
    }


    // To show multiple messages in the dialog box
    // You can choose symbol from here :)
    //  http://fsymbols.com/signs/stars/
    @NonNull
    private String buildExplanationMessageToShow(@NonNull List<String> messages) {
        StringBuilder sb = new StringBuilder();
        for (String msg : messages) {
            sb.append("✯").append("\u0009").append(msg).append("\n");
        }
        return sb.toString();
    }


    //Method will be called when user takes an action on Permission dialog
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_FOR_PERMISSION: {
                broadcastTheResults(permissions, grantResults);
                finish();
                //No Animation for finish, it looks bad with fake things
                this.overridePendingTransition(0,0);
            }
        }
    }

    //Broadcast the results back to the PleaseRequest class so that it can pass the results to calling Activity.
    //Broadcast is registered using Manifest
    private void broadcastTheResults(String[] permissions, int[] grantResults) {
        if (grantResults.length > 0) {
            Intent intent = new Intent();
            intent.setAction(PleaseRequest.PERMISSION_BROADCAST_INTENT);
            intent.putExtra(Constants.PERMISSIONS, permissions);
            intent.putExtra(Constants.GRANT_RESULTS, grantResults);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }



    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .setTitle("Permissions Required")
                .create()
                .show();
    }

}
*/
