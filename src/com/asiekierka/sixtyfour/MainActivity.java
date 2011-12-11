package com.asiekierka.sixtyfour;

import com.asiekierka.sixtyfour.client.*;
import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
	CraftrGame cg;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        cg = new CraftrGame();
    }
}