package com.permission.pleaserequest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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


    private static final String NEEDED_PERMISSIONS = "needed_permissions";
    private static final String SHOW_EXPLANATION_FOR = "explanation_for";
    private static final String EXPLANATION_MESSAGES_TO_SHOW = "explanation_message";


    private static final int PERMISSION_REQUEST_CODE = 100;

    private static final String PERMISSION_INTENT = "com.permission.pleaserequest.PERMISSION_RESULT_INTENT";

    @SuppressWarnings("unused")
    private static final String TAG = FakeActivity.class.getSimpleName();
    private String[] mPermissions;
    private String[] mExtraExplanationMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    //to handle rotation
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
                    ActivityCompat.requestPermissions(FakeActivity.this, showRationaleFor.toArray(new String[showRationaleFor.size()]), PERMISSION_REQUEST_CODE);
                    dialog.dismiss();
                }
            });


        } else if (neededPermissions.size() > 0) {
            ActivityCompat.requestPermissions(this, neededPermissions.toArray(new String[showRationaleFor.size()]), PERMISSION_REQUEST_CODE);
        } else {
            int[] result = new int[mPermissions.length];
            Arrays.fill(result, PackageManager.PERMISSION_GRANTED);
            broadcastTheResults(mPermissions, result);
            finish();
        }


    }


    //This method separate out all permissions in separate maps
    private Map<String, List<String>> extractAllPermissionsFromReceivedData(String[] permissions,
                                                                            String[] extraExplanationMessagesMessages) {
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
                // if multiple rational message corresponding to each permission
                if (extraExplanationMessagesMessages != null && extraExplanationMessagesMessages.length == permissions.length) {
                    neededExplanationMessagesList.add(extraExplanationMessagesMessages[i]);
                }
            }
        }

        map.put(NEEDED_PERMISSIONS, neededPermissionsList);
        map.put(SHOW_EXPLANATION_FOR, showExplanationForPermissionList);
        map.put(EXPLANATION_MESSAGES_TO_SHOW, neededExplanationMessagesList);
        return map;
    }


    // To show multiple messages in the dialog box
    @NonNull
    private String buildExplanationMessageToShow(@NonNull List<String> messages) {
        StringBuilder sb = new StringBuilder();
        for (String msg : messages) {
            sb.append("\u2022").append("\u0009").append(msg).append("\n");
        }
        return sb.toString();
    }


    //Method will be called when user takes an action on Permission dialog
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                broadcastTheResults(permissions, grantResults);
                finish();
            }
        }
    }

    //Broadcast the results back to the PleaseRequest class so that it can pass the results to calling Activity.
    //Broadcast is registered using Manifest
    private void broadcastTheResults(String[] permissions, int[] grantResults) {
        if (grantResults.length > 0) {
            Intent intent = new Intent();
            intent.setAction(PERMISSION_INTENT);
            intent.putExtra(Constants.PERMISSIONS, permissions);
            intent.putExtra(Constants.GRANT_RESULTS, grantResults);
            sendBroadcast(intent);
        }
    }



    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .setTitle("Permissions Needed")
                .create()
                .show();
    }

}
