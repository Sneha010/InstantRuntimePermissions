package com.permission.pleaserequest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Size;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User :   Sneha Khadatare
 * Date :   4/29/2016
 * Time :   3:57 PM IST
 */
public class PleaseRequest {


    private Context mContext;
    private String[] mPermissions;
    private String[] mExtraMessages;
    private static GrantPermissionListener mPermissionListener;

    private PleaseRequest(Context context) {
        mContext = context;
    }

    public static PleaseRequest inside(Context context){

        return new PleaseRequest(context);

    }


    public PleaseRequest forPermissions(@NonNull @Size(min = 1) String... permissions) {
        if (permissions.length == 0) {
            throw new IllegalArgumentException("The Permissions to request are missing");
        }
        this.mPermissions = permissions;
        return this;
    }


    public PleaseRequest withExtraExplanation(@NonNull String... extraMessages) {
        this.mExtraMessages = extraMessages;
        return this;
    }



    public interface GrantPermissionListener{

        void grantedPermission(List<String> permissions);

        void rejected(List<String> permissions);

    }


    public void request(GrantPermissionListener listener) {

        this.mPermissionListener = listener;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mPermissionListener.grantedPermission(Arrays.asList(mPermissions));
            mPermissionListener.rejected(new ArrayList<String>());
        } else {
            Intent intent = new Intent(mContext, FakeActivity.class);
            intent.putExtra(Constants.PERMISSIONS, mPermissions);
            intent.putExtra(Constants.EXTRA_MESSAGES, mExtraMessages);
            mContext.startActivity(intent);
        }

    }

    public static class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String[] permissions = intent.getStringArrayExtra(Constants.PERMISSIONS);
            int[] grantResults = intent.getIntArrayExtra(Constants.GRANT_RESULTS);
            Map<String, Boolean> permissionGrantResults = new HashMap<>();
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
        }
    }


}
