package com.wasn.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * To hold attributes of transaction
 *
 * @author erangaeb@gmail.com (eranga bandara)
 * TODO implements Parcelable
 */
public class Transaction implements Parcelable{

    int id;
    String clientName;
    String clientNic;
    String clientAccountNo;
    String previousBalance;
    int transactionAmount;
    String transactionTime;
    String transactionType;

    public Transaction(int id,
                       String clientName,
                       String clientNic,
                       String clientAccountNo,
                       String previousBalance,
                       int transactionAmount,
                       String transactionTime,
                       String transactionType) {
        this.id = id;
        this.clientName = clientName;
        this.clientNic = clientNic;
        this.clientAccountNo = clientAccountNo;
        this.previousBalance = previousBalance;
        this.transactionAmount = transactionAmount;
        this.transactionTime = transactionTime;
        this.transactionType = transactionType;
    }

    protected Transaction(Parcel in) {
        id = in.readInt();
        clientName = in.readString();
        clientNic = in.readString();
        clientAccountNo = in.readString();
        previousBalance = in.readString();
        transactionAmount = in.readInt();
        transactionTime = in.readString();
        transactionType = in.readString();
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientNic() {
        return clientNic;
    }

    public void setClientNic(String clientNic) {
        this.clientNic = clientNic;
    }

    public String getClientAccountNo() {
        return clientAccountNo;
    }

    public void setClientAccountNo(String clientAccountNo) {
        this.clientAccountNo = clientAccountNo;
    }

    public String getPreviousBalance() {
        return previousBalance;
    }

    public void setPreviousBalance(String previousBalance) {
        this.previousBalance = previousBalance;
    }

    public int getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(int transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(clientName);
        parcel.writeString(clientNic);
        parcel.writeString(clientAccountNo);
        parcel.writeString(previousBalance);
        parcel.writeInt(transactionAmount);
        parcel.writeString(transactionTime);
        parcel.writeString(transactionType);
    }
}
