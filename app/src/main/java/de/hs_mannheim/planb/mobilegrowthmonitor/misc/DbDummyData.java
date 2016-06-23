package de.hs_mannheim.planb.mobilegrowthmonitor.misc;

import android.content.Context;

import de.hs_mannheim.planb.mobilegrowthmonitor.database.DbHelper;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.MeasurementData;

/**
 * Created by Morty on 07.06.2016.
 */
public class DbDummyData {
    Context context;
    DbHelper dbHelper = DbHelper.getInstance(context);
    public DbDummyData(Context context){
        this.context = context;
    }
    public void addData(int id){
        MeasurementData data= new MeasurementData();
        for(int i = 1;i<9;i++){
            data.date= "2016-0"+i+"-05 12:00:00";

            data.height = 102+0.6*i;
            data.weight = 15+0.15*i;
            data.index = id;

            dbHelper.addMeasurement(data);
        }
    }

}
