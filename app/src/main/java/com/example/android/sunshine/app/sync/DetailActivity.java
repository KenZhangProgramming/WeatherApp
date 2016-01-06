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

import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.data.WeatherDbHelper;
import com.example.android.sunshine.app.data.WeatherProvider;

public class DetailActivity extends ActionBarActivity {
    public static final String DATE_KEY = "forecast_date";
    private Context mContext;
    private WeatherDbHelper mOpenHelper;
    private final String LOG_TAG = DetailActivity.class.getSimpleName();
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
    public void EnterDatabase(View v){
        EditText mEdit   = (EditText)findViewById(R.id.editText);
        String descriptiontext = mEdit.getText().toString();
        Log.v(LOG_TAG, descriptiontext);
        ContentValues inservalues = new ContentValues();
        inservalues.put(WeatherContract.DescriptionEntry.COLUMN_DATE, 0);
        inservalues.put(WeatherContract.DescriptionEntry.COLUMN_DESCRIPTION, descriptiontext);
        Uri locationUri = mContext.getContentResolver().
                insert(WeatherContract.DescriptionEntry.CONTENT_URI, inservalues);
    }*/

    public void EnterDatabase(View v){
        Intent intent = this.getIntent();
        String forecastDate = intent.getStringExtra(DetailActivity.DATE_KEY);
        Log.v(LOG_TAG, forecastDate);
        EditText mEdit = (EditText)findViewById(R.id.editText);
        String descriptiontext = mEdit.getText().toString();
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        ContentValues inservalues = new ContentValues();
        inservalues.put(WeatherContract.DescriptionEntry.COLUMN_DATE, forecastDate);
        inservalues.put(WeatherContract.DescriptionEntry.COLUMN_DESCRIPTION, descriptiontext);
        Log.v(LOG_TAG, descriptiontext);
        db.insert(WeatherContract.DescriptionEntry.TABLE_NAME, null, inservalues);
        Log.v(LOG_TAG, "insert success!");
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
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
