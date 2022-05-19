package com.mosaza.falldetectionapp.Classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Fall implements Parcelable {
    private String id, userID;
    private Date fallDate;
    private double latitude, longitude;

    public Fall() {
    }

    public Fall(String userID, Date fallDate, double latitude, double longitude) {
        this.userID = userID;
        this.fallDate = fallDate;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Date getFallDate() {
        return fallDate;
    }

    public void setFallDate(Date fallDate) {
        this.fallDate = fallDate;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Fall createFromParcel(Parcel in) {
            return new Fall(in);
        }

        public Fall[] newArray(int size) {
            return new Fall[size];
        }
    };

    public Fall(Parcel in){
        this.id = in.readString();
        this.userID = in.readString();
        this.fallDate = (Date) in.readSerializable();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.userID);
        parcel.writeSerializable(this.fallDate);
        parcel.writeDouble(this.latitude);
        parcel.writeDouble(this.longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Fall{" + '\'' +
                "ID = '" + this.id + '\'' +
                "User ID = '" + this.userID + '\'' +
                "Fall Date = '" + this.fallDate + '\'' +
                '}';
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        map.put("userID", this.userID);
        map.put("fallDate", this.fallDate);
        map.put("latitude", this.latitude);
        map.put("longitude", this.longitude);

        return map;
    }

    public void documentToObject(DocumentSnapshot document){
        this.id = document.getId();
        this.userID = document.getString("userID");
        this.fallDate = document.getDate("fallDate");
        this.latitude = document.getDouble("latitude");
        this.longitude = document.getDouble("longitude");
    }

    public void documentToObject(QueryDocumentSnapshot document){
        this.id = document.getId();
        this.userID = document.getString("userID");
        this.fallDate = document.getDate("fallDate");
        this.latitude = document.getDouble("latitude");
        this.longitude = document.getDouble("longitude");
    }
}
