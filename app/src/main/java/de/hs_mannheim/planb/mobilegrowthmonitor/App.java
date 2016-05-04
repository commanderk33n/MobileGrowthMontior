package de.hs_mannheim.planb.mobilegrowthmonitor;

/**
 *  Main Application
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