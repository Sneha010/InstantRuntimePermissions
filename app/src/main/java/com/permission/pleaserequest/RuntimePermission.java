package com.permission.pleaserequest;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sneha Khadatare : 587823
 * on 5/13/2016.
 */
public class RuntimePermission implements Parcelable{

    private String mPermissionName ;
    private String mMessageOnDenial;

    public RuntimePermission(String permissionName, String messageOnDenial) {
        mPermissionName = permissionName;
        mMessageOnDenial = messageOnDenial;
    }

    protected RuntimePermission(Parcel in) {
        mPermissionName = in.readString();
        mMessageOnDenial = in.readString();
    }

    public static final Creator<RuntimePermission> CREATOR = new Creator<RuntimePermission>() {
        @Override
        public RuntimePermission createFromParcel(Parcel in) {
            return new RuntimePermission(in);
        }

        @Override
        public RuntimePermission[] newArray(int size) {
            return new RuntimePermission[size];
        }
    };

    public String getPermissionName() {
        return mPermissionName;
    }

    public void setPermissionName(String permissionName) {
        mPermissionName = permissionName;
    }

    public String getMessageOnDenial() {
        return mMessageOnDenial;
    }

    public void setMessageOnDenial(String messageOnDenial) {
        mMessageOnDenial = messageOnDenial;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPermissionName);
        dest.writeString(mMessageOnDenial);
    }
}
