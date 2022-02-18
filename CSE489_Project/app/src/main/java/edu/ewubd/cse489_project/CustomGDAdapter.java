package edu.ewubd.cse489_project;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;


public class CustomGDAdapter extends ArrayAdapter<GD> {

    private final Context context;
    private final ArrayList<GD> values;


    public CustomGDAdapter(@NonNull Context context, @NonNull ArrayList<GD> objects) {
        super(context, -1, objects);
        this.context = context;
        this.values = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.layout_gd_rows, parent, false);

        TextView gdFullName = rowView.findViewById(R.id.tvFullName);
        TextView gdDateTime = rowView.findViewById(R.id.tvDate);
        TextView gdPoliceStationName = rowView.findViewById(R.id.tvPoliceStationName);
        TextView gdStatus = rowView.findViewById(R.id.tvStatus);

        gdFullName.setText(values.get(position).full_name);
        gdDateTime.setText(values.get(position).datetime);
        gdPoliceStationName.setText(values.get(position).police_station);
//        gdStatus.setText(values.get(position).status);
        String status = values.get(position).status;
        if (status.equals("Accepted")) {
            gdStatus.setText(status);
            gdStatus.setTextColor(Color.rgb(50,205,50));
        }
        else if (status.equals("Rejected")) {
            gdStatus.setText(status);
            gdStatus.setTextColor(Color.RED);
        }
        else if (status.equals("Pending")) {
            gdStatus.setText(status);
        }

        return rowView;
    }
}
