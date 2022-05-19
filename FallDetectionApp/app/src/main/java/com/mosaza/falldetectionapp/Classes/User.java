package com.mosaza.falldetectionapp.Classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class User implements Parcelable {
    private String id, username, firstName, lastName, email, phone, serverIPAddress;
    private double latitude, longitude;

    public User() {
    }

    public User(String id, String username, String firstName, String lastName, String email, String phone,
                String serverIPAddress, double latitude, double longitude) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.serverIPAddress = serverIPAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getServerIPAddress() {
        return serverIPAddress;
    }

    public void setServerIPAddress(String serverIPAddress) {
        this.serverIPAddress = serverIPAddress;
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
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public User(Parcel in){
        this.id = in.readString();
        this.username = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.email = in.readString();
        this.phone = in.readString();
        this.serverIPAddress = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.username);
        parcel.writeString(this.firstName);
        parcel.writeString(this.lastName);
        parcel.writeString(this.email);
        parcel.writeString(this.phone);
        parcel.writeString(this.serverIPAddress);
        parcel.writeDouble(this.latitude);
        parcel.writeDouble(this.longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "User{" + '\'' +
                "ID = '" + this.id + '\'' +
                "Username = '" + this.username + '\'' +
                "Email = '" + this.email + '\'' +
                '}';
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        map.put("username", this.username);
        map.put("firstName", this.firstName);
        map.put("lastName", this.lastName);
        map.put("email", this.email);
        map.put("phone", this.phone);
        map.put("serverIPAddress", this.serverIPAddress);
        map.put("latitude", this.latitude);
        map.put("longitude", this.longitude);

        return map;
    }

    public void documentToObject(DocumentSnapshot document){
        this.id = document.getId();
        this.username = document.getString("username");
        this.firstName = document.getString("firstName");
        this.lastName = document.getString("lastName");
        this.email = document.getString("email");
        this.phone = document.getString("phone");
        this.serverIPAddress = document.getString("serverIPAddress");
        this.latitude = document.getDouble("latitude");
        this.longitude = document.getDouble("longitude");
    }
}
