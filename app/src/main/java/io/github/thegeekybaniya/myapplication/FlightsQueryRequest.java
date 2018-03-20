package io.github.thegeekybaniya.myapplication;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by muralidharan on 6/2/16.
 */
public class FlightsQueryRequest {

    private static final String LOG_TAG = FlightsQueryRequest.class.getSimpleName();

    protected String fromAirport;
    protected String toAirport;
    protected String depDate;
    protected String returnDate;

    protected Context context;

    public FlightsQueryRequest(Context context, String fromAirport, String toAirport) {
        this.fromAirport = fromAirport;
        this.toAirport = toAirport;
        this.context = context;
    }

    public JSONObject getAPIRequestJSON(int numResults, int numAdults, int numChildren) throws JSONException {
        JSONObject ret = new JSONObject();
        JSONObject requestObj = new JSONObject();
        ret.put("request", requestObj);
        requestObj.put("solutions", numResults);

        // Get prices in USD for now.
        requestObj.put("saleCountry", "US");

        requestObj.put("refundable", false);
        JSONObject passengersObj = new JSONObject();
        passengersObj.put("adultCount", numAdults);
        passengersObj.put("childCount", numChildren);
        requestObj.put("passengers", passengersObj);
        JSONArray slice = new JSONArray();
        requestObj.put("slice", slice);
        if (depDate != null && !depDate.trim().isEmpty()) {
            slice.put(getFlightObj(fromAirport, toAirport, depDate));
        }
        if (returnDate != null && !returnDate.trim().isEmpty()) {
            slice.put(getFlightObj(toAirport, fromAirport, returnDate));
        }
        return ret;
    }

    protected JSONObject getFlightObj(
            String fromAirport, String toAirport, String date) throws JSONException {
        JSONObject flightObj = new JSONObject();

        flightObj.put("origin", fromAirport);
        flightObj.put("destination", toAirport);
        flightObj.put("date", date);

        return flightObj;
    }

    public String getFromAirport() {
        return fromAirport;
    }

    public void setFromAirport(String fromAirport) {
        this.fromAirport = fromAirport;
    }

    public String getToAirport() {
        return toAirport;
    }

    public void setToAirport(String toAirport) {
        this.toAirport = toAirport;
    }

    public String getDepDate() {
        return depDate;
    }

    public void setDepDate(String depDate) {
        this.depDate = depDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (fromAirport != null) {
            builder.append("fromAirport: ");
            builder.append(fromAirport);
            builder.append(' ');
        }
        if (toAirport != null) {
            builder.append("toAirport: ");
            builder.append(toAirport);
            builder.append(' ');
        }
        if (depDate != null) {
            builder.append("depDate: ");
            builder.append(depDate);
            builder.append(' ');
        }
        if (returnDate != null) {
            builder.append("returnDate: ");
            builder.append(returnDate);
            builder.append(' ');
        }
        return builder.toString();
    }
}
