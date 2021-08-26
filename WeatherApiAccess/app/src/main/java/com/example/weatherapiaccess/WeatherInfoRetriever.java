package com.example.weatherapiaccess;

import android.app.Activity;
import android.content.Context;

import android.location.Location;

import android.location.LocationManager;
import android.os.AsyncTask;

import android.os.Looper;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class WeatherInfoRetriever extends AsyncTask<TextView, Void, String> {

    //The key used to make calls to the OpenWeatherMap API
    private final String appId = "0f53a4251a2bfd0754662cac0302d2b3";

    //The text field that'll store the result
    private TextView weatherInfoText;

    private Context context;

    private double lat = 0;
    private double lon = 0;

    public WeatherInfoRetriever(Context cont) {
        context  = cont;
    }

    @Override
    protected String doInBackground(TextView... text) {
        weatherInfoText = text[0];

        //Request permission from the user to use the device's GPS
        requestPermission();

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Looper.prepare();

        //Try to get location
        try {
            try {
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                lat = location.getLatitude();
                lon = location.getLongitude();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //Credit to https://www.spaceotechnologies.com/implement-openweathermap-api-android-app-tutorial/
        //Request API call using latitude and longitude
        try {
            //Define the url that will be used to make the API call
            String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + (int)lat + "&lon=" + (int)lon + "&APPID=" + appId;
            //Connect to the web-page that will print the API call's response
            HttpURLConnection apiConnection = (HttpURLConnection) (new URL(url)).openConnection();

            apiConnection.setRequestMethod("GET");
            apiConnection.setDoInput(true);
            apiConnection.setDoOutput(true);
            apiConnection.connect();

            //Read the response
            StringBuffer buffer = null;
            buffer = new StringBuffer();
            InputStream input = apiConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line = null;

            while ( (line = reader.readLine()) != null ) {
                buffer.append(line + "\r\n");
            }
            input.close();
            apiConnection.disconnect();

            return buffer.toString();
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        //If API call failed
        return "";
    }

    @Override
    protected void onPostExecute(String weatherInfo) {

        if(weatherInfo.isEmpty())
        {
            weatherInfoText.setText("Error: Weather info collection failed");
        }
        else
        {
            //Prepare the output string
            StringBuilder resultString = new StringBuilder();

            /*
            resultString.append("Longitude: ");
            resultString.append(lon);
            resultString.append("\n");

            resultString.append("Latitude: ");
            resultString.append(lat);
            resultString.append("\n");
            */

            //Add the weather condition
            resultString.append("Weather: ");
            resultString.append(weatherInfo.substring((weatherInfo.indexOf("\"main\":\"") + 8),
                    (weatherInfo.indexOf("\"main\":\"") + 8) + weatherInfo.substring(weatherInfo.indexOf("\"main\":\"") + 8).indexOf('\"')));
            resultString.append("\n");

            //Add the temperature (after converting from Kelvin to Fahrenheit)
            double temp = Double.valueOf(weatherInfo.substring((weatherInfo.indexOf("\"temp\":") + 7),
                    (weatherInfo.indexOf("\"temp\":") + 7) + weatherInfo.substring(weatherInfo.indexOf("\"temp\":") + 7).indexOf(',')));
            temp = (temp - 273.15) * 9 / 5 + 32;
            resultString.append("Temperature: ");
            resultString.append((Math.round(temp * Math.pow(10, 3)) / Math.pow(10, 3)) + "°F");
            resultString.append("\n");

            //Add the humidity
            double humidity = Double.valueOf(weatherInfo.substring((weatherInfo.indexOf("\"humidity\":") + 11),
                    (weatherInfo.indexOf("\"humidity\":") + 11) + weatherInfo.substring(weatherInfo.indexOf("\"humidity\":") + 11).indexOf(',')));
            resultString.append("Humidity: ");
            resultString.append(humidity + "%");
            resultString.append("\n");

            //Add the pressure (after converting hPa to inHg)
            double pressure = Double.valueOf(weatherInfo.substring((weatherInfo.indexOf("\"pressure\":") + 11),
                    (weatherInfo.indexOf("\"pressure\":") + 11) + weatherInfo.substring(weatherInfo.indexOf("\"pressure\":") + 11).indexOf(',')));
            resultString.append("Pressure: ");
            resultString.append((pressure * 0.03) + " inHg");
            resultString.append("\n");

            //Add the wind speed (after converting from m/s to mph)
            double speed = Double.valueOf(weatherInfo.substring((weatherInfo.indexOf("\"speed\":") + 8),
                    (weatherInfo.indexOf("\"speed\":") + 8) + weatherInfo.substring(weatherInfo.indexOf("\"speed\":") + 8).indexOf(',')));
            speed = speed * 2.237;
            resultString.append("Wind Speed: ");
            resultString.append((Math.round(temp * Math.pow(10, 2)) / Math.pow(10, 2)) + " mph");


            //Set the output string to the text in the given TextView
            try {
                weatherInfoText.setText(resultString);
                //weatherInfoText.setText(lat + " " + lon);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions((Activity) context, new String[]{ACCESS_FINE_LOCATION}, 1);
    }

}
