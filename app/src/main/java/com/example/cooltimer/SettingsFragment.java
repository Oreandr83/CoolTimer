package com.example.cooltimer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.widget.Toast;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.timer_settings);

        //1)implementation the name of the signal under the  "Timer Melody"
        SharedPreferences sharedPreferences = getPreferenceScreen()
                .getSharedPreferences();

        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int count = preferenceScreen.getPreferenceCount();//counter
        //With the help of the getPreferenceCount get the number of settings that are on the PreferenceScreen

       //here get a specific setting
        for (int i = 0; i < count; i++) {
            Preference preference = preferenceScreen.getPreference(i);

            //3)when it is iteration I'll rule checkbox out ( if this setting not CheckBox)
            if(!(preference instanceof CheckBoxPreference)){
                //include this setting
                String value = sharedPreferences.getString(preference.getKey(), "");
                //set Preference Label (key : value)
                setPreferenceLabel(preference,value);
            }
        }
        Preference preference = findPreference("default_interval");
        //next for <EditTextPreference install PreferenceChangeListener
        preference.setOnPreferenceChangeListener(this);

    }
    //2) set the name of the setting
    private void setPreferenceLabel (Preference preference, String value){//value- this is  <array name="pref_timer_melody_values">
        // <item>bell</item>
     //   <item>alarm_siren</item>
    //    <item>bip</item>
        if(preference instanceof ListPreference){
            ListPreference listPreference =(ListPreference)preference;
            int index = listPreference.findIndexOfValue(value);
            //by index value I get all values from array
            if(index >= 0){
                listPreference.setSummary(listPreference.getEntries()[index]);
            }
        }else if(preference instanceof EditTextPreference){//install value under "Default Interval"
            preference.setSummary(value);//install value by key
        }
    }
     // method to make the music change right away
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key){
        //find setting  by key
        Preference preference =findPreference(key);
        if(!(preference instanceof CheckBoxPreference)){
            String value = sharedPreferences.getString(preference.getKey(),"");
            setPreferenceLabel(preference,value);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {//Remove from registration after the destruction of activity
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
    //keeps an eye on changing one setting
    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {

        Toast toast = Toast.makeText(getContext(),"Please enter an integer number ",Toast.LENGTH_LONG);

     if(preference.getKey().equals("default_interval")){
         String defaultIntervalString = (String)o;

         //trying to recognize this line as a whole number
         try {
             int defaultInterval = Integer.parseInt(defaultIntervalString);
         }catch (NumberFormatException naf){
             toast.show();
             return false;
         }
     }
       //if all ok then true
        return true;
    }
}

