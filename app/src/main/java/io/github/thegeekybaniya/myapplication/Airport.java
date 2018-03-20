package io.github.thegeekybaniya.myapplication;

import java.io.Serializable;

/**
 * Created by muralidharan on 6/4/16.
 *
 * Class to represent an Airport with its airport code and label.
 * Its expected to be used to populate results from the amadeus airport autocomplete API.
 * More info about the API is at:
 * https://sandbox.amadeus.com/travel-innovation-sandbox/apis/get/airports/autocomplete
 *
 */

public class Airport implements Serializable {

    protected String airportCode;
    protected String label;
    protected String city;

    public Airport(String airportCode, String label) {
        this.airportCode = airportCode;
        this.label = label;
    }

    public Airport() {
    }

    public String getAirportCode() {
        return airportCode;
    }

    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
