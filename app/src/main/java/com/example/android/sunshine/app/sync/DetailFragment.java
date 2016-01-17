package com.example.android.sunshine.app.sync;

import android.app.LoaderManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.data.WeatherContract;

/**
 * Created by kenzhang on 15-12-07.
 */
public class DetailFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
        public static final String LOCATION_KEY_CITY = "city";
        public static final String LOCATION_KEY_COUNTRY = "country";
        private static final String LOG_TAG = DetailFragment.class.getSimpleName();
        public static final String LOCATION_KEY = "location";
        private String mLocationcity;
        private String mLocationcountry;
        private static final int DETAIL_LOADER = 0;

        private static final String[] DETAIL_COLUMNS = {
                WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
                WeatherContract.WeatherEntry.COLUMN_DATE,
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
                WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
                WeatherContract.WeatherEntry.COLUMN_PRESSURE,
                WeatherContract.WeatherEntry.COLUMN_ICON_ID,
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING_CITY,
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING_COUNTRY
        };

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_HUMIDITY = 5;
    public static final int COL_WEATHER_PRESSURE = 6;
    public static final int COL_WEATHER_CONDITION_ID = 7;
    public static final int COL_WEATHER_LOCATION_CITY = 10;
    public static final int COL_WEATHER_LOCATION_COUNTRY = 11;

    private ImageView mIconView;
    private TextView mDateView;
    private TextView mDescriptionView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mHumidityView;
    private TextView mPressureView;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            outState.putString(LOCATION_KEY_CITY, mLocationcity);
            outState.putString(LOCATION_KEY_COUNTRY, mLocationcountry);
            super.onSaveInstanceState(outState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                  Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            mIconView = (ImageView) rootView.findViewById(R.id.detail_icon);
            mDateView = (TextView) rootView.findViewById(R.id.detail_date_textview);
            mDescriptionView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
            mHighTempView = (TextView) rootView.findViewById(R.id.detail_high_textview);
            mLowTempView = (TextView) rootView.findViewById(R.id.detail_low_textview);
            mHumidityView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
            mPressureView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);
            return rootView;
         }

        @Override
        public void onResume() {
            super.onResume();
            if (mLocationcity != null &&
                    mLocationcountry != null &&
                    !mLocationcity.equals(Utility.getPreferredLocationCity(getActivity())) &&
                    !mLocationcity.equals(Utility.getPreferredLocationCity(getActivity()))) {
                getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
            }
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            if (savedInstanceState != null) {
                mLocationcity = savedInstanceState.getString(LOCATION_KEY_CITY);
                mLocationcountry = savedInstanceState.getString(LOCATION_KEY_COUNTRY);
            }
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.v(LOG_TAG, "In onCreateLoader");
            Intent intent = getActivity().getIntent();
            if (intent == null || !intent.hasExtra(DetailActivity.DATE_KEY)) {
                return null;
            }
            //Get the forecasatDate from the previous screen
            String forecastDate = intent.getStringExtra(DetailActivity.DATE_KEY);

            // Sort order:  Ascending, by date.
            String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";

            mLocationcity = Utility.getPreferredLocationCity(getActivity());
            mLocationcountry = Utility.getPreferredLocationCountry(getActivity());
            Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                    mLocationcity, mLocationcountry, forecastDate);
            Log.v(LOG_TAG, weatherForLocationUri.toString());

            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    weatherForLocationUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    sortOrder
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            Log.v(LOG_TAG, "In onLoadFinished");

            // why u need data.moveToFirst to be false?!!!
            if (data != null && data.moveToFirst()) {

                String iconId = data.getString(COL_WEATHER_CONDITION_ID);
                mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(iconId));

                String dateString = data.getString(COL_WEATHER_DATE);
                dateString = Utility.changeDateFormat(dateString);
                mDateView.setText(dateString);

                String weatherDescription = data.getString(COL_WEATHER_DESC);
                mDescriptionView.setText(weatherDescription);

                boolean isMetric = Utility.isMetric(getActivity());

                String high = Utility.formatTemperature(
                        data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
                mHighTempView.setText("Max: " + high);


                String low = Utility.formatTemperature(
                       data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
                mLowTempView.setText("Min: " + low);

                // Read humidity from cursor and update view
                float humidity = data.getFloat(COL_WEATHER_HUMIDITY);
                mHumidityView.setText(getActivity().getString(R.string.format_humidity, humidity));

                // Read pressure from cursor and update view
                float pressure = data.getFloat(COL_WEATHER_PRESSURE);
                mPressureView.setText(getActivity().getString(R.string.format_pressure, pressure));
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }








