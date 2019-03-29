package com.example.mycar;

public class LocationDetail {
    String address;
    double latitude;
    double longitude;
    double altitude;

    LocationDetail(){
        this.address = "";
        this.altitude = 0;
        this.latitude = 0;
        this.longitude = 0;
    }

    LocationDetail(String address,double altitude,double latitude,double longitude){
        this.address = address;
        this.altitude = altitude;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLocation(double altitude,double latitude,double longitude) {
        this.altitude = altitude;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
