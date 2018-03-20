package io.github.thegeekybaniya.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muralidharan on 6/3/16.
 */
public class AirportAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private static final int MAX_RESULTS = 10;
    private Context mContext;
    private List<Airport> resultList = new ArrayList<Airport>(MAX_RESULTS);

    public AirportAutoCompleteAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public Airport getItem(int position) {
        return resultList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.simple_dropdown_item_2line, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.text1)).setText(getItem(position).getLabel());
        ((TextView) convertView.findViewById(R.id.text2)).setText(getItem(position).getAirportCode());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<Airport> airports = findAirports(mContext, constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = airports;
                    filterResults.count = airports.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    resultList = (List<Airport>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }

    protected List<Airport> findAirports(Context context, String airportStr) {
        return AirportQuery.getAirports(context, airportStr);
    }
}
