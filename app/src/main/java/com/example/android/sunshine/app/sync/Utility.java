/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine.app.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.data.WeatherContract;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Utility {
    public static String getPreferredLocationCity(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_locationCity_key),
                context.getString(R.string.pref_default_city));
    }

    public static String getPreferredLocationCountry(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_locationCountry_key),
                context.getString(R.string.pref_default_country));
    }

    public static boolean isMetric(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return  prefs.getString("units",
                "metric")
                .equals("metric");
    }

    public static String formatTemperature(double temperature, boolean isMetric) {
        double temp;
        if ( !isMetric ) {
            temp = 9*temperature/5+32;
        } else {
            temp = temperature;
        }
        return String.format("%.0f", temp);
    }


    /**
     * Helper method to provide the icon resource id according to the weather condition icon id returned
     * by the OpenWeatherMap call.
     * @param weatherIconId from OpenWeatherMap API response
     * @return resource id for the corresponding icon. -1 if no relation is found.
     */
    public static int getIconResourceForWeatherCondition(String weatherIconId) {
        switch (weatherIconId){
            case "01d":
                return R.drawable.ic_clear;
            case "01n":
                return R.drawable.ic_clear;
            case "02d":
                return R.drawable.ic_light_clouds;
            case "02n":
                return R.drawable.ic_light_clouds;
            case "03d":
                return R.drawable.ic_cloudy;
            case "03n":
                return R.drawable.ic_cloudy;
            case "04d":
                return R.drawable.ic_cloudy;
            case "04n":
                return R.drawable.ic_cloudy;
            case "09d":
                return R.drawable.ic_rain;
            case "09n":
                return R.drawable.ic_rain;
            case "10d":
                return R.drawable.ic_light_rain;
            case "10n":
                return R.drawable.ic_light_rain;
            case "11d":
                return R.drawable.ic_storm;
            case "11n":
                return R.drawable.ic_storm;
            case "13d":
                return R.drawable.ic_snow;
            case "13n":
                return R.drawable.ic_snow;
            case "50d":
                return R.drawable.ic_fog;
            case "50n":
                return R.drawable.ic_fog;
            default:
                return -1;
        }
    }

    /**
     * Helper method to provide the icon resource id according to the weather condition icon id returned
     * by the OpenWeatherMap call.
     * @param weatherIconId from OpenWeatherMap API response
     * @return resource id for the corresponding icon. -1 if no relation is found.
     */
    public static int getArtResourceForWeatherCondition(String weatherIconId) {
        switch (weatherIconId){
            case "01d":
                return R.drawable.art_clear;
            case "01n":
                return R.drawable.art_clear;
            case "02d":
                return R.drawable.art_light_clouds;
            case "02n":
                return R.drawable.art_light_clouds;
            case "03d":
                return R.drawable.art_clouds;
            case "03n":
                return R.drawable.art_clouds;
            case "04d":
                return R.drawable.art_clouds;
            case "04n":
                return R.drawable.art_clouds;
            case "09d":
                return R.drawable.art_rain;
            case "09n":
                return R.drawable.art_rain;
            case "10d":
                return R.drawable.art_light_rain;
            case "10n":
                return R.drawable.art_light_rain;
            case "11d":
                return R.drawable.art_storm;
            case "11n":
                return R.drawable.art_storm;
            case "13d":
                return R.drawable.art_snow;
            case "13n":
                return R.drawable.art_snow;
            case "50d":
                return R.drawable.art_fog;
            case "50n":
                return R.drawable.art_fog;
            default:
                return -1;
        }
    }

    /*Change the date format from integer string to the ones that can be displayed properly on the screen*/

    public static String changeDateFormat(String s){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String date = "";
       try{ Date newDate = format.parse(s);
           format = new SimpleDateFormat("yyyy-MMM-dd c");
           date = format.format(newDate);}
       catch (ParseException e){
           Log.v(Utility.class.getSimpleName(), "changeDateFormatIssue!");
       }
        return date;
    }
}
