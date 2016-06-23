package de.hs_mannheim.planb.mobilegrowthmonitor.datahandler;

import android.content.Context;

import java.util.Date;

import de.hs_mannheim.planb.mobilegrowthmonitor.database.ProfileData;

/**
 * Created by Morty on 12.06.2016.
 */
public interface DecisionRule {
    public boolean getDecision(ProfileData profileData,Context context) throws IllegalArgumentException;
}
