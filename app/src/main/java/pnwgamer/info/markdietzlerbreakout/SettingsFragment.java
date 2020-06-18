package pnwgamer.info.markdietzlerbreakout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public SettingsFragment() {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Preference prefThatWasChanged = findPreference(key);

        switch(key) {
            case "init_brick_count":
                int temp_brick = Integer.parseInt(sharedPreferences.getString(key,""));
                if(temp_brick <= 0) temp_brick = 1;
                if(temp_brick >= 100) temp_brick = 100;
                sharedPreferences.edit().putString(key, Integer.toString(temp_brick)).commit();
                prefThatWasChanged.setSummary("Bricks:" + temp_brick);
                ((EditTextPreference)prefThatWasChanged).setText(Integer.toString(temp_brick));
                break ;
            case "init_brick_hits" :
                int temp_Hits = Integer.parseInt(sharedPreferences.getString(key,""));
                if(temp_Hits < 0) temp_Hits = 1;
                if(temp_Hits > 4) temp_Hits = 4;
                sharedPreferences.edit().putString(key, Integer.toString(temp_Hits)).commit();
                prefThatWasChanged.setSummary("Hits");
                ((EditTextPreference)prefThatWasChanged).setText(Integer.toString(temp_Hits));
                break ;
            case "init_balls_level" :
                int temp_balls = Integer.parseInt(sharedPreferences.getString(key,""));
                if(temp_balls < 1) temp_balls = 1;
                if(temp_balls > 4) temp_balls = 4;
                sharedPreferences.edit().putString(key, Integer.toString(temp_balls)).commit();
                prefThatWasChanged.setSummary("Balls");
                ((EditTextPreference)prefThatWasChanged).setText(Integer.toString(temp_balls));
                break ;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
