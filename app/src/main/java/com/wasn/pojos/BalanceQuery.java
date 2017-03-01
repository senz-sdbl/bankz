package com.wasn.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * POJO class to keep Balance query result
 *
 * @author eranga herath(erangaeb@gmail.com)
 *         TODO implement Parcelabale
 */
public class BalanceQuery implements Parcelable {
    private String clientAccount;
    private String clientName;
    private String clientNic;
    private String balance;

    public BalanceQuery(String clientAccount, String clientName, String clientNic, String balance) {
        this.clientAccount = clientAccount;
        this.clientName = clientName;
        this.clientNic = clientNic;
        this.balance = balance;

    }

    protected BalanceQuery(Parcel in) {
        clientAccount = in.readString();
        clientName = in.readString();
        clientNic = in.readString();
        balance = in.readString();
    }

    public static final Creator<BalanceQuery> CREATOR = new Creator<BalanceQuery>() {
        @Override
        public BalanceQuery createFromParcel(Parcel in) {
            return new BalanceQuery(in);
        }

        @Override
        public BalanceQuery[] newArray(int size) {
            return new BalanceQuery[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getClientAccount() {
        return clientAccount;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientNic() {
        return clientNic;
    }

    public String getBalance() {
        return balance;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(clientAccount);
        parcel.writeString(clientName);
        parcel.writeString(clientNic);
        parcel.writeString(balance);
    }
}
