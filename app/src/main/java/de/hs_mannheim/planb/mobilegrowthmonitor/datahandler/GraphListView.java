package de.hs_mannheim.planb.mobilegrowthmonitor.datahandler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.AbstractAppLock;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.AppLockView;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.BaseActivity;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.LockManager;

public class GraphListView extends BaseActivity {
    private ListView plots;
    DbHelper dbHelper;
    private int profileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.graph_list_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_graph_list);
        setSupportActionBar(toolbar);

        dbHelper = DbHelper.getInstance(getApplicationContext());
        Bundle extras = getIntent().getExtras();
        profileId = extras.getInt("profile_Id");
        plots = (ListView) findViewById(R.id.lv_plots);
        plots.setAdapter(new MyViewAdapter(getApplicationContext(), R.layout.graph_view, null));

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_graph_list_view, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.graph_help) {

            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.help_graph_list_view, null);

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(R.string.graph_help_title);
            alertDialog.setView(view);
            alertDialog.setIcon(R.drawable.logo_planb_klein);
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //dismisses Dialog automatically
                }
            });
            AlertDialog alert = alertDialog.create();
            alert.show();

        }

        return super.onOptionsItemSelected(item);
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
            if(pos==0){
                pos=1;
            }else if(pos==1){
                pos=2;
            }else if(pos==2){
                pos=0;
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

                if (pos == 1 && age > 228) { //take default values for 19 year olds if a person is older
                    if(profile.sex==1){
                        sdMinus2.add(154.648);
                        sdPlus2.add(183.841	);
                    }else{
                        sdMinus2.add(143.532);
                        sdPlus2.add(169.696	);
                    }

                }
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
                seriesName = getString(R.string.height_in_cm);
                seriesUnit = "cm";
            } else if (pos == 2) {
                seriesName = getString(R.string.weight_in_kg);
                seriesUnit = "kg";
            }

            mPlot.setTitle(seriesName);
            mPlot.setRangeLabel(seriesUnit);

            XYSeries valueSeries = new SimpleXYSeries(valueList, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, seriesUnit);
            XYSeries sdm2Series = new SimpleXYSeries(sdMinus2, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "SD-2");
            XYSeries sdp2Series = new SimpleXYSeries(sdPlus2, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "SD+2");


            // create formatters to use for drawing a series using LineAndPointRenderer
            // and configure them from xml:
            LineAndPointFormatter valueFormat = new LineAndPointFormatter();
            valueFormat.setPointLabelFormatter(new PointLabelFormatter());
            valueFormat.configure(getApplicationContext(),
                    R.xml.line_point_formatter_with_labels);
            valueFormat.setPointLabeler(null);

            // just for fun, add some smoothing to the lines:
            // see: http://androidplot.com/smooth-curves-and-androidplot/
            valueFormat.setInterpolationParams(
                    new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));


            LineAndPointFormatter sd2Format = new LineAndPointFormatter();
            sd2Format.setPointLabelFormatter(new PointLabelFormatter());
            sd2Format.configure(getApplicationContext(),
                    R.xml.line_point_formatter_with_labels_2);
            sd2Format.setPointLabeler(null);
            // just for fun, add some smoothing to the lines:
            // see: http://androidplot.com/smooth-curves-and-androidplot/
            sd2Format.setInterpolationParams(
                    new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

            // add a new series' to the xyplot:
            mPlot.addSeries(valueSeries, valueFormat);
            // mPlot.addSeries(optimalSeries,sd0Format);
            mPlot.addSeries(sdm2Series, sd2Format);
            mPlot.addSeries(sdp2Series, sd2Format);

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
            mPlot.setTicksPerRangeLabel(2);
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
