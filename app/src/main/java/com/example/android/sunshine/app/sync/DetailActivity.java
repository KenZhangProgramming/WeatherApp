package com.example.android.sunshine.app.sync;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.data.WeatherDbHelper;
import com.example.android.sunshine.app.data.WeatherProvider;

public class DetailActivity extends ActionBarActivity {
    public static final String DATE_KEY = "forecast_date";
    private Context mContext;
    private WeatherDbHelper mOpenHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getBaseContext();
        mOpenHelper = new WeatherDbHelper(mContext);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container,new DetailFragment())
                    .commit();
        }
    }
    /*
        Enable users to enter their feelings in the database
    */

    public void EnterDatabase(View v){
        Intent intent = this.getIntent();
        String forecastDate = intent.getStringExtra(DetailActivity.DATE_KEY);
        EditText mEdit = (EditText)findViewById(R.id.editText);
        String descriptiontext = mEdit.getText().toString();
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String locationcity = Utility.getPreferredLocationCity(this);
        String locationcountry = Utility.getPreferredLocationCountry(this);
        ContentValues inservalues = new ContentValues();
        inservalues.put(WeatherContract.DescriptionEntry.COLUMN_DATE, forecastDate);
        inservalues.put(WeatherContract.DescriptionEntry.COLUMN_DESCRIPTION, descriptiontext);
        inservalues.put(WeatherContract.DescriptionEntry.COLUMN_CITY, locationcity);
        inservalues.put(WeatherContract.DescriptionEntry.COLUMN_COUNTRY, locationcountry);
        db.insert(WeatherContract.DescriptionEntry.TABLE_NAME, null, inservalues);
        Toast.makeText(getApplicationContext(),
                "Your Feeling Has Been Recorded", Toast.LENGTH_LONG).show();
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_feel) {
            startActivity(new Intent(this, DescriptionActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
