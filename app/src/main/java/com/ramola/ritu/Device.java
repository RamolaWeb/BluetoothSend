package com.ramola.ritu;


import android.os.Parcel;
import android.os.Parcelable;

public class Device implements Parcelable {

    public String name,address;

    public Device(String name, String address) {
        this.name = name;
        this.address = address;
    }

    protected Device(Parcel in) {
        name = in.readString();
        address = in.readString();
    }

    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(address);
    }
}
