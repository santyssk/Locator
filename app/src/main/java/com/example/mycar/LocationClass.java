package com.example.mycar;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LocationClass {
    private LocationDetail locationDetail;
    private TextView textView;
    private ImageView imageView;
    private Context context;

    LocationClass(Context context){
        this.context = context;
        this.locationDetail = new LocationDetail();
    }

    double getLatitude(){
        if(locationDetail==null)
            return 0;
        return locationDetail.latitude;
    }

    double getLongitude(){
        if(locationDetail==null)
            return 0;
        return locationDetail.longitude;
    }

    double getAltitude(){
        if(locationDetail==null)
            return 0;
        return locationDetail.altitude;
    }

    String getAddress(){
        if(locationDetail==null)
            return "";
        return locationDetail.address;
    }

    LocationDetail getLocationDetail(){
        return locationDetail;
    }
    void setLocationDetail(LocationDetail locationDetail){
        this.locationDetail = locationDetail;
    }

    void setTextView(int id){
        textView = ((Activity)context).findViewById(id);
    }

    void setImageView(int id){
        imageView = ((Activity)context).findViewById(id);
    }

    void writeAddress(){
        textView.setText(locationDetail.address+"\nAltitude : "+locationDetail.altitude);
    }

    void writeAddress(String message){
        textView.setText(message);
    }

    void makeVisible(){
        textView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
    }

    void makeInvisible(){
        textView.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.INVISIBLE);
    }

    RelativeLayout.LayoutParams getTextLayout(){
        return (RelativeLayout.LayoutParams) textView.getLayoutParams();
    }

    RelativeLayout.LayoutParams getImageLayout(){
        return (RelativeLayout.LayoutParams) imageView.getLayoutParams();
    }

    void setTextLayout(RelativeLayout.LayoutParams layoutParams){
        textView.setLayoutParams(layoutParams);
    }

    void setImageLayout(RelativeLayout.LayoutParams layoutParams){
        imageView.setLayoutParams(layoutParams);
    }
}
