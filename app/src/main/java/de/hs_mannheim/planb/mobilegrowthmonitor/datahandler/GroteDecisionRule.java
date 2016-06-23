package de.hs_mannheim.planb.mobilegrowthmonitor.datahandler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import de.hs_mannheim.planb.mobilegrowthmonitor.R;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.DbHelper;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.MeasurementData;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.ProfileData;
import de.hs_mannheim.planb.mobilegrowthmonitor.misc.Utils;

/**
 * Gives A result on user input after taking input from user
 */
public class GroteDecisionRule implements DecisionRule {
    private static final String PREFS_NAME = "Values";
    double sdMinus2=0;
    double sdMinus3 = 0;
    double sdMinus25=0;
    SharedPreferences preferences ;
    @Override
    public boolean getDecision(ProfileData profileData, final Context context) throws IllegalArgumentException {
        preferences =  context.getSharedPreferences(PREFS_NAME, 0);
        Date birthdate = Utils.getDate(profileData.birthday);

        int age = Utils.getAgeInMonths(birthdate,null);
        int ageTemp = age;
        DbHelper dbHelper = DbHelper.getInstance(context);
        ArrayList<MeasurementData> measurements = dbHelper.getAllMeasurements(profileData.index);
        final double thisHeight = measurements.get(measurements.size()-1).height;
        if(measurements.size()<2){
            throw new IllegalArgumentException(context.getString(R.string.cdr_not_enough_measurements));
        }
        final MeasurementData thisMeasurement = measurements.get(measurements.size()-1);
        final double lastHeight =  measurements.get(measurements.size()-2).height;
        final MeasurementData lastMeasurement = measurements.get(measurements.size()-2);
        double[][] data = new Filereader(context).giveMeTheData(2,birthdate,profileData.sex==1,null);
        final  double [] gestationalAge = new double[1];
        final boolean[] result = new boolean[1];

        if (age > 120) {
            throw new IllegalArgumentException(context.getString(R.string.cdr_too_old));
        } else if (age <= 60) {
            age *= 30;
        }

        for (int i = 0; i < data.length; i++) {
            if ((int) data[i][0] == age) {

                sdMinus2 = (data[i][data[0].length / 2 - 2]);

                sdMinus3 = (data[i][data[0].length / 2 - 3]);
                sdMinus25 = (sdMinus2 + sdMinus3) / 2;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putFloat("sdMinus2", (float)sdMinus2);
                editor.putFloat("sdMinus3", (float)sdMinus3);
                editor.putFloat("sdMinus25", (float)sdMinus25);

                editor.commit();
                break;

            }}
    Log.i("sdminus25","ist "+sdMinus25);

       if(ageTemp>36){
           if(thisHeight<sdMinus25){
               Toast.makeText(context,R.string.cdr_diagnostic, Toast.LENGTH_LONG).show();

               return true;
           }
           if(thisHeight>sdMinus2){
               Toast.makeText(context, R.string.cdr_no_further, Toast.LENGTH_LONG).show();

               return false;

           }
           final LayoutInflater inflater = LayoutInflater.from(context);
           final View view = inflater.inflate(R.layout.decision_rules,null,false);

           final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
           alertDialog.setMessage(R.string.cdr_birthweight_length_gestation );
           alertDialog.setView(view);
           alertDialog.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   result[0]=true;
                   Toast.makeText(context, R.string.cdr_diagnostic, Toast.LENGTH_LONG).show();

                   return;
                   }

       });




           alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {

                   final View view2 = inflater.inflate(R.layout.decision_rules,null,false);

                   final AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(context);
                   alertDialog2.setMessage(R.string.cdr_disproportion );

                   alertDialog2.setView(view2);
                   alertDialog2.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           result[0]=true;
                           Toast.makeText(context, R.string.cdr_diagnostic, Toast.LENGTH_LONG).show();

                           return;
                       }

                   });
                   alertDialog2.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           final View view3 = inflater.inflate(R.layout.decision_rules,null,false);

                           AlertDialog.Builder alertDialog3 = new AlertDialog.Builder(context);
                           alertDialog3.setMessage(R.string.cdr_distance_target_height );
                           alertDialog3.setView(view3);
                           alertDialog3.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {
                                   result[0]=true;
                                   Toast.makeText(context, R.string.cdr_diagnostic, Toast.LENGTH_LONG).show();

                                   return;
                               }

                           });
                           alertDialog3.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {
                                   final View view4 = inflater.inflate(R.layout.decision_rules,null,false);

                                   final AlertDialog.Builder alertDialog4 = new AlertDialog.Builder(context);
                                   alertDialog4.setMessage("Absolute height deflection less than –1 SD" );
                                   alertDialog4.setView(view4);
                                   alertDialog4.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {
                                           result[0]=true;
                                           Toast.makeText(context, R.string.cdr_diagnostic, Toast.LENGTH_LONG).show();

                                           return;

                                       }

                                   });
                                   alertDialog4.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {
                                           result[0]=false;
                                           Toast.makeText(context, R.string.cdr_no_further, Toast.LENGTH_LONG).show();

                                           return;

                                       }

                                   });

                                   AlertDialog alert4 = alertDialog4.create();
                                   alert4.show();
                               }

                           });
                           AlertDialog alert3 = alertDialog3.create();
                           alert3.show();

                       }

                   });
                   AlertDialog alert2 = alertDialog2.create();
                   alert2.show();



               }

           });
           AlertDialog alert = alertDialog.create();
           alert.show();
           if(result[0]==true){
               Toast.makeText(context, R.string.cdr_diagnostic, Toast.LENGTH_LONG).show();

               return  true;
           }




       }else {
           LayoutInflater inflaterb = LayoutInflater.from(context);
           View viewb = inflaterb.inflate(R.layout.measurement_dialog, null);

           AlertDialog.Builder alertDialogb = new AlertDialog.Builder(context);
           alertDialogb.setMessage(R.string.cdr_birthweight);
           alertDialogb.setView(viewb);
           final EditText birthweightEt = (EditText) viewb.findViewById(R.id.ed_dialog);
           final double[] birthweight = new double[1];

           alertDialogb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
               try {
                   birthweight[0] = Double.parseDouble(birthweightEt.getText().toString());
               } catch (Exception e) {
                   birthweight[0] = 20;
               }


                   if ( birthweight[0]>=2.5 ) {
                       LayoutInflater inflater = LayoutInflater.from(context);
                       View view = inflater.inflate(R.layout.measurement_dialog, null);

                       AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                       alertDialog.setMessage("enter gestational age");
                       alertDialog.setView(view);
                       final EditText gestationalAgeEt = (EditText) view.findViewById(R.id.ed_dialog);
                       final double finalSdMinus = preferences.getFloat("sdMinus2",0);
                       final double finalSdMinus2 = preferences.getFloat("sdMinus25",0);
                       alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               //dismisses Dialog automatically
                               try {
                                   gestationalAge[0] = Double.parseDouble(gestationalAgeEt.getText().toString());
                               } catch (Exception e) {
                                    gestationalAge[0] = 20;
                               }finally{


                                   if(gestationalAge[0]>37 &&thisHeight < preferences.getFloat("sdMinus2",0)){
                                       result[0] = true;
                                       Toast.makeText(context, R.string.cdr_diagnostic, Toast.LENGTH_LONG).show();

                                       return;

                                   }else if(gestationalAge[0]>37){
                                       Date measureThis = Utils.getDate(thisMeasurement.date);
                                       Date measureLast = Utils.getDate(lastMeasurement.date);
                                       int delta = Utils.getAgeInMonths(measureLast,measureThis);
                                        if(delta>5&& delta<12){
                                            if(lastHeight<  preferences.getFloat("sdMinus25",0) &&thisHeight< preferences.getFloat("sdMinus25",0)){
                                                result[0]=true;
                                                Toast.makeText(context, R.string.cdr_diagnostic, Toast.LENGTH_LONG).show();

                                            }else{
                                                Toast.makeText(context, R.string.cdr_no_further, Toast.LENGTH_LONG).show();

                                            }
                                        }else{
                                            result[0]=false;
                                            Toast.makeText(context, R.string.cdr_no_further, Toast.LENGTH_LONG).show();



                                        }
                                       return;
                                   }else{
                                       result[0]=false;
                                       Toast.makeText(context, R.string.cdr_no_further, Toast.LENGTH_LONG).show();



                                       return;
                                   }
                               }



                           }
                           });
                       alertDialog.show();


                   }else{
                       result[0]=false;
                       Toast.makeText(context, R.string.cdr_no_further, Toast.LENGTH_LONG).show();



                       return;
                   }
               }
           });
           alertDialogb.show();
               }


        return false;
    }
}
