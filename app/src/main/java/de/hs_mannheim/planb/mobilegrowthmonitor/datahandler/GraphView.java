package de.hs_mannheim.planb.mobilegrowthmonitor.datahandler;


import android.graphics.PointF;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.androidplot.xy.BoundaryMode;
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
import java.util.Timer;
import java.util.TimerTask;

import de.hs_mannheim.planb.mobilegrowthmonitor.R;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.DbHelper;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.MeasurementData;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.ProfileData;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.BaseActivity;

public class GraphView extends BaseActivity implements View.OnTouchListener {

    private static final String TAG = GraphView.class.getSimpleName();

    private XYPlot mPlot;
    private int profileId;
    private DbHelper dbHelper;
    private PointF minXY;
    private PointF maxXY;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        Filereader f = new Filereader(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_view);
        dbHelper = DbHelper.getInstance(this);
        Bundle extras = getIntent().getExtras();
        profileId = extras.getInt("profile_Id");
        ProfileData profile = dbHelper.getProfile(profileId);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        ArrayList<MeasurementData> measurements = dbHelper.getAllMeasurements(profileId);

        List<Date> dateList = new ArrayList<>();
        List<Double> bmiList = new ArrayList<>();


        if (measurements != null && measurements.size() >= 3) {
            for (MeasurementData md : measurements) {

                double bmi = md.weight / (md.height /100) / (md.height/100);
                bmi = bmi*100;
                bmi = bmi-bmi%10;
                bmi = bmi/100;
                bmiList.add(bmi);
                System.out.println("Height: " + md.height + "Weight: " + md.weight);
                Calendar calendar = new GregorianCalendar();
                try {
                    calendar.setTime(format.parse(md.date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dateList.add(calendar.getTime());
                System.out.println("Timestamp: " + calendar.getTimeInMillis());
            }
        } else {
            return;
        }


        /*
        Find the Data with SD0 for the appropriate range
         */
       // List<Double> optimal = new ArrayList<>();
        List<Double> sdMinus1 = new ArrayList<>();
        List<Double> sdPlus1 = new ArrayList<>();
        Date birthday = null;
        try {
            birthday = format.parse(profile.birthday);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int age = 0;

        /*
        If the person is over 19, the values are the same for both sexes & we don't have any data
         */

        double[][] data = f.giveMeTheData(1, birthday, true);

        for (Date d : dateList) {

            Calendar calendar = new GregorianCalendar();
            calendar.setTime(d);
            Calendar measuredDay = Calendar.getInstance();
            calendar.setTime(birthday);
            age = measuredDay.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
            age *= 12;
            age += (measuredDay.get(Calendar.MONTH) - calendar.get(Calendar.MONTH));

            if(age<228){ //19 jahre
            for(int i = 0;i<data.length;i++){
                if((int)data[i][0]==age){
                  //  optimal.add(data[i][data[0].length / 2]);
                    sdMinus1.add(data[i][data[0].length / 2-1]);
                    sdPlus1.add(data[i][data[0].length / 2+1]);

                }
            }

        }else{
                sdMinus1.add(18.5);
                sdPlus1.add(25.0);
            }
        }

        final Date[] dateArray = dateList.toArray(new Date[dateList.size()]);
       // final Double[] optimalArray = optimal.toArray(new Double[optimal.size()]);
        // initialize our XYPlot reference:
        mPlot = (XYPlot) findViewById(R.id.plot);
        //TODO fix zoom and pan
      //  mPlot.setOnTouchListener(this);

        XYSeries bmiSeries = new SimpleXYSeries(bmiList, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "BMI");
      //  XYSeries optimalSeries = new SimpleXYSeries(optimal, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "SD0");
        XYSeries sdm1Series = new SimpleXYSeries(sdMinus1, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "SD-1");
        XYSeries sdp1Series = new SimpleXYSeries(sdPlus1, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "SD+1");


        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
        LineAndPointFormatter sd0Format = new LineAndPointFormatter();
        sd0Format.setPointLabelFormatter(new PointLabelFormatter());
        sd0Format.configure(getApplicationContext(),
                R.xml.line_point_formatter_with_labels);
        Log.i("hallo","ballo");


        // just for fun, add some smoothing to the lines:
        // see: http://androidplot.com/smooth-curves-and-androidplot/
        sd0Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));


        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
        LineAndPointFormatter bmiFormat = new LineAndPointFormatter();
        bmiFormat.setPointLabelFormatter(new PointLabelFormatter());
        bmiFormat.configure(getApplicationContext(),
                R.xml.line_point_formatter_with_labels3);


        // just for fun, add some smoothing to the lines:
        // see: http://androidplot.com/smooth-curves-and-androidplot/
        bmiFormat.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        LineAndPointFormatter sd1Format = new LineAndPointFormatter();

        sd1Format.setPointLabelFormatter(new PointLabelFormatter());

        sd1Format.configure(getApplicationContext(),
                R.xml.line_point_formatter_with_labels_2);
        //sd1Format.setPointLabelFormatter(null);
        sd1Format.setPointLabeler(null);
        // just for fun, add some smoothing to the lines:
        // see: http://androidplot.com/smooth-curves-and-androidplot/
        sd1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        // add a new series' to the xyplot:
        mPlot.addSeries(bmiSeries, bmiFormat);
     // mPlot.addSeries(optimalSeries,sd0Format);
        mPlot.addSeries(sdm1Series,sd1Format);
        mPlot.addSeries(sdp1Series,sd1Format);


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
        mPlot.setTicksPerRangeLabel(1);
        mPlot.setTicksPerDomainLabel(1);

        // rotate domain labels 45 degrees to make them more compact horizontally:
        mPlot.getGraphWidget().setDomainLabelOrientation(-45);
       mPlot.getGraphWidget().setRangeTickLabelWidth(25);
      mPlot.getGraphWidget().setDomainGridLinePaint(null);

        mPlot.redraw();

        //Set of internal variables for keeping track of the boundaries
        mPlot.calculateMinMaxVals();
        minXY=new PointF(mPlot.getCalculatedMinX().floatValue(),mPlot.getCalculatedMinY().floatValue());
        maxXY=new PointF(mPlot.getCalculatedMaxX().floatValue(),mPlot.getCalculatedMaxY().floatValue());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus){
            dbHelper.close();

        }
    }

    // Definition of the touch states
    static final int NONE = 0;
    static final int ONE_FINGER_DRAG = 1;
    static final int TWO_FINGERS_DRAG = 2;
    int mode = NONE;

    PointF firstFinger;
    float lastScrolling;
    float distBetweenFingers;
    float lastZooming;

    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: // Start gesture
                firstFinger = new PointF(event.getX(), event.getY());
                mode = ONE_FINGER_DRAG;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                //When the gesture ends, a thread is created to give inertia to the scrolling and zoom
                Timer t = new Timer();
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        while(Math.abs(lastScrolling)>1f || Math.abs(lastZooming-1)<1.01){
                            lastScrolling*=.8;
                            scroll(lastScrolling);
                            lastZooming+=(1-lastZooming)*.2;
                            zoom(lastZooming);
                            mPlot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.AUTO);
                            mPlot.redraw();
                            // the thread lives until the scrolling and zooming are imperceptible
                        }
                    }
                }, 0);

            case MotionEvent.ACTION_POINTER_DOWN: // second finger
                distBetweenFingers = spacing(event);
                // the distance check is done to avoid false alarms
                if (distBetweenFingers > 5f) {
                    mode = TWO_FINGERS_DRAG;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == ONE_FINGER_DRAG) {
                    PointF oldFirstFinger=firstFinger;
                    firstFinger=new PointF(event.getX(), event.getY());
                    lastScrolling=oldFirstFinger.x-firstFinger.x;
                    scroll(lastScrolling);
                    lastZooming=(firstFinger.y-oldFirstFinger.y)/mPlot.getHeight();
                    if (lastZooming<0)
                        lastZooming=1/(1-lastZooming);
                    else
                        lastZooming+=1;
                    zoom(lastZooming);
                    mPlot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.AUTO);
                    mPlot.redraw();

                } else if (mode == TWO_FINGERS_DRAG) {
                    float oldDist =distBetweenFingers;
                    distBetweenFingers=spacing(event);
                    lastZooming=oldDist/distBetweenFingers;
                    zoom(lastZooming);
                    mPlot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.AUTO);
                    mPlot.redraw();
                }
                break;
        }
        return true;
    }

    private void zoom(float scale) {
        float domainSpan = maxXY.x	- minXY.x;
        float domainMidPoint = maxXY.x		- domainSpan / 2.0f;
        float offset = domainSpan * scale / 2.0f;
        minXY.x=domainMidPoint- offset;
        maxXY.x=domainMidPoint+offset;
    }

    private void scroll(float pan) {
        float domainSpan = maxXY.x	- minXY.x;
        float step = domainSpan / mPlot.getWidth();
        float offset = pan * step;
        minXY.x+= offset;
        maxXY.x+= offset;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }
}

