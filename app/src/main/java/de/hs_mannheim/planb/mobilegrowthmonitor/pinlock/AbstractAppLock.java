package de.hs_mannheim.planb.mobilegrowthmonitor.pinlock;

/**
 * Abstract Class of AppLock
 * for configuration purposes
 * AppLock used from this repo:
 * https://github.com/coderkiss/AppLock
 */

import java.util.HashSet;

public abstract class AbstractAppLock {
    public static final int ENABLE_PASSLOCK = 0;
    public static final int DISABLE_PASSLOCK = 1;
    public static final int CHANGE_PASSWORD = 2;
    public static final int UNLOCK_PASSWORD = 3;

    public static final String MESSAGE = "message";
    public static final String TYPE = "type";

    public static final int DEFAULT_TIMEOUT = 1000; // timeout in milliseconds

    protected int lockTimeOut;
    protected HashSet<String> ignoredActivities;

    /**
     *
     * @param timeout
     */
    public void setTimeout(int timeout) {
        this.lockTimeOut = timeout;
    }

    /**
     * Constructor for AbstractAppLock
     */
    public AbstractAppLock() {
        ignoredActivities = new HashSet<String>();
        lockTimeOut = DEFAULT_TIMEOUT;
    }

    /**
     *
     * @param clazz
     */
    public void addIgnoredActivity(Class<?> clazz) {
        String clazzName = clazz.getName();
        this.ignoredActivities.add(clazzName);
    }

    /**
     *
     * @param clazz
     */
    public void removeIgnoredActivity(Class<?> clazz) {
        String clazzName = clazz.getName();
        this.ignoredActivities.remove(clazzName);
    }

    public abstract void enable();

    public abstract void disable();

    /**
     *
     * @param passcode
     * @return
     */
    public abstract boolean setPasscode(String passcode);

    /**
     *
     * @param passcode
     * @return
     */
    public abstract boolean checkPasscode(String passcode);

    /**
     *
     * @return
     */
    public abstract boolean isPasscodeSet();
}

