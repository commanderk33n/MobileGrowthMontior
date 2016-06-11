package de.hs_mannheim.planb.mobilegrowthmonitor.datahandler;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.androidplot.Plot;
import com.androidplot.util.PixelUtils;
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
import java.util.Random;

import de.hs_mannheim.planb.mobilegrowthmonitor.R;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.DbHelper;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.MeasurementData;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.ProfileData;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.BaseActivity;

public class GraphListView extends BaseActivity {
    private ListView plots;
    DbHelper dbHelper;
    private int profileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_list_view);
        dbHelper = DbHelper.getInstance(getApplicationContext());
        PixelUtils.init(getApplicationContext());
        Bundle extras = getIntent().getExtras();
        profileId = extras.getInt("profile_Id");
        plots = (ListView) findViewById(R.id.lv_plots);
        plots.setAdapter(new MyViewAdapter(getApplicationContext(), R.layout.graph_view, null));

    }

    class MyViewAdapter extends ArrayAdapter<View> {
        private Context context;
        public MyViewAdapter(Context context, int resId, List<View> views) {
            super(context, resId, views);
            this.context = context;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            LayoutInflater inf = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            pos++;
            View v = convertView;
            if (v == null) {
                v = inf.inflate(R.layout.graph_view, parent, false);
            }

            XYPlot mPlot = (XYPlot) v.findViewById(R.id.plot);
            mPlot.clear();
            Filereader f = new Filereader(getApplicationContext());
             PointF minXY;
             PointF maxXY;

            ProfileData profile = dbHelper.getProfile(profileId);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            ArrayList<MeasurementData> measurements = dbHelper.getAllMeasurements(profileId);

            List<Date> dateList = new ArrayList<>();
            List<Double> valueList = new ArrayList<>();

                if (measurements != null && measurements.size() >= 3) {
                    for (MeasurementData md : measurements) {
                        if(pos==1) {

                            double bmi = md.weight / (md.height / 100) / (md.height / 100);
                            bmi = bmi * 100;
                            bmi = bmi - bmi % 10;
                            bmi = bmi / 100;
                            valueList.add(bmi);
                        }else if (pos==2) {
                            valueList.add(md.height);
                        }else if(pos==3) {
                            valueList.add(md.weight);
                        }
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
                    return null;
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

        Log.i("Position","pos"+pos);
            double[][] data = f.giveMeTheData(pos, birthday, profile.sex==1); // type, birthday, gender

            for (Date d : dateList) {

                Calendar calendar = new GregorianCalendar();
                calendar.setTime(d);
                Calendar measuredDay = Calendar.getInstance();
                calendar.setTime(birthday);
                age = measuredDay.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
                age *= 12;
                age += (measuredDay.get(Calendar.MONTH) - calendar.get(Calendar.MONTH));
                if(pos==3 && age>120){
                    sdMinus1.add(0.0);
                    sdPlus1.add(100.0);
                }
                if(age<228){ //19 jahre
                    for(int i = 0;i<data.length;i++){
                        if((int)data[i][0]==age){
                            //  optimal.add(data[i][data[0].length / 2]);
                            sdMinus1.add(data[i][data[0].length / 2-2]);
                            sdPlus1.add(data[i][data[0].length / 2+2]);

                        }
                    }

                }else{
                    if(pos==1){

                    sdMinus1.add(18.5);
                    sdPlus1.add(25.0);
                }
                if(pos==2){
                    age=228;
                    for(int i = 0;i<data.length;i++){
                        if((int)data[i][0]==age){
                            //  optimal.add(data[i][data[0].length / 2]);
                            sdMinus1.add(data[i][data[0].length / 2-2]);
                            sdPlus1.add(data[i][data[0].length / 2+2]);

                        }
                    }
                }


            }}

            final Date[] dateArray = dateList.toArray(new Date[dateList.size()]);
            // final Double[] optimalArray = optimal.toArray(new Double[optimal.size()]);
            // initialize our XYPlot reference:
            //TODO fix zoom and pan
            //  mPlot.setOnTouchListener(this);

                String seriesName="";
                if(pos ==1){
                    seriesName ="BMI";
                }else if(pos==2){
                    seriesName= "Height";
                }else if(pos==3){
                    seriesName= "weight";
                }
            mPlot.setTitle(seriesName);

            XYSeries valueSeries = new SimpleXYSeries(valueList, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, seriesName);
            //  XYSeries optimalSeries = new SimpleXYSeries(optimal, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "SD0");
            XYSeries sdm1Series = new SimpleXYSeries(sdMinus1, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "SD-1");
            XYSeries sdp1Series = new SimpleXYSeries(sdPlus1, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "SD+1");


            // create formatters to use for drawing a series using LineAndPointRenderer
            // and configure them from xml:
            LineAndPointFormatter sd0Format = new LineAndPointFormatter();
            sd0Format.setPointLabelFormatter(new PointLabelFormatter());
            sd0Format.configure(getApplicationContext(),
                    R.xml.line_point_formatter_with_labels);


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
            mPlot.addSeries(valueSeries, bmiFormat);
            // mPlot.addSeries(optimalSeries,sd0Format);
            mPlot.addSeries(sdm1Series,sd1Format);
            mPlot.addSeries(sdp1Series,sd1Format);


            // draw a domain tick for each year:
            mPlot.setDomainStep(XYStepMode.SUBDIVIDE, dateArray.length);
            mPlot.setRangeValueFormat(new DecimalFormat("0"));

          /*  mPlot.setDomainValueFormat(new Format() {

                // create a simple date format that draws on the year portion of our timestamp.
                // see http://download.oracle.com/javase/1.4.2/docs/api/java/text/SimpleDateFormat.html
                // for a full description of SimpleDateFormat.
                private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd");

                @Override
                public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                    int i = ((Number) obj).intValue();
                    Date date = dateArray[i];
                    return dateFormat.format(date, toAppendTo, pos);
                }

                @Override
                public Object parseObject(String source, ParsePosition pos) {
                    return null;

                }
            });
*/
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

            return v;
        }
    }
}
