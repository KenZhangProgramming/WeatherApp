package com.example.android.sunshine.app.sync;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.example.android.sunshine.app.R;

import java.util.List;
public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }


    @Override
    protected boolean isValidFragment(String fragmentName)
    {
        return LocationPreferenceFragment.class.getName().equals(fragmentName) ||
                UnitsPreferenceFragment.class.getName().equals(fragmentName) ;
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            }
            else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value.
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
    sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
            PreferenceManager
                    .getDefaultSharedPreferences(preference.getContext())
            .getString(preference.getKey(), ""));
}


    /**
     * Location Preferences
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class LocationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_location);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values.
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_locationCity_key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_locationCountry_key)));
        }
    }

    /**
     * Units Preferences
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class UnitsPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_units);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values.
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_units_key)));
        }
    }
}
