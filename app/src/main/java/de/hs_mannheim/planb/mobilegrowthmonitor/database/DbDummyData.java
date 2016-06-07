package de.hs_mannheim.planb.mobilegrowthmonitor.database;

import android.content.Context;

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
        for(int i = 0;i<6;i++){
            data.date= "2016-06-0"+i+" 12:00:00";

            data.height = 1.30;
            data.weight = 30-i;
            data.index = id;

            dbHelper.addMeasurement(data);
        }
    }

}
