package io.github.thegeekybaniya.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeAirportTextViews();
        initializeFindFlightsButton();
        initializeDatePicker();
        initializeNumPassengerSpinners();
    }

    protected void initializeNumPassengerSpinners() {
        final int MAX_PASSENGERS = 10;
        Spinner adultsSpinner = (Spinner) findViewById(R.id.num_adults_spinner);
        Spinner childrenSpinner = (Spinner) findViewById(R.id.num_children_spinner);

        initializeNumPassengerSpinner(adultsSpinner, MAX_PASSENGERS);
        initializeNumPassengerSpinner(childrenSpinner, MAX_PASSENGERS);
    }

    protected void initializeNumPassengerSpinner(Spinner childrenSpinner, int max_passengers) {
        String[] items = new String[max_passengers + 1];
        for (int i = 0; i < max_passengers + 1; i++) {
            items[i] = "" + i;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.support_simple_spinner_dropdown_item, items);
        childrenSpinner.setAdapter(adapter);
    }


    protected void initializeAirportTextViews()
    {
        final int THRESHOLD = 2;
        final DelayAutoCompleteTextView fromCityTextView =
                (DelayAutoCompleteTextView) findViewById(R.id.from_city);
        fromCityTextView.setThreshold(THRESHOLD);
        fromCityTextView.setAdapter(new AirportAutoCompleteAdapter(this)); // 'this' is Activity instance
        fromCityTextView.setLoadingIndicator(
                (ProgressBar) findViewById(R.id.from_city_loading_indicator));
        fromCityTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Airport airport = (Airport) adapterView.getItemAtPosition(position);
                fromCityTextView.setText(airport.getAirportCode());
            }
        });

        final DelayAutoCompleteTextView toCityTextView =
                (DelayAutoCompleteTextView) findViewById(R.id.to_city);
        toCityTextView.setThreshold(THRESHOLD);
        toCityTextView.setAdapter(new AirportAutoCompleteAdapter(this)); // 'this' is Activity instance
        toCityTextView.setLoadingIndicator(
                (ProgressBar) findViewById(R.id.to_city_loading_indicator));
        toCityTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Airport airport = (Airport) adapterView.getItemAtPosition(position);
                toCityTextView.setText(airport.getAirportCode());
            }
        });
    }

    protected void initializeFindFlightsButton() {
        Button findFlightsButton = (Button)findViewById(R.id.button_find_flights);
        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.flights_loading_indicator);

        if (findFlightsButton != null) {
            findFlightsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String fromAirport = ((TextView)findViewById(R.id.from_city)).getText().toString();
                    String toAirport = ((TextView)findViewById(R.id.to_city)).getText().toString();
                    String depDate = ((TextView)findViewById(R.id.onward_date)).getText().toString();
                    String returnDate = ((TextView)findViewById(R.id.return_date)).getText().toString();
                    try {
                        progressBar.setVisibility(View.VISIBLE);
                        findFlights(fromAirport, toAirport, depDate, returnDate);
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "JSONException: ", e);
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            Log.w(LOG_TAG, "Could not find button to add clicklistener to!");
        }
    }

    protected void initializeDatePicker() {

        final TextView depDateTextView = (TextView)findViewById(R.id.onward_date);
        final TextView returnDateTextView = (TextView)findViewById(R.id.return_date);

        DateSetListener depDateSetListener = new DateSetListener(depDateTextView);
        DateSetListener returnDateSetListener = new DateSetListener(returnDateTextView);

        DateViewClickListener depDateClickListener = new DateViewClickListener(
                depDateTextView,
                depDateSetListener,
                getFragmentManager(),
                "depDate"
        );
        depDateClickListener.setMinDate(Calendar.getInstance());

        DateViewClickListener returnDateClickListener = new DateViewClickListener(
                returnDateTextView,
                returnDateSetListener,
                getFragmentManager(),
                "returnDate"
        );
        returnDateClickListener.setMinDate(Calendar.getInstance());
        depDateSetListener.registerClickListenerToSetMinDate(returnDateClickListener);

        depDateTextView.setOnClickListener(depDateClickListener);
        returnDateTextView.setOnClickListener(returnDateClickListener);
    }

    public void onCheckboxClicked(View view) {

        boolean checked = ((CheckBox)view).isChecked();
        if (view.getId() == R.id.checkbox_roundtrip) {
            TableRow returnDateTableRow = ((TableRow) findViewById(R.id.return_date_tablerow));
            if (checked) {
                returnDateTableRow.setVisibility(View.VISIBLE);
            } else {
                TextView returnDateTextView = (TextView)findViewById(R.id.return_date);
                returnDateTextView.setText("");

                returnDateTableRow.setVisibility(View.GONE);

            }
        }
    }

    protected void findFlights(
            String fromAirport,
            String toAirport,
            String depDate,
            String returnDate) throws JSONException {

        FlightsQueryRequest request = new FlightsQueryRequest(getBaseContext(), fromAirport, toAirport);
        request.setDepDate(depDate);
        request.setReturnDate(returnDate);

        Spinner adultsSpinner = (Spinner) findViewById(R.id.num_adults_spinner);
        Spinner childrenSpinner = (Spinner) findViewById(R.id.num_children_spinner);

        int num_adults = 0;
        int num_children = 0;

        try {
            num_adults = Integer.parseInt(adultsSpinner.getSelectedItem().toString());
            num_children = Integer.parseInt(childrenSpinner.getSelectedItem().toString());
        } catch (NumberFormatException e) {
            Log.e(LOG_TAG, "Could not parse passenger counts!", e);
        }

        JSONObject requestJSON = request.getAPIRequestJSON(20, num_adults, num_children);
        Log.v(LOG_TAG, "RequestJSON: " + requestJSON);

        RequestQueue queue = Volley.newRequestQueue(request.getContext());
        final String BASE_URL = "https://www.googleapis.com/qpxExpress/v1/trips/search?key=yoloyolyo";
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.flights_loading_indicator);

        final MainActivity thisActivity = this;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                BASE_URL,
                requestJSON,
                new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject responseJSON) {
                try {
                    Log.v(LOG_TAG, "Received response: " + responseJSON.toString(4));
                    FlightsQueryResponse response = FlightsQueryResponse.parseFrom(responseJSON);

                    Intent resultsIntent = new Intent(thisActivity, FlightResultsActivity.class);
                    resultsIntent.putExtra(Intent.EXTRA_STREAM, response);
                    startActivity(resultsIntent);
                } catch (JSONException e) {
                    Log.v(LOG_TAG, "Invalid JSON? ", e);
                    findFlightsError();
                } finally {
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "Received error response: " + error.getMessage() );
                findFlightsError();
                progressBar.setVisibility(View.GONE);
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }

    protected void findFlightsError() {
        Toast.makeText(
                getBaseContext(),
                "Error retrieving flight results! Please try again later",
                Toast.LENGTH_LONG).show();
    }
}
