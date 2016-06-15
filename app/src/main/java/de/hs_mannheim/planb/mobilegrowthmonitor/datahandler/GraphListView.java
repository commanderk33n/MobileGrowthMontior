package de.hs_mannheim.planb.mobilegrowthmonitor.datahandler;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import de.hs_mannheim.planb.mobilegrowthmonitor.misc.Utils;
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
            //   pos++;
            View v = convertView;
            if (v == null) {
                v = inf.inflate(R.layout.graph_view, parent, false);
            }else{
                return v;
            }

            XYPlot mPlot = (XYPlot) v.findViewById(R.id.plot);
            mPlot.clear();
            Filereader f = new Filereader(getApplicationContext());
            //    PointF minXY;
            //  PointF maxXY;

            ProfileData profile = dbHelper.getProfile(profileId);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            ArrayList<MeasurementData> measurements = dbHelper.getAllMeasurements(profileId);

            List<Date> dateList = new ArrayList<>();
            List<Double> valueList = new ArrayList<>();

            if (measurements != null && measurements.size() >= 3) {
                for (MeasurementData md : measurements) {
                    if (pos == 0) {

                        double bmi = md.weight / (md.height / 100) / (md.height / 100);
                        bmi = bmi * 100;
                        bmi = bmi - bmi % 10;
                        bmi = bmi / 100;
                        valueList.add(bmi);
                    } else if (pos == 1) {
                        valueList.add(md.height);
                    } else if (pos == 2) {
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
            List<Double> sdMinus2 = new ArrayList<>();
            List<Double> sdPlus2 = new ArrayList<>();
            Date birthday = null;
            try {
                birthday = format.parse(profile.birthday);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int age = Utils.getAgeInMonths(birthday,dateList.get(0));


            Log.i("Position", "pos" + pos);
            double[][] data = f.giveMeTheData(pos + 1, birthday, profile.sex == 1,dateList.get(0)); // type, birthday, gender
            int pastAge = age;
            for (Date d : dateList) {
                age = Utils.getAgeInMonths(birthday,d);

                if(pos !=0){   // reload the Data if age passed a critical border ( file border)
                    if((pastAge<60 && age>60)||(pastAge>60 && age<60)){
                         data = f.giveMeTheData(pos + 1, birthday, profile.sex == 1,d); // type, birthday, gender
                    }
                }else{
                    if((pastAge<24 && age>24) ||(pastAge<60 && age>60)|| (pastAge>24 && age<24) ||(pastAge>60 && age<60)) {
                    data = f.giveMeTheData(pos + 1, birthday, profile.sex == 1,d); // type, birthday, gender

                    }
                }
                pastAge = age;

                if (pos == 2 && age > 120) {
                    sdMinus2.add(0.0);
                    sdPlus2.add(100.0);
                }
                if (age < 228) { //19 jahre
                    if (age <= 60 && pos != 0) {
                        age *= 30;
                    }
                    for (int i = 0; i < data.length; i++) {
                        if ((int) data[i][0] == age) {
                            //  optimal.add(data[i][data[0].length / 2]);
                            sdMinus2.add(data[i][data[0].length / 2 - 2]);
                            sdPlus2.add(data[i][data[0].length / 2 + 2]);
                            break;
                        }
                    }
                } else { //over 19
                    if (pos == 0) {

                        sdMinus2.add(18.5);
                        sdPlus2.add(25.0);
                    }
                }
            }

            final Date[] dateArray = dateList.toArray(new Date[dateList.size()]);
            // final Double[] optimalArray = optimal.toArray(new Double[optimal.size()]);
            // initialize our XYPlot reference:
            //TODO fix zoom and pan
            //  mPlot.setOnTouchListener(this);

            String seriesName = "";
            String seriesUnit ="";
            if (pos == 0) {
                seriesName = "BMI";
                seriesUnit = seriesName;
            } else if (pos == 1) {
                seriesName = "height in cm";
                seriesUnit = "cm";
            } else if (pos == 2) {
                seriesName = "weight in kg";
                seriesUnit = "kg";
            }

            mPlot.setTitle(seriesName);
            mPlot.setRangeLabel(seriesUnit);

            XYSeries valueSeries = new SimpleXYSeries(valueList, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, seriesUnit);
            XYSeries sdm1Series = new SimpleXYSeries(sdMinus2, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "SD-2");
            XYSeries sdp1Series = new SimpleXYSeries(sdPlus2, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "SD+2");


            // create formatters to use for drawing a series using LineAndPointRenderer
            // and configure them from xml:
            LineAndPointFormatter bmiFormat = new LineAndPointFormatter();
            bmiFormat.setPointLabelFormatter(new PointLabelFormatter());
            bmiFormat.configure(getApplicationContext(),
                    R.xml.line_point_formatter_with_labels);
            bmiFormat.setPointLabeler(null);
            // just for fun, add some smoothing to the lines:
            // see: http://androidplot.com/smooth-curves-and-androidplot/
            bmiFormat.setInterpolationParams(
                    new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));


            LineAndPointFormatter sd1Format = new LineAndPointFormatter();
            sd1Format.setPointLabelFormatter(new PointLabelFormatter());
            sd1Format.configure(getApplicationContext(),
                    R.xml.line_point_formatter_with_labels_2);
            sd1Format.setPointLabeler(null);
            // just for fun, add some smoothing to the lines:
            // see: http://androidplot.com/smooth-curves-and-androidplot/
            sd1Format.setInterpolationParams(
                    new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

            // add a new series' to the xyplot:
            mPlot.addSeries(valueSeries, bmiFormat);
            // mPlot.addSeries(optimalSeries,sd0Format);
            mPlot.addSeries(sdm1Series, sd1Format);
            mPlot.addSeries(sdp1Series, sd1Format);

            // draw a domain tick for each year:
            mPlot.setDomainStep(XYStepMode.SUBDIVIDE, dateArray.length);
            mPlot.setRangeValueFormat(new DecimalFormat("#"));

            mPlot.setDomainValueFormat(new Format() {

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

            // reduce the number of range labels
            mPlot.setTicksPerRangeLabel(1);
            mPlot.setTicksPerDomainLabel(2);


            // rotate domain labels 45 degrees to make them more compact horizontally:
            mPlot.getGraphWidget().setDomainLabelOrientation(-45);
            mPlot.getGraphWidget().setDomainTickLabelVerticalOffset(15);
            mPlot.getGraphWidget().setDomainTickLabelWidth(5);
            mPlot.getGraphWidget().setRangeTickLabelWidth(5);
            mPlot.getGraphWidget().setDomainGridLinePaint(null);
            mPlot.redraw();

            //Set of internal variables for keeping track of the boundaries
            mPlot.calculateMinMaxVals();
            //minXY=new PointF(mPlot.getCalculatedMinX().floatValue(),mPlot.getCalculatedMinY().floatValue());
            //maxXY=new PointF(mPlot.getCalculatedMaxX().floatValue(),mPlot.getCalculatedMaxY().floatValue());

            return v;
        }
    }
}
