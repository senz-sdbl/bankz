package com.wasn.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * POJO class to hold settings attributes
 */
public class Setting implements Parcelable {
    String agent;
    String branch;
    String telephone;
    String printerAddress;

    public Setting(String agent, String branch, String telephone, String printerAddress) {
        this.agent = agent;
        this.branch = branch;
        this.telephone = telephone;
        this.printerAddress = printerAddress;
    }

    protected Setting(Parcel in) {
        agent = in.readString();
        branch = in.readString();
        telephone = in.readString();
        printerAddress = in.readString();
    }

    public static final Creator<Setting> CREATOR = new Creator<Setting>() {
        @Override
        public Setting createFromParcel(Parcel in) {
            return new Setting(in);
        }

        @Override
        public Setting[] newArray(int size) {
            return new Setting[size];
        }
    };

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPrinterAddress() {
        return printerAddress;
    }

    public void setPrinterAddress(String printerAddress) {
        this.printerAddress = printerAddress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(agent);
        dest.writeString(branch);
        dest.writeString(telephone);
        dest.writeString(printerAddress);
    }
}

