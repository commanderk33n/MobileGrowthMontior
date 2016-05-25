package de.hs_mannheim.planb.mobilegrowthmonitor.database;

import java.io.Serializable;

/**
 * Created by Laura on 23.05.2016.
 */
public class MeasurementData implements Serializable {

    public int index;
    public double weight, size;
    public String image, date;

}
