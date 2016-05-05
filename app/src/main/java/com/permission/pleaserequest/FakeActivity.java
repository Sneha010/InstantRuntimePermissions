package com.permission.pleaserequest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

/**
 * User :   Sneha Khadatare
 * Date :   4/29/2016
 * Time :   4:08 PM IST
 *
 * I am calling it fake because it is just to get the callback from new permission framework, not used as Activity.
 */
public class FakeActivity extends AppCompatActivity {

    @SuppressWarnings("unused")
    private static final String TAG = FakeActivity.class.getSimpleName();


    private static final String NEEDED_PERMISSIONS = "needed_permissions";
    private static final String SHOW_EXPLANATION_FOR = "explanation_for";
    private static final String EXPLANATION_MESSAGES_TO_SHOW = "explanation_message";

    private static final int REQUEST_CODE_FOR_PERMISSION = 1;


    private String[] mPermissions;
    private String[] mExtraExplanationMessages;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //No need to touch my Fake Activity
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        initializeWithRotationHandling(savedInstanceState);
    }

    private void initializeWithRotationHandling(Bundle state) {

        if (state != null) {
            mPermissions = state.getStringArray(Constants.PERMISSIONS);
            mExtraExplanationMessages = state.getStringArray(Constants.EXTRA_MESSAGES);
        } else {
            Intent intent = getIntent();
            mPermissions = intent.getStringArrayExtra(Constants.PERMISSIONS);
            mExtraExplanationMessages = intent.getStringArrayExtra(Constants.EXTRA_MESSAGES);
        }

        getNeededPermissions();

    }

    //To handle rotation
    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putStringArray(Constants.PERMISSIONS, mPermissions);
        state.putStringArray(Constants.EXTRA_MESSAGES, mExtraExplanationMessages);
    }


    //Get Needed permission passed by the user of library
    private void getNeededPermissions() {

        Map<String, List<String>> map = extractAllPermissionsFromReceivedData(mPermissions, mExtraExplanationMessages);
        List<String> neededPermissions = map.get(NEEDED_PERMISSIONS);
        final List<String> showRationaleFor = map.get(SHOW_EXPLANATION_FOR);
        List<String> rationalMessagesToShow = map.get(EXPLANATION_MESSAGES_TO_SHOW);

        if (showRationaleFor.size() > 0 && rationalMessagesToShow != null && rationalMessagesToShow.size() > 0) {


            showMessageOKCancel(buildExplanationMessageToShow(rationalMessagesToShow), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(FakeActivity.this, showRationaleFor.toArray(new String[showRationaleFor.size()]), REQUEST_CODE_FOR_PERMISSION);
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
    private Map<String, List<String>> extractAllPermissionsFromReceivedData(String[] permissions,
                                                                            String[] extraExplanationMessages) {
        Map<String, List<String>> map = new HashMap<>();
        List<String> neededPermissionsList = new ArrayList<>();
        List<String> showExplanationForPermissionList = new ArrayList<>();
        List<String> neededExplanationMessagesList = new ArrayList<>();

        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                neededPermissionsList.add(permission);
            }


            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                showExplanationForPermissionList.add(permission);
                // if multiple explanation messages corresponding to each permission
                if (extraExplanationMessages != null &&
                        extraExplanationMessages.length == permissions.length) {
                    neededExplanationMessagesList.add(extraExplanationMessages[i]);
                }
            }



        }

        map.put(NEEDED_PERMISSIONS, neededPermissionsList);
        map.put(SHOW_EXPLANATION_FOR, showExplanationForPermissionList);
        map.put(EXPLANATION_MESSAGES_TO_SHOW, neededExplanationMessagesList);

        return map;
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
