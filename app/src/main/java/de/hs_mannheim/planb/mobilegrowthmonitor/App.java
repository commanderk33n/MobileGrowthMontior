package de.hs_mannheim.planb.mobilegrowthmonitor;

/**
 * Created by eikood on 04.05.2016.
 */
import android.app.Application;

import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.LockManager;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LockManager.getInstance().enableAppLock(this);
    }

}