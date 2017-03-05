package com.wasn.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by eranga on 3/5/17.
 */

public class Account implements Parcelable {

    String accNo;
    String name;
    String type;

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Account(String accNo, String name, String type) {
        this.accNo = accNo;
        this.name = name;
        this.type = type;
    }

    protected Account(Parcel in) {
        accNo = in.readString();
        name = in.readString();
        type = in.readString();
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(accNo);
        dest.writeString(name);
        dest.writeString(type);
    }
}
