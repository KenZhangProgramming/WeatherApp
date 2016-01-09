package com.example.android.sunshine.app.sync;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.data.WeatherDbHelper;

public class DescriptionActivity extends AppCompatActivity {
    private TableRow row;
    private TableLayout descTable;
    private TextView dateView, maxView, minView, descView;
    private WeatherDbHelper mOpenHelper;
    private Context mContext;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        mContext = this.getBaseContext();
        mOpenHelper = new WeatherDbHelper(mContext);
       final SQLiteDatabase mDb = mOpenHelper.getReadableDatabase();

        descTable = (TableLayout) this.findViewById(R.id.mytable);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                BuildTable1(descTable, mDb);
            }
        });



      //  Cursor cursor = db.query(WeatherContract.DescriptionEntry.TABLE_NAME, null, null, null, null, null, null, null);


    }

    private void BuildTable1(TableLayout t, SQLiteDatabase DB) {

            String sql = "SELECT Date, weather_description FROM description";
            Cursor mCur = DB.rawQuery(sql, null);
            if (mCur.getCount() != 0) {
                if (mCur.moveToFirst()) {

                    TableRow rowHeader = new TableRow(this);
                    rowHeader.setLayoutParams(new TableLayout.LayoutParams(
                            TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));

                    TextView tv1 = new TextView(this);
                    tv1.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    tv1.setGravity(Gravity.LEFT);
                    tv1.setTextSize(10);
                    tv1.setText("Date");
                    rowHeader.addView(tv1);

                    TextView tv2 = new TextView(this);
                    tv2.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    tv2.setGravity(Gravity.LEFT);
                    tv2.setTextSize(10);
                    tv2.setText("Description");
                    rowHeader.addView(tv2);
                    t.addView(rowHeader);

                    do {
                    TableRow rows = new TableRow(this);
                    int cols = mCur.getColumnCount();

                        for (int j = 0; j < cols; j++) {
                            TextView tv = new TextView(this);
                            tv.setLayoutParams(new TableRow.LayoutParams(
                                    TableRow.LayoutParams.MATCH_PARENT,
                                    TableRow.LayoutParams.WRAP_CONTENT));
                            tv.setGravity(Gravity.LEFT);
                            tv.setTextSize(10);
                            tv.setText(mCur.getString(j));
                            tv.setPadding(0,0,100,0);
                            rows.addView(tv);

                        }
                        t.addView(rows);
                    } while (mCur.moveToNext());
                }
            }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_description, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
//

}
