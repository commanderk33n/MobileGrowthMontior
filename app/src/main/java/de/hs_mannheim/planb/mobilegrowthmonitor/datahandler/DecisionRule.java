package de.hs_mannheim.planb.mobilegrowthmonitor.datahandler;

import android.content.Context;

import java.util.Date;

import de.hs_mannheim.planb.mobilegrowthmonitor.database.ProfileData;

/**
 * Inteface for Decision Rules
 */
public interface DecisionRule {
    public boolean getDecision(ProfileData profileData,Context context) throws IllegalArgumentException;
}
