package de.hs_mannheim.planb.mobilegrowthmonitor.datahandler;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
        Calendar birthday = new GregorianCalendar();
        birthday.setTime(dateOfBirth);
        Calendar today = Calendar.getInstance();
        age = today.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);
        if (today.get(Calendar.MONTH) < birthday.get(Calendar.MONTH)) {
            age--;
        } else if (today.get(Calendar.MONTH) == birthday.get(Calendar.MONTH) &&
                today.get(Calendar.DAY_OF_MONTH) < birthday.get(Calendar.DAY_OF_MONTH)) {
            age--;
        }
        age*=12;
        age +=  (today.get(Calendar.MONTH)-birthday.get(Calendar.MONTH));
        try{
        switch (classification){
            case 1 :
                if(age<3*12){
                    if(!male){
                        table = readBMIScore(R.raw.bmi_girls_0_2_zscores,false);
                    }else {
                        table = readBMIScore(R.raw.bmi_boys_0_2_zcores, false);
                    }


                }else if(age<5*12){
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
                if(age<5*12){
                    if(!male){
                        table = readZScore(R.raw.lhfa_girls_z_exp);
                    }else {
                        table = readZScore(R.raw.lhfa_boys_z_exp);
                    }

                }else{
                    if(!male){
                        table = readZOver5(R.raw.hfa_girls_z_who2007_exp,false);
                    }else {
                        table = readZOver5(R.raw.hfa_boys_z_who2007_exp,false);
                    }
                }
                break;
            case 3 :
                if(age<5*12){
                    if(!male){
                        table = readZScore(R.raw.wfa_girls_z_exp);
                    }else {
                        table = readZScore(R.raw.wfa_boys_z_exp);
                    }

                }else{
                    if(!male){
                        table = readZOver5(R.raw.wfa_girls_z_who2007_exp,true);
                    }else {
                        table = readZOver5(R.raw.wfa_boys_z_who2007_exp,true);
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

        int lines = 3000;
        double[][] values = new double[lines-1][10];

        File outputDir = context.getCacheDir(); // context being the Activity pointer
        final File outputFile = File.createTempFile("temp", "txt", outputDir);
        outputFile.deleteOnExit();
        FileOutputStream out = new FileOutputStream(outputFile) ;
        int len = initialStream.read(buffer);
        int columns = 10;
        while (len != -1) {
            out.write(buffer, 0, len);
            len = initialStream.read(buffer);
        }

        Scanner reader = new Scanner(outputFile);
        reader.useLocale(Locale.US);
        int zeile = 0;
reader.nextLine();
        while (reader.hasNextDouble()) {
            values[zeile / 10][zeile % 10] = reader.nextDouble();
            zeile++;
        }
        return values;
    }

    public  double[][] readBMIScore(int path,boolean over5) throws IOException {
        InputStream initialStream = context.getResources().openRawResource(path);
        byte[] buffer = new byte[8192];
        int n;

        int lines = 3000;
        int columns = over5? 10:8;
        double[][] values = new double[lines-1][columns];
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


    public  double[][] readZOver5(int path, boolean weight ) throws IOException {
        int skip = weight ? 3 : 5;


        InputStream initialStream = context.getResources().openRawResource(path);
        byte[] buffer = new byte[8192];

        int lines = 3000;
        double[][] values = new double[lines-1][10];

        File outputDir = context.getCacheDir(); // context being the Activity pointer
        final File outputFile = File.createTempFile("temp", "txt", outputDir);
        outputFile.deleteOnExit();
        FileOutputStream out = new FileOutputStream(outputFile) ;
        int len = initialStream.read(buffer);
        int columns = 10;
        while (len != -1) {
            out.write(buffer, 0, len);
            len = initialStream.read(buffer);
        }

        Scanner reader = new Scanner(outputFile);
        reader.useLocale(Locale.US);





        int zeile = 0;
        reader.nextLine();
        reader.useLocale(Locale.US);
        int i = 0;
        while (reader.hasNextDouble()) {
            if(i>0&&i<=skip){
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