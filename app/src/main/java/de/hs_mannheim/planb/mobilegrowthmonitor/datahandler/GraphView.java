package de.hs_mannheim.planb.mobilegrowthmonitor.datahandler;

import android.graphics.DashPathEffect;
import android.os.Bundle;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import de.hs_mannheim.planb.mobilegrowthmonitor.R;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.DbHelper;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.MeasurementData;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.BaseActivity;

public class GraphView extends BaseActivity {

    private XYPlot plot;
    private int profileId;
    private DbHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_view);
        dbHelper = DbHelper.getInstance(this);
        Bundle extras = getIntent().getExtras();
        profileId = extras.getInt("profile_Id");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        ArrayList<MeasurementData> measurements = dbHelper.getAllMeasurements(profileId);

        List<Double> bmi = new ArrayList<>();
        for (MeasurementData md : measurements) {
            bmi.add(md.height * md.weight);

            /*
            Calendar date = new GregorianCalendar();
            try {
                date.setTime(format.parse(md.date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long l = date.getTimeInMillis();
            bmi.add(Double.longBitsToDouble(l));
            System.out.println("TimeStamp: " + l);
            */

        }

        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.plot);


        // turn the above arrays into XYSeries':

        XYSeries bmiSeries = new SimpleXYSeries(bmi,
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "BMI");


        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
        LineAndPointFormatter bmiFormat = new LineAndPointFormatter();
        bmiFormat.setPointLabelFormatter(new PointLabelFormatter());
        bmiFormat.configure(getApplicationContext(),
                R.xml.line_point_formatter_with_labels);



        // just for fun, add some smoothing to the lines:
        // see: http://androidplot.com/smooth-curves-and-androidplot/
        bmiFormat.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));


        // add a new series' to the xyplot:
        plot.addSeries(bmiSeries, bmiFormat);

 
        // reduce the number of range labels
        plot.setTicksPerRangeLabel(3);

        // rotate domain labels 45 degrees to make them more compact horizontally:
        plot.getGraphWidget().setDomainLabelOrientation(-45);

    }
}