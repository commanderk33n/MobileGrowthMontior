package de.hs_mannheim.planb.mobilegrowthmonitor.profiles;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.hs_mannheim.planb.mobilegrowthmonitor.R;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.ProfileData;
import de.hs_mannheim.planb.mobilegrowthmonitor.datavisual.Listener;

/**
 * Created by eikood on 05.05.2016.
 *
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {

    Context context;
    List<ProfileData> dataList = new ArrayList<>();
    LayoutInflater inflater;
    Listener listener;

    public ListAdapter(Context context, List<ProfileData> dataList) {

        this.context = context;
        this.dataList = dataList;
        this.listener = (Listener) context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View convertView = inflater.inflate(R.layout.profilelist_item, parent, false);
        final ListViewHolder holder = new ListViewHolder(convertView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = dataList.get(holder.getAdapterPosition()).index;
                listener.selectProfile(index);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        holder.tv_name.setText(dataList.get(position).firstname + " " + dataList.get(position).lastname + ", ");

        // shows age
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        int age = 0;
        try {
            Date tempDate = format.parse(dataList.get(position).birthday);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(tempDate);
            Calendar today = Calendar.getInstance();
            age = today.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
            if (today.get(Calendar.MONTH) < calendar.get(Calendar.MONTH)) {
                age--;
            } else if (today.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH) < calendar.get(Calendar.DAY_OF_MONTH)) {
                age--;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.tv_bday.setText(Integer.toString(age) + " " + context.getString(R.string.template_age));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
    class ListViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_bday;
        public ListViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_bday = (TextView) itemView.findViewById(R.id.tv_bday);
        }
    }
}