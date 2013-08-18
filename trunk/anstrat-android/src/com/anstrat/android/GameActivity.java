package com.anstrat.android;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import com.anstrat.core.Main;
import com.badlogic.gdx.backends.android.AndroidApplication;

public class GameActivity extends AndroidApplication {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initialize(Main.getInstance(), false);
        
    }
}