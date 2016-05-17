package com.permission.pleaserequest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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


    private ArrayList<RuntimePermission> mPermissions = new ArrayList<>();
    private boolean mPermissionCallbackReceived = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //No need to touch my Fake Activity
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        initializeWithRotationHandling(savedInstanceState);
    }

    private void initializeWithRotationHandling(Bundle state) {

        if (state != null) {
            mPermissions = state.getParcelableArrayList(Constants.PERMISSIONS);
        } else {
            mPermissions = getIntent().getParcelableArrayListExtra(Constants.PERMISSIONS);
        }

        getNeededPermissions();

    }

    //To handle rotation
    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putParcelableArrayList(Constants.PERMISSIONS, mPermissions);
    }


    //Get Needed permission passed by the user of library
    private void getNeededPermissions() {

        Map<String, ArrayList<RuntimePermission>> map = extractAllPermissionsFromReceivedData(mPermissions);

        ArrayList<RuntimePermission> neededPermissions =  map.get(NEEDED_PERMISSIONS);
        final ArrayList<RuntimePermission> showRationaleFor = map.get(SHOW_EXPLANATION_FOR);

        if (showRationaleFor.size() > 0) {

            showMessageOKCancel(buildExplanationMessageToShow(showRationaleFor), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(FakeActivity.this, extractPermissionFromModel(showRationaleFor), REQUEST_CODE_FOR_PERMISSION);
                            dialog.dismiss();
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });


        } else if (neededPermissions.size() > 0) {

            ActivityCompat.requestPermissions(this, extractPermissionFromModel(neededPermissions), REQUEST_CODE_FOR_PERMISSION);
        }
        else {
            //All permissions are already granted so finish this with calling listener with grant message
            int[] result = new int[mPermissions.size()];
            Arrays.fill(result, PackageManager.PERMISSION_GRANTED);
            broadcastTheResults(extractPermissionFromModel(mPermissions), result);
            finish();
            //No Animation for finish, it looks bad with fake things
            this.overridePendingTransition(0,0);

        }


    }


    private String[] extractPermissionFromModel(@NonNull @Size(min=1) ArrayList<RuntimePermission> perList){

        String[] permissionStringArray = new String[perList.size()];

        for (int i = 0; i < perList.size(); i++) {
            permissionStringArray[i] = perList.get(i).getPermissionName();
        }

        return permissionStringArray;
    }


    //This method separate out all needed permissions and their messages in separate maps
    private Map<String, ArrayList<RuntimePermission>> extractAllPermissionsFromReceivedData(ArrayList<RuntimePermission> permissions) {

        Map<String, ArrayList<RuntimePermission>> map = new HashMap<>();

        ArrayList<RuntimePermission> neededPermissionsList = new ArrayList<>();
        ArrayList<RuntimePermission> requestDenialMsgPermissionList = new ArrayList<>();

        for (int i = 0; i < permissions.size(); i++) {

            String permission = permissions.get(i).getPermissionName();

            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                neededPermissionsList.add(permissions.get(i));
            }

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                requestDenialMsgPermissionList.add(permissions.get(i));
            }
        }

        map.put(NEEDED_PERMISSIONS, neededPermissionsList);
        map.put(SHOW_EXPLANATION_FOR, requestDenialMsgPermissionList);

        return map;
    }


    // To show multiple messages in the dialog box
    // You can choose symbol from here :)
    //  http://fsymbols.com/signs/stars/
    @NonNull
    private String buildExplanationMessageToShow(@NonNull ArrayList<RuntimePermission> permissionsList) {

        StringBuilder sb = new StringBuilder();

        for (RuntimePermission permission : permissionsList) {
            if(!TextUtils.isEmpty(permission.getMessageOnDenial()))
                sb.append("✯").append("\u0009").append(permission.getMessageOnDenial()).append("\n");
        }
        return sb.toString();
    }


    //Method will be called when user takes an action on Permission dialog
    @Override
    public void onRequestPermissionsResult(int requestCode,final
                                           @NonNull String permissions[],
                                           final @NonNull int[] grantResults) {
        Log.d("!!!", "onRequestPermissionsResult: "+requestCode);

        switch (requestCode) {
            case REQUEST_CODE_FOR_PERMISSION: {


                final ArrayList<RuntimePermission> resultList = getDenialPermissionExtraMessages(permissions ,grantResults);

                String denialRationalMessages = buildExplanationMessageToShow(resultList);

                if(!TextUtils.isEmpty(denialRationalMessages) && !mPermissionCallbackReceived){
                    showMessageOKCancel(denialRationalMessages, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mPermissionCallbackReceived = true;
                                    ActivityCompat.requestPermissions(FakeActivity.this, extractPermissionFromModel(resultList), REQUEST_CODE_FOR_PERMISSION);
                                    dialog.dismiss();
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                }else{
                    broadcastTheResults(permissions, grantResults);
                    finish();
                }



                return;
            }

            default:
                return;
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



    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener , DialogInterface.OnClickListener cancelListener) {

        Log.d(TAG, "showMessageOKCancel: "+this);

        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", cancelListener)
                .setTitle("Permissions Required")
                .create()
                .show();
    }

    private ArrayList<RuntimePermission> getDenialPermissionExtraMessages(String permissions[], int[] grantResults){
        ArrayList<RuntimePermission> messageList = new ArrayList<>();

        for (int i = 0; i < mPermissions.size(); i++) {
            for (int j = 0; j < permissions.length ; j++) {
                if(mPermissions.get(i).getPermissionName().equalsIgnoreCase(permissions[j]) && grantResults[j] != PackageManager.PERMISSION_GRANTED){
                    messageList.add(mPermissions.get(i));
                }
            }

        }

        return messageList;


    }

}
