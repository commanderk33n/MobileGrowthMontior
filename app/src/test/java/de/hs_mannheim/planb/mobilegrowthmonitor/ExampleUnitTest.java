package de.hs_mannheim.planb.mobilegrowthmonitor;

import org.junit.Test;

import java.util.Date;

import de.hs_mannheim.planb.mobilegrowthmonitor.misc.Utils;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {


    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void age_isCorrect() throws Exception{
        assertEquals(23*12, Utils.getAgeInMonths(new Date(1993,06,05),new Date(2016,06,16)));
        assertEquals(21*12+3,Utils.getAgeInMonths(new Date(1995,03,13),new Date(2016,06,16)));
    }
    /*@Test
    public void sizeIsMeasured() throws Exception{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        ImageProcess ip = new ImageProcess();
        assertEquals(171,ip.sizeMeasurement("C:\\Users\\Yogi\\Desktop\\growpics\\torsten.png"),5);
        assertEquals(171,ip.sizeMeasurement("C:\\Users\\Yogi\\Desktop\\growpics\\ali.png"),5);
        assertEquals(173,ip.sizeMeasurement("C:\\Users\\Yogi\\Desktop\\growpics\\seda.png"),5);
        assertEquals(163,ip.sizeMeasurement("C:\\Users\\Yogi\\Desktop\\growpics\\laura.png"),5);
        assertEquals(172,ip.sizeMeasurement("C:\\Users\\Yogi\\Desktop\\growpics\\jacky.png"),5);

    }*/
}