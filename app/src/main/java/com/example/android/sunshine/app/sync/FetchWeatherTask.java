package com.example.android.sunshine.app.sync;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.data.WeatherContract.WeatherEntry;
import com.example.android.sunshine.app.data.WeatherContract.LocationEntry;
import com.example.android.sunshine.app.data.WeatherContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Vector;

/**
 * Created by kenzhang on 15-11-21.
 */
public class FetchWeatherTask extends AsyncTask<String, Void, Void> {
    private final Context mContext;

    public FetchWeatherTask(Context context) {
        mContext = context;
    }

    private void notification(){
        MainActivity mainScreen = new MainActivity();
        Toast.makeText(mainScreen.getApplicationContext(),
                "No Data! Please Check Your Location Settings", Toast.LENGTH_LONG).show();
    }





    /*
    Prepare the weather high/lows for presentation
     */
    private String formatHighLows(double high, double low) {
        // Data is fetched in Celsius by default.
        // If user prefers to see in Fahrenheit, convert the values here.
        // We do this rather than fetching in Fahrenheit so that the user can
        // change this option without us having to re-fetch the data once
        // we start storing the values in a database.
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        String unitType = sharedPrefs.getString(
                mContext.getString(R.string.pref_units_key),
                mContext.getString(R.string.pref_units_metric));
        if (unitType.equals(mContext.getString(R.string.pref_units_imperial))) {
            high = (high * 1.8) + 32;
            low = (low * 1.8) + 32;
        } else if (!unitType.equals(mContext.getString(R.string.pref_units_metric))) {
        }
        // for presentation, assume the user doesn't care about tenths of a degree
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    /**
     * Helper method to handle insertion of a new location in the weather database.
     *
     * @param Loc_setting_city    The location city string used to request updates from the server.
     * @param Loc_setting_country The location country string used to request updates from the server
     * @param lat                 the latitude of the city
     * @param lon                 the longitude of the city
     * @return the row ID of the added location.
     */
    private long addLocation(String Loc_setting_city, String Loc_setting_country,
                             double lat, double lon) {
        // First, check if the location with this city name exists in the db
        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                new String[]{LocationEntry._ID},
                LocationEntry.COLUMN_LOCATION_SETTING_CITY + " = ?"
                        + " AND " + LocationEntry.COLUMN_LOCATION_SETTING_COUNTRY + " = ?",
                new String[]{Loc_setting_city, Loc_setting_country},
                null);

        if (cursor.moveToFirst()) {
            // When you find the record in the database, there's no insertion. There's only return!
            int locationIdIndex = cursor.getColumnIndex(LocationEntry._ID);
            return cursor.getLong(locationIdIndex);
        } else {
            //These are the key-value pairs in the ContentValues
            ContentValues inservalues = new ContentValues();
            inservalues.put(LocationEntry.COLUMN_LOCATION_SETTING_CITY, Loc_setting_city);
            inservalues.put(LocationEntry.COLUMN_LOCATION_SETTING_COUNTRY, Loc_setting_country);
            inservalues.put(LocationEntry.COLUMN_COORD_LAT, lat);
            inservalues.put(LocationEntry.COLUMN_COORD_LONG, lon);

            Uri locationUri = mContext.getContentResolver().
                    insert(LocationEntry.CONTENT_URI, inservalues);
            long locationRowId = ContentUris.parseId(locationUri);

            return locationRowId;
        }
    }

    /*
        Take the String representing the complete forecast in JSON Format and pull out the data we
        need to construct the Strings needed for the wireframes.
     */
    private void getWeatherDataFromJson(String forcastJsonStr, int numDays,
                                        String Loc_setting_city, String Loc_setting_country)
            throws JSONException {

        // Location information
        final String OWM_CITY = "city";
        final String OWM_COORD = "coord";
        final String OWM_COORD_LAT = "lat";
        final String OWM_COORD_LONG = "lon";

        // Weather information.  Each day's forecast info is an element of the "list" array.

        final String OWM_LIST = "list";
        final String OWM_DT = "dt";
        final String OWM_PRESSURE = "pressure";
        final String OWM_HUMIDITY = "humidity";
        final String OWM_WINDSPEED = "speed";
        final String OWM_WIND_DIRECTION = "deg";

        // All temperatures are children of the "temp" object.
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_WEATHER = "weather";
        final String OWM_DESCRIPTION = "main";
        final String OWM_WEATHER_ID = "id";
        final String OWM_WEATHER_ICON_ID = "icon";

        JSONObject forecastJson = new JSONObject(forcastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        JSONObject cityJson = forecastJson.getJSONObject(OWM_CITY);
        JSONObject coordJSON = cityJson.getJSONObject(OWM_COORD);
        double cityLatitude = coordJSON.getLong(OWM_COORD_LAT);
        double cityLongitude = coordJSON.getLong(OWM_COORD_LONG);

        // Insert the location into the database. Get the locationID in order
        // to refer to the correct location city and country
        long locationID = addLocation(Loc_setting_city, Loc_setting_country, cityLatitude, cityLongitude);

        // Get and insert the new weather information into the database
        Vector<ContentValues> cVVector = new Vector<ContentValues>(weatherArray.length());

        String[] resultStrs = new String[numDays];

        for (int i = 0; i < weatherArray.length(); i++) {
            // These are the values that will be collected.
            long dateTime;
            double pressure;
            int humidity;
            double windSpeed;
            double windDirection;

            double high;
            double low;

            String description;
            int weatherId;
            String Weather_Icon_ID;

            //Get the JSON object representing the day.
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            // The date/time is returned as a long.
            dateTime = dayForecast.getLong(OWM_DT);
            pressure = dayForecast.getDouble(OWM_PRESSURE);
            humidity = dayForecast.getInt(OWM_HUMIDITY);
            windSpeed = dayForecast.getDouble(OWM_WINDSPEED);
            windDirection = dayForecast.getDouble(OWM_WIND_DIRECTION);

            // Description is in a child array called "weather", which is 1 element long.
            // That element also contains a weather code.
            JSONObject weatherObject =
                    dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);
            weatherId = weatherObject.getInt(OWM_WEATHER_ID);
            Weather_Icon_ID = weatherObject.getString(OWM_WEATHER_ICON_ID);

            // Temperatures are in a child object called "temp".  .
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            high = temperatureObject.getDouble(OWM_MAX);
            low = temperatureObject.getDouble(OWM_MIN);

            ContentValues weatherValues = new ContentValues();

            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationID);
            weatherValues.put(WeatherEntry.COLUMN_DATE,
                    WeatherContract.getDbDateString(new Date(dateTime * 1000L)));
            weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, humidity);
            weatherValues.put(WeatherEntry.COLUMN_PRESSURE, pressure);
            weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
            weatherValues.put(WeatherEntry.COLUMN_DEGREES, windDirection);
            weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, high);
            weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, low);
            weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, description);
            weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, weatherId);
            weatherValues.put(WeatherEntry.COLUMN_ICON_ID,  Weather_Icon_ID);
            cVVector.add(weatherValues);
        }
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            mContext.getContentResolver().bulkInsert(WeatherEntry.CONTENT_URI, cvArray);
        }
    }

    @Override
    protected Void doInBackground (String... params) {
        if (params.length == 0) {
            return null;
        }

        String locationCity = params[0];
        String locationCountry = params[1];

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        String format = "json";
        String units = "metric";
        String ID = "aa89adb4261e6003c9c8c261dc82dc34";
        int numDays = 14;

        try {
            // Construct the URL for the OpenWeatherMap query
            final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
            final String QUERY_PARAM = "q";
            final String APPID_PARAM = "APPID";
            final String FORMAT_PARAM = "mode";
            final String UNITS_PARAM = "units";
            final String DAYS_PARAM = "cnt";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, params[0] + "," + params[1])
                    .appendQueryParameter(APPID_PARAM, ID)
                    .appendQueryParameter(FORMAT_PARAM, format)
                    .appendQueryParameter(UNITS_PARAM, units)
                    .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                    .build();
            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            forecastJsonStr = buffer.toString();
        } catch (IOException e) {
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                }
            }
        }
        try {
            getWeatherDataFromJson(forecastJsonStr, numDays, locationCity, locationCountry);
        } catch (JSONException e) {
        }
        return null;
    }
}