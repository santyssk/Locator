package com.example.mycar;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.johnhiott.darkskyandroidlib.ForecastApi;
import com.johnhiott.darkskyandroidlib.RequestBuilder;
import com.johnhiott.darkskyandroidlib.models.Request;
import com.johnhiott.darkskyandroidlib.models.WeatherResponse;

import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static java.lang.Math.round;

public class MainActivity extends AppCompatActivity {

    private CountDownTimer countDownTimer;
    private LocationClass current_location;
    private LocationClass parked_location;
    private RelativeLayout.LayoutParams image_LayoutParams, text_LayoutParams;
    private Button park,navigate,refresh,clear;
    private SharedPreferences sharedPreferences;
    private TextView temperature,humidity,timer,wind;

    private FusedLocationProviderClient fusedLocationClient;

    public void get_weather(double latitude, double longitude) {
        final RequestBuilder weather = new RequestBuilder();

        Request request = new Request();
        request.setLat(latitude + "");
        request.setLng(longitude + "");
        request.setUnits(Request.Units.SI);
        request.setLanguage(Request.Language.PIG_LATIN);
        request.addExtendBlock(Request.Block.CURRENTLY);
        weather.getWeather(request, new Callback<WeatherResponse>() {
            @Override
            public void success(WeatherResponse weatherResponse, Response response) {
                Log.d("Weather Humidity", weatherResponse.getCurrently().getHumidity());
                humidity.setText(round(Double.parseDouble(weatherResponse.getCurrently().getHumidity()) * 100)+"");
                Log.d("Weather Precipitation", weatherResponse.getCurrently().getPrecipProbability());
                Log.d("Weather Visibility", weatherResponse.getCurrently().getVisibility());
                Log.d("Weather Wind", weatherResponse.getCurrently().getWindSpeed());
                wind.setText(weatherResponse.getCurrently().getWindSpeed() + " km/h");
                Log.d("Weather Temp", String.valueOf(weatherResponse.getCurrently().getTemperature()));
                temperature.setText(weatherResponse.getCurrently().getTemperature() + " C");
                Log.d("Weather Cloud", weatherResponse.getCurrently().getCloudClover());
                Log.d("Weather Alert", String.valueOf(weatherResponse.getAlerts()));
                Log.d("Weather Icon", weatherResponse.getCurrently().getIcon());
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.d("Weather ", " Error calling: " + retrofitError.getUrl());
            }
        });
    }

    public LocationClass get_current_location(final LocationClass locationClass,final boolean park) {

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            double altitude = round(location.getAltitude() * 100.0) / 100.0;
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            LocationDetail locationDetail = new LocationDetail();
                            locationDetail.setLocation(altitude,latitude,longitude);

                            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                            String address;
                            try {
                                List<Address> totaladdress;
                                totaladdress = geocoder.getFromLocation(latitude, longitude, 1);
                                address = totaladdress.get(0).getAddressLine(0);
                                String city = totaladdress.get(0).getLocality();
                                address = address.substring(0, address.indexOf(city) + city.length()).replace(", ", "\n");
                                locationDetail.setAddress(address);
                                locationClass.setLocationDetail(locationDetail);
                                locationClass.writeAddress();
                                Log.d("Location func", park + "\n" + address + "\n" + altitude);
                            } catch (Exception e) {
                                locationClass.writeAddress("Location\n" + latitude + ',' + longitude + "\nAltitude\n" + altitude);
                            }
                            if (park) {
                                Gson gson = new Gson();
                                String json = gson.toJson(locationDetail);
                                editor.putString("location_address", json);
                                editor.commit();
                            }
                            get_weather(latitude,longitude);
                        } else {
                            locationClass.writeAddress("Location not found");
                            humidity.setText("");
                            temperature.setText("");
                            wind.setText("");
                        }

                    }
                });
        return locationClass;
    }

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.ACCESS_COARSE_LOCATION",
                "android.permission.ACCESS_FINE_LOCATION"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
        boolean s= Settings.System.canWrite(this);
        if(!s){
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS );
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (shouldAskPermissions()) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED||ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                askPermissions();
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        ForecastApi.create("Dark-Sky API Key");
        setContentView(R.layout.activity_main);

        parked_location = new LocationClass(this);
        current_location = new LocationClass(this);
        parked_location.setImageView(R.id.parkimage);
        parked_location.setTextView(R.id.parkedaddress);
        current_location.setImageView(R.id.currentimage);
        current_location.setTextView(R.id.currentaddress);
        park = findViewById(R.id.park);
        navigate = findViewById(R.id.navigate);
        refresh = findViewById(R.id.refresh);
        clear = findViewById(R.id.clear);
        temperature = findViewById(R.id.temperature);
        humidity = findViewById(R.id.humidity);
        wind = findViewById(R.id.wind);
        timer = findViewById(R.id.timer);

        sharedPreferences = getSharedPreferences("Preference", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("location_address",null);
        LocationDetail locationDetail = gson.fromJson(json,LocationDetail.class);
        parked_location.setLocationDetail(locationDetail);
        image_LayoutParams = current_location.getImageLayout();
        text_LayoutParams = current_location.getTextLayout();

        if(parked_location.getAddress()!=""){
            parked_location.makeVisible();
            parked_location.writeAddress();
            navigate.setVisibility(View.VISIBLE);
            clear.setVisibility(View.VISIBLE);
        }
        else{
            current_location.setTextLayout(parked_location.getTextLayout());
            current_location.setImageLayout(parked_location.getImageLayout());
        }
        current_location = get_current_location(current_location,false);
        if(parked_location.getAddress()!="")
            countdown();

        park.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this,Park.class);
            startActivityForResult(intent,311);
            }
        });

        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(parked_location.getAddress()=="")
                    Log.d("Navigate","Error");
                else{
                    Uri uri = Uri.parse("google.navigation:q="+parked_location.getLatitude()+','+parked_location.getLongitude()+"&mode=w");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);
                }
             }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_location = get_current_location(current_location,false);
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.edit().remove("location_address").apply();
                parked_location.setLocationDetail(new LocationDetail());
                parked_location.makeInvisible();
                navigate.setVisibility(View.INVISIBLE);
                clear.setVisibility(View.INVISIBLE);
                current_location.setTextLayout(parked_location.getTextLayout());
                current_location.setImageLayout(parked_location.getImageLayout());
                if(countDownTimer!=null)
                    countDownTimer.cancel();
                timer.setText("");
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(MainActivity.this, Remind.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
                if(alarmManager!=null)
                    alarmManager.cancel(pendingIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that it is the SecondActivity with an OK result
        if (requestCode == 311) {
            if (resultCode == Activity.RESULT_OK) {
                parked_location.makeVisible();
                parked_location = get_current_location(parked_location,true);
                countdown();
                navigate.setVisibility(View.VISIBLE);
                refresh.setVisibility(View.VISIBLE);
                clear.setVisibility(View.VISIBLE);
                current_location.setTextLayout(text_LayoutParams);
                current_location.setImageLayout(image_LayoutParams);
            }
        }
    }

    public void countdown(){
        long time_left = sharedPreferences.getLong("time_left",0);
        long remind_time = sharedPreferences.getLong("remind_left",0);
        //final long remind_time = 15000;
        if(countDownTimer!=null)
            countDownTimer.cancel();
        if(time_left==0){
            timer.setText("");
            return;
        }
        Log.d("Count_Time_max",time_left+"");
        long current_time = System.currentTimeMillis();
        Log.d("Count_Current_time",current_time+"");
        time_left-=current_time;
        Log.d("Count_Time_left",time_left+"");
        Log.d("Count_Remind_time",remind_time+"");

        //time_left = 30000;
        if(time_left>0) {
            if(time_left>remind_time && remind_time>0) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(this, Remind.class);
                intent.putExtra("remind_time", remind_time);
                intent.putExtra("address",parked_location.getAddress());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
                if(alarmManager!=null)
                    alarmManager.cancel(pendingIntent);
                remind_time = current_time+time_left-remind_time;
                Log.d("Count_Notify",remind_time+"");
                alarmManager.set(AlarmManager.RTC, remind_time,pendingIntent);
            }
            countDownTimer = new CountDownTimer(time_left, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    int hour = (int) (millisUntilFinished / 3600000);
                    millisUntilFinished = millisUntilFinished % 3600000;
                    int minute = (int) (millisUntilFinished / 60000);
                    millisUntilFinished = millisUntilFinished % 60000;
                    int second = (int) (millisUntilFinished / 1000);
                    timer.setText("Parking time left "+ hour + " : " + minute + " : " + second);
                }

                @Override
                public void onFinish() {
                    timer.setText("Parking time exceeded");
                }
            }.start();
        }
        else
            timer.setText("Parking time exceeded");
    }
}
