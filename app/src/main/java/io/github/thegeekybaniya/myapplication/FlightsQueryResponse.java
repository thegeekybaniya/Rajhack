package io.github.thegeekybaniya.myapplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by muralidharan on 6/9/16.
 */
public class FlightsQueryResponse implements Serializable {

    private static final String LOG_TAG  = FlightsQueryResponse.class.getSimpleName();

    List<FlightOption> flightOptions;

    // FlightSegment logos are available here:
    // http://pics.avs.io/<width>/<height>/<iata>.png

    public FlightsQueryResponse() {
        flightOptions = new ArrayList<FlightOption>();
    }

    public static FlightsQueryResponse parseFrom(JSONObject responseJSON) throws JSONException {
        FlightsQueryResponse response = new FlightsQueryResponse();
        JSONObject tripsJSON = responseJSON.getJSONObject("trips");
        JSONObject dataJSON = tripsJSON.getJSONObject("data");

        JSONArray citiesJSON = dataJSON.getJSONArray("city");
        Map<String, String> cityCodeToName = new HashMap<>();
        if (citiesJSON != null) {
            for (int i = 0; i < citiesJSON.length(); i++) {
                JSONObject cityJSON = citiesJSON.getJSONObject(i);
                String cityCode = cityJSON.getString("code");
                String cityName = cityJSON.getString("name");

                if (!isEmpty(cityCode) && !isEmpty(cityName)) {
                    cityCodeToName.put(cityCode, cityName);
                }
            }
        }

        JSONArray airportsJSON = dataJSON.getJSONArray("airport");
        Map<String, Airport> airports = new HashMap<>();
        if (airportsJSON != null) {
            for (int i = 0; i < airportsJSON.length(); i++) {
                JSONObject airportJSON = airportsJSON.getJSONObject(i);

                String airportCode = airportJSON.getString("code");
                String airportName = airportJSON.getString("name");
                String airportCityCode = airportJSON.getString("city");
                if (!isEmpty(airportName) && !isEmpty(airportCode)) {
                    Airport airport = new Airport(airportCode, airportName);
                    if (!isEmpty(airportCityCode)) {
                        String airportCityName = cityCodeToName.get(airportCityCode);
                        if (!isEmpty(airportCityName)) {
                            airport.setCity(airportCityName);
                        }
                    }
                    airports.put(airportCode, airport);
                }
            }
        }

        JSONArray carriersJSON = dataJSON.getJSONArray("carrier");
        Map<String, String> carriers = new HashMap<>();
        if (carriersJSON != null) {
            for(int i = 0; i < carriersJSON.length(); i++) {
                JSONObject carrierJSON = carriersJSON.getJSONObject(i);
                String carrierCode = carrierJSON.getString("code");
                String carrierName = carrierJSON.getString("name");

                if (!isEmpty(carrierCode) && !isEmpty(carrierName)) {
                    carriers.put(carrierCode, carrierName);
                }
            }
        }

        JSONArray tripOptionsJSON = tripsJSON.getJSONArray("tripOption");

        for (int i = 0; i < tripOptionsJSON.length(); i++) {
            JSONObject tripOptionJSON = tripOptionsJSON.getJSONObject(i);
            FlightOption flightOption = new FlightOption();

            String totalPriceStr = tripOptionJSON.getString("saleTotal");
            float totalPrice = getFloatPrice(totalPriceStr);
            float adultsBasePrice = 0, adultsTax = 0, childrenBasePrice = 0, childrenTax = 0;
            int numAdults = 0, numChildren = 0;

            JSONArray pricingJSONArray = tripOptionJSON.getJSONArray("pricing");
            for (int j = 0; j < pricingJSONArray.length(); j++) {
                JSONObject pricingJSON = pricingJSONArray.getJSONObject(j);
                String saleFareStr = pricingJSON.getString("saleFareTotal");
                float saleFare = getFloatPrice(saleFareStr);
                String saleTaxStr = pricingJSON.getString("saleTaxTotal");
                float saleTax = getFloatPrice(saleTaxStr);
                JSONObject passengersJSON = pricingJSON.getJSONObject("passengers");
                if (passengersJSON.has("adultCount")) {
                    numAdults = passengersJSON.getInt("adultCount");
                    if (numAdults > 0) {
                        adultsBasePrice = saleFare;
                        adultsTax = saleTax;
                    }
                }
                if (passengersJSON.has("childCount")) {
                    numChildren = passengersJSON.getInt("childCount");
                    if (numChildren > 0) {
                        childrenBasePrice = saleFare;
                        childrenTax = saleTax;
                    }
                }
            }

            flightOption.setNumAdults(numAdults);
            flightOption.setNumChildren(numChildren);
            flightOption.setAdultsBasePrice(adultsBasePrice);
            flightOption.setAdultsTax(adultsTax);
            flightOption.setChildrenBasePrice(childrenBasePrice);
            flightOption.setChildrenTax(childrenTax);

            response.addFlightOption(flightOption);

            parseFlights(tripOptionJSON, flightOption, carriers, airports);

        }
        return response;
    }

    public static void parseFlights(JSONObject tripOption,
                                    FlightOption flightOption,
                                    Map<String, String> carriers,
                                    Map<String, Airport> airports) throws JSONException {
        JSONArray slicesArray = tripOption.getJSONArray("slice");

        for (int i = 0; i < slicesArray.length(); i++) {
            JSONObject sliceJSON = slicesArray.getJSONObject(i);
            int sliceDuration = sliceJSON.getInt("duration");

            JSONArray segmentArray = sliceJSON.getJSONArray("segment");
            int connectionDuration = -1;
            FlightSlice flightSlice = null;
            for (int j = 0; j < segmentArray.length(); j++) {
                JSONObject segmentJSON = segmentArray.getJSONObject(j);

                JSONObject flightJSON = segmentJSON.getJSONObject("flight");

                String carrier = flightJSON.getString("carrier");
                String flightNumber = flightJSON.getString("number");
                String carrierName = carriers.get(carrier);

                JSONObject legJSON = segmentJSON.getJSONArray("leg").getJSONObject(0);
                String originAirportCode = legJSON.getString("origin");
                String destinationAirportCode = legJSON.getString("destination");
                String departureTimeStr = legJSON.getString("departureTime");
                String arrivalTimeStr = legJSON.getString("arrivalTime");
                int segmentDuration = legJSON.getInt("duration");

                Airport originAirport = airports.get(originAirportCode);
                Airport destinationAirport = airports.get(destinationAirportCode);

                FlightSegment flightSegment = new FlightSegment(originAirport, destinationAirport);
                flightSegment.setDurationMins(segmentDuration);
                flightSegment.setAirline(carrierName);
                flightSegment.setCarrierCode(carrier);
                flightSegment.setFlightNumber(flightNumber);

                if (connectionDuration < 0) {
                    // First segment
                    flightSlice = new FlightSlice(flightSegment);
                } else {
                    flightSlice.addFlight(flightSegment, connectionDuration);
                }
                if (segmentJSON.has("connectionDuration")) {
                    connectionDuration = segmentJSON.getInt("connectionDuration");
                }
            }
            flightSlice.setDurationMins(sliceDuration);
            flightOption.addFlightSlice(flightSlice);
        }
    }

    // Expect a date of the form 2016-06-16T12:35+04:00 and return an array with first element being
    // the date: '2016-06-16' and the second being the time: '12:35'. Skip timezone for now.
    public static Calendar parseDateAndTime(String jsonDateStr) {
        Calendar cal = Calendar.getInstance();
        int year = Integer.parseInt(jsonDateStr.substring(0,4));
        int month = Integer.parseInt(jsonDateStr.substring(5,7));
        int date = Integer.parseInt(jsonDateStr.substring(8,10));
        int hour = Integer.parseInt(jsonDateStr.substring(11, 13));
        int min = Integer.parseInt(jsonDateStr.substring(14, 16));

//        String timeZoneStr = jsonDateStr.substring(16,22);
//        TimeZone tz = TimeZone.getTimeZone("GMT" + timeZoneStr);
//        cal.setTimeZone(tz);

        cal.set(year, month, date, hour, min, 0);
        return cal;
    }

    public static float getFloatPrice(String priceStr) throws NumberFormatException {
        priceStr = priceStr.replaceAll("[^\\d.]", "");
        return Float.parseFloat(priceStr);
    }

    public static boolean isEmpty(String str) {
        if (str == null || str.trim().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public void addFlightOption(FlightOption flightOption) {
        flightOptions.add(flightOption);
    }

    public List<FlightOption> getFlightOptions() {
        return flightOptions;
    }
}
