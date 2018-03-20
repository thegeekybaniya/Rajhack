package io.github.thegeekybaniya.myapplication;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by muralidharan on 6/4/16.
 */
public class AirportQuery {

    private static final String LOG_TAG = AirportQuery.class.getSimpleName();

    public static List<Airport> getAirports(Context context, String query) {
        List<Airport> airports = new ArrayList<Airport>();

        final String BASE_URL = "https://api.sandbox.amadeus.com/v1.2/airports/autocomplete?";

        final String API_KEY_PARAM = "apikey";
        final String TERM_PARAM = "term";

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String airportsJsonStr = null;

        try {
            Uri uri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, context.getString(R.string.amadeus_api_key))
                    .appendQueryParameter(TERM_PARAM, query).build();

            URL url = new URL(uri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            airportsJsonStr = buffer.toString();

            JSONArray airportsJsonArray = new JSONArray(airportsJsonStr);

            for (int i = 0; i < airportsJsonArray.length(); i++) {
                JSONObject airportJsonObj = airportsJsonArray.getJSONObject(i);
                String airportCode = airportJsonObj.getString("value");

                String label = airportJsonObj.getString("label");
                int bracketIndex = label.indexOf(" [");
                if (bracketIndex > 0) {
                    label = label.substring(0, bracketIndex);
                }
                airports.add(new Airport(airportCode, label));
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error getting results from Amadeus API", e);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing json from Amadeus API", e);
        }

        return airports;
    }
}
