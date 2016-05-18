package com.permission.pleaserequest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

/**
 * User :   Sneha Khadatare
 * Date :   4/29/2016
 * Time :   4:08 PM IST
 * <p/>
 * I am calling it fake because it is just to get the callback from new permission framework, not used as Activity.
 */
public class FakeActivity extends AppCompatActivity {

    @SuppressWarnings("unused")
    private static final String TAG = FakeActivity.class.getSimpleName();
    private static final String PREF_NAME = "PREF_NAME";

    private static final int REQUEST_CODE_FOR_PERMISSION = 1;

    private ArrayList<RuntimePermission> mPermissions = new ArrayList<>();
    private static String mRequestId;


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
            mRequestId = state.getString(Constants.REQUEST_ID);
        } else {
            mPermissions = getIntent().getParcelableArrayListExtra(Constants.PERMISSIONS);
            mRequestId = getIntent().getStringExtra(Constants.REQUEST_ID);
        }

        getAskForPermissions();

    }

    //To handle rotation
    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putParcelableArrayList(Constants.PERMISSIONS, mPermissions);
    }


    //Get Needed permission passed by the user of library
    private void getAskForPermissions() {

        if (getNeedPermissions().size() > 0) {

            ActivityCompat.requestPermissions(this, extractPermissionFromModel(getNeedPermissions()), REQUEST_CODE_FOR_PERMISSION);

        } else {
            //All permissions are already granted so finish this with calling listener with grant message
            int[] result = new int[mPermissions.size()];
            Arrays.fill(result, PackageManager.PERMISSION_GRANTED);
            broadcastTheResults(extractPermissionFromModel(mPermissions), result);
            finish();
            //No Animation for finish, it looks bad with fake things
            this.overridePendingTransition(0, 0);

        }

    }

    private ArrayList<RuntimePermission> getNeedPermissions(){
        ArrayList<RuntimePermission> neededPerList = new ArrayList<>();

        for (int i = 0; i < mPermissions.size(); i++) {

            if(ContextCompat.checkSelfPermission(FakeActivity.this, mPermissions.get(i).getPermissionName()) != PackageManager.PERMISSION_GRANTED){
                neededPerList.add( mPermissions.get(i));
            }

        }

        return neededPerList;
    }


    private String[] extractPermissionFromModel(@NonNull @Size(min = 1) ArrayList<RuntimePermission> perList) {

        String[] permissionStringArray = new String[perList.size()];

        for (int i = 0; i < perList.size(); i++) {
            permissionStringArray[i] = perList.get(i).getPermissionName();
        }

        return permissionStringArray;
    }


    // To show multiple messages in the dialog box
    // You can choose symbol from here :)
    //  http://fsymbols.com/signs/stars/
    @NonNull
    private String buildExplanationMessageToShow(@NonNull ArrayList<RuntimePermission> permissionsList) {

        StringBuilder sb = new StringBuilder();

        for (RuntimePermission permission : permissionsList) {
            if (!TextUtils.isEmpty(permission.getMessageOnDenial()))
                sb.append("✯").append("\u0009").append(permission.getMessageOnDenial()).append("\n");
        }
        return sb.toString();
    }


    //Method will be called when user takes an action on Permission dialog
    @Override
    public void onRequestPermissionsResult(int requestCode, final
    @NonNull String permissions[],
                                           final @NonNull int[] grantResults) {
        Log.d("!!!", "onRequestPermissionsResult: " + requestCode);

        switch (requestCode) {
            case REQUEST_CODE_FOR_PERMISSION: {


                final ArrayList<RuntimePermission> resultList = getDenialPermissionExtraMessages(permissions, grantResults);

                String denialRationalMessages = buildExplanationMessageToShow(resultList);

                if (!TextUtils.isEmpty(denialRationalMessages) && !isPermissionCallbackReceive(FakeActivity.this)) {

                    setPermissionCallbackReceiveFlag(FakeActivity.this, true);

                    showMessageOKCancel(denialRationalMessages, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    ActivityCompat.requestPermissions(FakeActivity.this, extractPermissionFromModel(resultList), REQUEST_CODE_FOR_PERMISSION);
                                    dialog.dismiss();
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    broadcastTheResults(permissions, grantResults);
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                } else {
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
            intent.setAction(PermissionRequest.PERMISSION_BROADCAST_INTENT);
            intent.putExtra(Constants.PERMISSIONS, permissions);
            intent.putExtra(Constants.GRANT_RESULTS, grantResults);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {

        Log.d(TAG, "showMessageOKCancel: " + this);

        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", cancelListener)
                .setTitle("Permissions Required")
                .create()
                .show();
    }

    private ArrayList<RuntimePermission> getDenialPermissionExtraMessages(String permissions[], int[] grantResults) {
        ArrayList<RuntimePermission> messageList = new ArrayList<>();

        for (int i = 0; i < mPermissions.size(); i++) {
            for (int j = 0; j < permissions.length; j++) {
                if (mPermissions.get(i).getPermissionName().equalsIgnoreCase(permissions[j]) && grantResults[j] != PackageManager.PERMISSION_GRANTED) {
                    messageList.add(mPermissions.get(i));
                }
            }

        }

        return messageList;


    }

    private static SharedPreferences getPreferences(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                PREF_NAME, Context.MODE_PRIVATE);
        return prefs;
    }


    public static void setPermissionCallbackReceiveFlag(Context context, boolean isCallbackReceived) {
        getPreferences(context).edit().putBoolean(mRequestId, isCallbackReceived).commit();
    }

    public static boolean isPermissionCallbackReceive(Context context) {
        return getPreferences(context).getBoolean(mRequestId, false);
    }


}
