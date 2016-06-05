package de.hs_mannheim.planb.mobilegrowthmonitor.datahandler;

import java.io.*;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by Morty on 25.05.2016.
 */
public class Filereader {

    public static void main(String[] args) {
        try {
            double[][] height = readZScore("C:\\Users\\Yogi\\Desktop\\who\\height-age\\lhfa_boys_z_exp.txt");
            for (int i = 0; i < height.length; i++) {
                System.out.println(height[i][0]);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public static double[][] readZScore(String path) throws IOException {
        LineNumberReader lnr = new LineNumberReader(new FileReader(new File(path))); // to know the dimensions of the double array
        lnr.skip(Long.MAX_VALUE);
        long lines = lnr.getLineNumber();
        double[][] values = new double[lnr.getLineNumber()][10];
        lnr.close();
        File file = new File(path);
        Scanner reader = new Scanner(file);
        //  int spalte = 0;
        int zeile = 0;
        reader.nextLine();
        reader.useLocale(Locale.US);

        while (reader.hasNextDouble()) {
            values[zeile / 10][zeile % 10] = reader.nextDouble();
            zeile++;
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