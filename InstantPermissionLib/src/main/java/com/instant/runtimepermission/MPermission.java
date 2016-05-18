package com.instant.runtimepermission;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sneha Khadatare : 587823
 * on 5/13/2016.
 */
public class MPermission implements Parcelable{

    private String mPermissionName ;
    private String mMessageOnDenial;

    public MPermission(String permissionName, String messageOnDenial) {
        mPermissionName = permissionName;
        mMessageOnDenial = messageOnDenial;
    }

    protected MPermission(Parcel in) {
        mPermissionName = in.readString();
        mMessageOnDenial = in.readString();
    }

    public static final Creator<MPermission> CREATOR = new Creator<MPermission>() {
        @Override
        public MPermission createFromParcel(Parcel in) {
            return new MPermission(in);
        }

        @Override
        public MPermission[] newArray(int size) {
            return new MPermission[size];
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
