package com.wasn.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * POJO class to hold attributes of transaction summary
 *
 * @author erangaeb@gmail.com (eranga bandara)
 */
public class Summary implements Parcelable {
    private String agent;
    private int count;
    private int total;
    private String time;

    public Summary(String agent, int count, int total, String time) {
        this.agent = agent;
        this.count = count;
        this.total = total;
        this.time = time;
    }

    protected Summary(Parcel in) {
        agent = in.readString();
        count = in.readInt();
        total = in.readInt();
        time = in.readString();
    }

    public static final Creator<Summary> CREATOR = new Creator<Summary>() {
        @Override
        public Summary createFromParcel(Parcel in) {
            return new Summary(in);
        }

        @Override
        public Summary[] newArray(int size) {
            return new Summary[size];
        }
    };

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(agent);
        dest.writeInt(count);
        dest.writeInt(total);
        dest.writeString(time);
    }
}
