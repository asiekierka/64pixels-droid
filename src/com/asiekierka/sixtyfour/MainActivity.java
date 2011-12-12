package com.asiekierka.sixtyfour;

import com.asiekierka.sixtyfour.client.*;
import android.app.Activity;
import android.os.Bundle;
import android.content.res.*;
import android.content.res.*;

public class MainActivity extends Activity {
	CraftrGame cg;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        cg = new CraftrGame("/sdcard/64pixels",getAssets());
        // TODO 2.2: getExternalFilesDir
    }
}