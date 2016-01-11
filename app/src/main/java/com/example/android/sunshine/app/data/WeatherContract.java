package com.example.android.sunshine.app.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kenzhang on 15-11-11.
 */
public class WeatherContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.android.sunshine.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot"
    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_WEATHER = "weather";
    public static final String PATH_LOCATION = "location";
    public static final String PATH_DESC = "description";

    // Format used for storing dates in the database.  ALso used for converting those strings
    // back into date objects for comparison/processing.
    public static final String DATE_FORMAT = "yyyyMMdd";

    /**
     * Converts Date class to a string representation, used for easy comparison and database lookup.
     * @param date The input date
     * @return a DB-friendly representation of the date, using the format defined in DATE_FORMAT.
     */
    public static String getDbDateString(Date date){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    /**
     * Converts a dateText to a long Unix time representation
     * @param dateText the input date string
     * @return the Date object
     */
    public static Date getDateFromDb(String dateText) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return dbDateFormat.parse(dateText);
        } catch ( ParseException e ) {
            e.printStackTrace();
            return null;
        }
    }


    /* Inner class that defines the table contents of the location table */
    public static final class LocationEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_LOCATION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        // Table name
        public static final String TABLE_NAME = "location";
        // The location setting string is what will be sent to openweathermap
        // as the location query.
        public static final String COLUMN_LOCATION_SETTING_CITY = "location_setting_city";
        public static final String COLUMN_LOCATION_SETTING_COUNTRY = "location_setting_country";

        // In order to uniquely pinpoint the location on the map when we launch the
        // map intent, we store the latitude and longitude as returned by openweathermap.
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LONG = "coord_long";

        public static Uri buildLocationUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

        /* Inner class that defines the table contents of the weather table */
    public static final class WeatherEntry implements BaseColumns{
            public static final Uri CONTENT_URI =
                    BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_WEATHER).build();

            public static final String CONTENT_TYPE =
                    ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
            public static final String CONTENT_ITEM_TYPE =
                    ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

            //Table name
         public static final String TABLE_NAME = "weather";

         // Column with the foreign key into the location table.
         public static final String COLUMN_LOC_KEY = "location_id";

         // Date, stored as long in milliseconds since the epoch///maybe stored as int//
         public  static final String COLUMN_DATE = "date";

         // Weather ID as returned by API, to identify the icon to be used

         public static final String COLUMN_WEATHER_ID = "weather_id";

         // Short description and long description of the weather, as provided by API.
         // e.g "clear" vs "sky is clear".
         public static final String COLUMN_SHORT_DESC = "short_desc";

         // Min and max temperatures for the day (stored as floats)
         public static final String COLUMN_MIN_TEMP = "min";
         public static final String COLUMN_MAX_TEMP = "max";

         // Humidity is stored as a float representing percentage
         public static final String COLUMN_HUMIDITY = "humidity";

         // Humidity is stored as a float representing percentage
         public static final String COLUMN_PRESSURE = "pressure";

         // Windspeed is stored as a float representing windspeed  mph
         public static final String COLUMN_WIND_SPEED = "wind";

         // Degrees are meteorological degrees (e.g, 0 is north, 180 is south).  Stored as floats.
         public static final String COLUMN_DEGREES = "degrees";

         // Users' descriptions are stored as string
         public static final String COLUMN_USER_DESC = "user_description";

         // Weather Icon to decide which icon will be used.
         public static final String COLUMN_ICON_ID = "ICON_ID";

            public static Uri buildWeatherUri(long id) {
                return ContentUris.withAppendedId(CONTENT_URI, id);
            }

            /*
            Student: This is the buildWeatherLocation function you filled in.
         */
            public static Uri buildWeatherLocation(String city, String country){
                return CONTENT_URI.buildUpon().appendPath(city).appendPath(country).build();
            }

            public static Uri buildWeatherLocationWithStartDate(
                    String city, String country,String startDate){
                return CONTENT_URI.buildUpon().appendPath(city).appendPath(country)
                        .appendQueryParameter(COLUMN_DATE, startDate).build();
            }

            public static Uri buildWeatherLocationWithDate(String city, String country, String date) {
                return CONTENT_URI.buildUpon().appendPath(city).appendPath(country)
                        .appendQueryParameter(COLUMN_DATE, date).build();
            }

            public static String[] getLocationSettingFromUri(Uri uri) {

                String[] locationSetting = new String[]{uri.getPathSegments().get(1), uri.getPathSegments().get(2)};

                return locationSetting;
            }

            public static String getDateFromUri(Uri uri) {
                return uri.getPathSegments().get(3);
            }

            public static String getStartDateFromUri(Uri uri) {
                return uri.getQueryParameter(COLUMN_DATE);
            }
     }

    public static final class DescriptionEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_DESC).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DESC;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DESC;

        // Table name
        public static final String TABLE_NAME = "description";
        //Entries in the table
        public static final String COLUMN_DATE = "Date";
        public static final String COLUMN_DESCRIPTION = "weather_description";
        public static final String COLUMN_CITY = "City";
        public static final String COLUMN_COUNTRY = "Country";


        /*
        * The below is the query uri that you need to fix
        * */
        /*
        // The location setting string is what will be sent to openweathermap
        // as the location query.
        public static final String COLUMN_LOCATION_SETTING_CITY = "location_setting_city";
        public static final String COLUMN_LOCATION_SETTING_COUNTRY = "location_setting_country";

        // In order to uniquely pinpoint the location on the map when we launch the
        // map intent, we store the latitude and longitude as returned by openweathermap.
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LONG = "coord_long";

        public static Uri buildLocationUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }*/
    }
}
