package com.example.weatherapiaccess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView weatherText;
    private Context cont = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //final Intent getWeather = new Intent(this, WeatherInfoParser.class);

        weatherText = findViewById(R.id.weatherDisplay);

        final Button button = findViewById(R.id.weatherButton);

        //Pass the text field to be changed to the WeatherInfoRetriever and execute the retriever
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new WeatherInfoRetriever(cont).execute(weatherText);
            }
        });

    }

}
