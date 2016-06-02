package de.hs_mannheim.planb.mobilegrowthmonitor;

import org.junit.Test;
import org.opencv.core.Core;

import de.hs_mannheim.planb.mobilegrowthmonitor.imageprocessing.ImageProcess;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {


    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void sizeIsMeasured() throws Exception{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        ImageProcess ip = new ImageProcess();
        assertEquals(171,ip.sizeMeasurement("C:\\Users\\Yogi\\Desktop\\growpics\\torsten.png"),5);
        assertEquals(171,ip.sizeMeasurement("C:\\Users\\Yogi\\Desktop\\growpics\\ali.png"),5);
        assertEquals(173,ip.sizeMeasurement("C:\\Users\\Yogi\\Desktop\\growpics\\seda.png"),5);
        assertEquals(163,ip.sizeMeasurement("C:\\Users\\Yogi\\Desktop\\growpics\\laura.png"),5);
        assertEquals(172,ip.sizeMeasurement("C:\\Users\\Yogi\\Desktop\\growpics\\jacky.png"),5);

    }
}