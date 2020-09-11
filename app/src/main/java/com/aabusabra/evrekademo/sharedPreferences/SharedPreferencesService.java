package com.aabusabra.evrekademo.sharedPreferences;

import android.content.SharedPreferences;


public class SharedPreferencesService {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    public SharedPreferencesService(SharedPreferences preferences) {
        this.preferences = preferences;
        this.editor = preferences.edit();
    }


    public void clearAllSharedPreferences()
    {
        this.editor.clear();
        this.editor.commit();
    }




}
