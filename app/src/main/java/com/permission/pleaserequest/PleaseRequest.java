package com.permission.pleaserequest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User :   Sneha Khadatare
 * Date :   4/29/2016
 * Time :   3:57 PM IST
 */
public class PleaseRequest {


    protected static final String PERMISSION_BROADCAST_INTENT = "com.permission.pleaserequest.PERMISSION_RESULT_INTENT";
    private static GrantPermissionListener mPermissionListener;

    private Context mContext;
    private ArrayList<RuntimePermission> mPermissions;


    private PermissionResultsBroadCastReceiver mPermissionResultsBroadCastReceiver;

    private PleaseRequest(Context context) {
        mContext = context;
    }

    public static PleaseRequest inside(Context context){

        return new PleaseRequest(context);

    }


    public PleaseRequest forPermissions(@NonNull @Size(min = 1) RuntimePermission... permissions) {

        if (permissions.length == 0) {
            throw new IllegalArgumentException("Please request for at least one permission.");
        }
        this.mPermissions = new ArrayList<>(Arrays.asList(permissions));

        return this;
    }


    public void request(GrantPermissionListener listener) {

        this.mPermissionListener = listener;

        //Only Marshmellow with Runtime permission else go with normal flow
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mPermissionListener.grantedPermission(extractPermissionsName(mPermissions));
        } else {
            Intent intent = new Intent(mContext, FakeActivity.class);
            intent.putParcelableArrayListExtra(Constants.PERMISSIONS, mPermissions);
            mContext.startActivity(intent);
        }


        registerMyBroadCast();
    }

    private void registerMyBroadCast() {
        //local broadcast for events
        mPermissionResultsBroadCastReceiver = new PermissionResultsBroadCastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PERMISSION_BROADCAST_INTENT);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mPermissionResultsBroadCastReceiver, filter);

    }

    class PermissionResultsBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String[] permissions = intent.getStringArrayExtra(Constants.PERMISSIONS);
            int[] grantResults = intent.getIntArrayExtra(Constants.GRANT_RESULTS);

            List<String> grantedPermissions = new ArrayList<>();
            List<String> deniedPermissions = new ArrayList<>();

            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    grantedPermissions.add(permissions[i]);
                } else {
                    deniedPermissions.add(permissions[i]);
                }
            }

                mPermissionListener.rejected(deniedPermissions);


                mPermissionListener.grantedPermission(grantedPermissions);


            //unregister the receiver
            LocalBroadcastManager.getInstance(context).unregisterReceiver(mPermissionResultsBroadCastReceiver);

        }
    }

    private List<String> extractPermissionsName(List<RuntimePermission> permissions){

        List<String> permissionList = new ArrayList<>();

        for (int i = 0; i < permissions.size() ; i++) {
            permissionList.add(permissions.get(i).getPermissionName());
        }

        return permissionList;

    }

    public interface GrantPermissionListener{

        void grantedPermission(List<String> permissions);

        void rejected(List<String> permissions);

    }


}
