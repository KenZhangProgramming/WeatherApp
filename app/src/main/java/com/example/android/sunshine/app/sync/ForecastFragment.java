package com.example.android.sunshine.app.sync;

import android.app.Activity;
import android.content.Intent;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.data.WeatherContract.WeatherEntry;
import com.example.android.sunshine.app.data.WeatherContract.LocationEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by kenzhang on 15-10-08.
 */
public class ForecastFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
    private ForecastAdapter mForecastAdapter;
    private static final int FORECAST_LOADER = 0;
    private String mLocationcity;
    private String mLocationcountry;
    // Specify the columns we need.
    private static final String[] FORECAST_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_ICON_ID,
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_ICON_ID = 5;

    public ForecastFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //Add the line in order for the fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
           updateWeather();
            Intent intent = new Intent(this.getContext(), MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // The ArrayAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.fragment_main,container,false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = mForecastAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .putExtra(DetailActivity.DATE_KEY, cursor.getString(COL_WEATHER_DATE));
                    startActivity(intent);
                }
            }
        });
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void updateWeather() {
        String locationcity = Utility.getPreferredLocationCity(getActivity());
        String locationcountry = Utility.getPreferredLocationCountry(getActivity());
        new FetchWeatherTask(getActivity()).execute(locationcity, locationcountry);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLocationcity != null
                && mLocationcountry!=null
                && mLocationcity.equals(Utility.getPreferredLocationCity(getActivity()))
                && mLocationcountry.equals(Utility.getPreferredLocationCountry(getActivity()))) {
            getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
        }
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args){

        String startDate = WeatherContract.getDbDateString(new Date());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";

        mLocationcity = Utility.getPreferredLocationCity(getActivity());
        mLocationcountry = Utility.getPreferredLocationCountry(getActivity());
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                mLocationcity, mLocationcountry,startDate);

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );

    }
    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }
}

