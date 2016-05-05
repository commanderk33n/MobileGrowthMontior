package de.hs_mannheim.planb.mobilegrowthmonitor.pinlock;

/**
 * LockManager singleton
 * enables/disables Pinlock
 *
 */
import android.app.Application;

public class LockManager {

    private volatile static LockManager instance;
    private AbstractAppLock curAbstractAppLocker;

    public static LockManager getInstance() {
        synchronized (LockManager.class) {
            if (instance == null) {
                instance = new LockManager();
            }
        }
        return instance;
    }

    /**
     *
     * @param app
     */
    public void enableAppLock(Application app) {
        if (curAbstractAppLocker == null) {
            curAbstractAppLocker = new AppLock(app);
        }
        curAbstractAppLocker.enable();
    }

    /**
     *
     * @return
     */
    public boolean isAppLockEnabled() {
        if (curAbstractAppLocker == null) {
            return false;
        } else {
            return true;
        }
    }

    public void setAppLock(AbstractAppLock abstractAppLocker) {
        if (curAbstractAppLocker != null) {
            curAbstractAppLocker.disable();
        }
        curAbstractAppLocker = abstractAppLocker;
    }

    public AbstractAppLock getAppLock() {
        return curAbstractAppLocker;
    }
}
