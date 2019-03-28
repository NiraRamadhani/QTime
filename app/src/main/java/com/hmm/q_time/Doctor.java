package com.hmm.q_time;

import android.os.Parcel;
import android.os.Parcelable;

public class Doctor implements Parcelable {
    private String mId;
    private String mName;
    private String mType;
    private String mLatitude;
    private String mLongitude;
    private String mImageUrl;

    public Doctor(String id, String name, String type, String latitude, String longitude, String imageUrl) {
        mId = id;
        mName = name;
        mType = type;
        mLatitude = latitude;
        mLongitude = longitude;
        mImageUrl = imageUrl;
    }

    protected Doctor(Parcel in) {
        mId = in.readString();
        mName = in.readString();
        mType = in.readString();
        mLatitude = in.readString();
        mLongitude = in.readString();
        mImageUrl = in.readString();
    }

    public static final Creator<Doctor> CREATOR = new Creator<Doctor>() {
        @Override
        public Doctor createFromParcel(Parcel in) {
            return new Doctor(in);
        }

        @Override
        public Doctor[] newArray(int size) {
            return new Doctor[size];
        }
    };

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getLatitude() {
        return mLatitude;
    }

    public void setLatitude(String latitude) {
        mLatitude = latitude;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public void setLongitude(String longitude) {
        mLongitude = longitude;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mName);
        dest.writeString(mType);
        dest.writeString(mLatitude);
        dest.writeString(mLongitude);
        dest.writeString(mImageUrl);
    }
}
