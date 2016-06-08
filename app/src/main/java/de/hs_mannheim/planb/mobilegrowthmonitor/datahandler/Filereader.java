package de.hs_mannheim.planb.mobilegrowthmonitor.datahandler;

import android.content.Context;
import android.util.Log;

import java.io.*;
import java.lang.reflect.Field;
import java.net.CookieHandler;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Scanner;

import de.hs_mannheim.planb.mobilegrowthmonitor.R;

/**
 * Created by Morty on 25.05.2016.
 */
public class Filereader {
    private static  final String[] filenames = {

            "bmi_boys_0_2_zcores.txt",
            "bmi_girls_0_2_zscores.txt",

            "bmi_girls_2_5_zscores.txt",
            "bmi_boys_2_5_zscores.txt",

            "bmi_boys_z_who2007_exp.txt",
            "bmi_girls_z_who2007_exp.txt",

            "lhfa_boys_z_exp.txt",
            "lhfa_girls_z_exp.txt",


            "hfa_boys_z_who2007_exp.txt",
            "hfa_girls_z_who2007_exp.txt",


            "wfa_boys_z_exp.txt",
            "wfa_girls_z_exp.txt",

            "wfa_boys_z_who2007_exp.txt",
            "wfa_girls_z_who2007_exp.txt"
    };
   private Context context;
    public Filereader(Context context){
        this.context = context;
    }

    /**
     *
     * @param classification 1 = bmi, 2 = height , 3  = weight
     * @param dateOfBirth to see which file to read
     * @param male
     * @return
     */
    public  double[][] giveMeTheData(int classification, Date dateOfBirth, boolean male) throws IllegalArgumentException {
        int file = 0;
        int age=0;
        double[][] table=null;
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(dateOfBirth);
        Calendar today = Calendar.getInstance();
        age = today.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
        if (today.get(Calendar.MONTH) < calendar.get(Calendar.MONTH)) {
            age--;
        } else if (today.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                today.get(Calendar.DAY_OF_MONTH) < calendar.get(Calendar.DAY_OF_MONTH)) {
            age--;
        }
        try{
        switch (classification){
            case 1 :
                if(age<3){
                    if(!male){
                        table = readBMIScore(R.raw.bmi_girls_0_2_zscores,false);
                    }else {
                        table = readBMIScore(R.raw.bmi_boys_0_2_zcores, false);
                    }


                }else if(age<5){
                    if(!male){
                        table = readBMIScore(R.raw.bmi_girls_2_5_zscores,false);
                    }else {
                        table = readBMIScore(R.raw.bmi_boys_2_5_zscores, false);
                    }


                }else {
                    if(!male){
                        table = readBMIScore(R.raw.bmi_girls_z_who2007_exp,true);
                    }else {
                        table = readBMIScore(R.raw.bmi_boys_z_who2007_exp, true);
                    }
                }

                break;
            case 2 :
                if(age<5){
                    if(!male){
                        table = readZScore(R.raw.lhfa_girls_z_exp);
                    }else {
                        table = readZScore(R.raw.lhfa_boys_z_exp);
                    }

                }else{
                    if(!male){
                        table = readZOver5(R.raw.hfa_girls_z_who2007_exp);
                    }else {
                        table = readZOver5(R.raw.hfa_boys_z_who2007_exp);
                    }
                }
                break;
            case 3 :
                if(age<5){
                    if(!male){
                        table = readZScore(R.raw.wfa_girls_z_exp);
                    }else {
                        table = readZScore(R.raw.wfa_boys_z_exp);
                    }

                }else{
                    if(!male){
                        table = readZOver5(R.raw.wfa_girls_z_who2007_exp);
                    }else {
                        table = readZOver5(R.raw.wfa_boys_z_who2007_exp);
                    }
                }
                break;
            default: throw new IllegalArgumentException("falsche angabe, keine Daten verfügbar");
        }}catch(IOException e){

        }



        return table;

    }

    /**
     * reads a File and returns the values sorted in the known order of:
     * Age SD4neg	SD3neg	SD2neg	SD1neg	SD0	SD1	SD2	SD3	SD4
     * the reader has to handle whether the age is in months or days
     * TODO: decide wether to have a path or a file as a parameter
     *
     * @param path
     * @return the sorted array of values
     * @throws IOException
     */
    public  double[][] readZScore( int path) throws IOException {




        InputStream initialStream = context.getResources().openRawResource(path);
        byte[] buffer = new byte[8192];
        int count = 0;
        int n;
        while ((n = initialStream.read(buffer)) > 0) {
            for (int i = 0; i < n; i++) {
                if (buffer[i] == '\n') count++;
            }
        }
        initialStream.close();


       // LineNumberReader lnr = new LineNumberReader(new FileReader(new File(path))); // to know the dimensions of the double array
        //lnr.skip(Long.MAX_VALUE);
        int lines = count;
        double[][] values = new double[lines-1][10];
     //   lnr.close();
        //File file = new File(path);
        Scanner reader = new Scanner(initialStream);
        int zeile = 0;
        reader.nextLine();
        reader.useLocale(Locale.US);

        while (reader.hasNextDouble()) {
            values[zeile / 10][zeile % 10] = reader.nextDouble();
            zeile++;
        }
        return values;
    }

    public  double[][] readBMIScore(int path,boolean over5) throws IOException {
        InputStream initialStream = context.getResources().openRawResource(path);
        byte[] buffer = new byte[8192];
        int count = 0;
        int n;
      /*  while ((n = initialStream.read(buffer)) > 0) {
            for (int i = 0; i < n; i++) {
                if (buffer[i] == '\n') count++;
            }
        }
*/

        // LineNumberReader lnr = new LineNumberReader(new FileReader(new File(path))); // to know the dimensions of the double array
        //lnr.skip(Long.MAX_VALUE);
        int lines = 3000;
        int columns = over5? 10:8;
        double[][] values = new double[lines-1][10];
       // File file = new File(path);

        File outputDir = context.getCacheDir(); // context being the Activity pointer
        final File outputFile = File.createTempFile("temp", "txt", outputDir);
        outputFile.deleteOnExit();
        FileOutputStream out = new FileOutputStream(outputFile) ;
        int len = initialStream.read(buffer);

        while (len != -1) {
            out.write(buffer, 0, len);
            len = initialStream.read(buffer);
        }

        Scanner reader = new Scanner(outputFile);

        int zeile = 0;
        reader.nextLine();
        reader.useLocale(Locale.US);
        int i = 0;
        while (reader.hasNextDouble()) {
            if(i>0&&i<4){
                i++;
                reader.nextDouble();
                continue;
            }
            values[zeile / columns][zeile % columns] = reader.nextDouble();
            zeile++;
            i++;

            if(zeile%columns==0){
                i=0;
            }

        }        initialStream.close();


        return values;
    }


    public  double[][] readZOver5(int path ) throws IOException {
        InputStream initialStream = context.getResources().openRawResource(path);
        byte[] buffer = new byte[8192];
        int count = 0;
        int n;
        while ((n = initialStream.read(buffer)) > 0) {
            for (int i = 0; i < n; i++) {
                if (buffer[i] == '\n') count++;
            }
        }
        initialStream.close();


        // LineNumberReader lnr = new LineNumberReader(new FileReader(new File(path))); // to know the dimensions of the double array
        //lnr.skip(Long.MAX_VALUE);
        int lines = count;
        int columns = 10;
        double[][] values = new double[lines-1][columns];
        Scanner reader = new Scanner(initialStream);
        int zeile = 0;
        reader.nextLine();
        reader.useLocale(Locale.US);
        int i = 0;
        while (reader.hasNextDouble()) {
            if(i>0&&i<6){
                i++;
                reader.nextDouble();
                continue;
            }
            values[zeile / columns][zeile % columns] = reader.nextDouble();
            zeile++;
            i++;

            if(zeile%columns==0){
                i=0;
            }

        }
        return values;
    }



    /**
     * takes an array that the Filereader read as input, takes the height (or weight)
     * as input
     * and tells you the Standarddeviation
     * @param ageArray
     * @param height
     * @return
     */
    public int deviation(double[] ageArray, double height) throws IllegalArgumentException {

        for (int i = 0; i < ageArray.length; i++) {
            if (ageArray[i + 1] > height || ageArray[ageArray.length - i-1] < height) {
                int sign = height < ageArray[i + 1] ? -1 : 1; //positive or negative deviation
                return (5 - i) * sign;
            }
        }
        throw new IllegalArgumentException("no group found");
    }
}