package de.hs_mannheim.planb.mobilegrowthmonitor.datahandler;


import android.os.Bundle;
import android.util.Log;

import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.hs_mannheim.planb.mobilegrowthmonitor.R;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.DbHelper;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.MeasurementData;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.BaseActivity;

public class GraphView extends BaseActivity {

    private static final String TAG = GraphView.class.getSimpleName();

    private XYPlot mPlot;
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

        List<Date> dateList = new ArrayList<>();
        List<Double> bmiList = new ArrayList<>();
        for (MeasurementData md : measurements) {
            bmiList.add(md.height * md.weight);
            System.out.println("Height: " + md.height + "Weight: " + md.weight);
            Calendar calendar = new GregorianCalendar();
            try {
                calendar.setTime(format.parse(md.date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dateList.add(calendar.getTime());
            System.out.println("Timestamp: " +calendar.getTimeInMillis());
        }

        final Date[] dateArray = dateList.toArray(new Date[dateList.size()]);
        // initialize our XYPlot reference:
        mPlot = (XYPlot) findViewById(R.id.plot);

        XYSeries bmiSeries = new SimpleXYSeries(bmiList, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "BMI");

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
        mPlot.addSeries(bmiSeries, bmiFormat);

        // draw a domain tick for each year:
        mPlot.setDomainStep(XYStepMode.SUBDIVIDE, dateArray.length);
        mPlot.setRangeValueFormat(new DecimalFormat("0"));

        mPlot.setDomainValueFormat(new Format() {

            // create a simple date format that draws on the year portion of our timestamp.
            // see http://download.oracle.com/javase/1.4.2/docs/api/java/text/SimpleDateFormat.html
            // for a full description of SimpleDateFormat.
            private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd");

            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = ((Number) obj).intValue();
                Date date = dateArray[i];
                Log.d(TAG, "formatting date=" + new SimpleDateFormat("MMMM dd, yyyy").format(date));
                return dateFormat.format(date, toAppendTo, pos);
            }

            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;

            }
        });




        // reduce the number of range labels
        mPlot.setTicksPerRangeLabel(2);
        mPlot.setTicksPerDomainLabel(2);

        // rotate domain labels 45 degrees to make them more compact horizontally:
        mPlot.getGraphWidget().setDomainLabelOrientation(-45);
        mPlot.getGraphWidget().setRangeTickLabelWidth(50);



    }




}

