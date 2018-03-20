package io.github.thegeekybaniya.myapplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by muralidharan on 6/12/16.
 */
public class FlightOption implements Serializable {

    float adultsBasePrice;
    float adultsTax;
    float childrenBasePrice;
    float childrenTax;
    int numAdults;
    int numChildren;

    List<FlightSlice> flightSlices;

    public FlightOption() {
        flightSlices = new ArrayList<>();
    }

    public float getTotalPrice() {
        return numAdults * (adultsBasePrice + adultsTax) +
                numChildren * (childrenBasePrice + childrenTax);
    }

    public float getAdultsBasePrice() {
        return adultsBasePrice;
    }

    public void setAdultsBasePrice(float adultsBasePrice) {
        this.adultsBasePrice = adultsBasePrice;
    }

    public float getAdultsTax() {
        return adultsTax;
    }

    public void setAdultsTax(float adultsTax) {
        this.adultsTax = adultsTax;
    }

    public float getChildrenBasePrice() {
        return childrenBasePrice;
    }

    public void setChildrenBasePrice(float childrenBasePrice) {
        this.childrenBasePrice = childrenBasePrice;
    }

    public float getChildrenTax() {
        return childrenTax;
    }

    public void setChildrenTax(float childrenTax) {
        this.childrenTax = childrenTax;
    }

    public int getNumAdults() {
        return numAdults;
    }

    public void setNumAdults(int numAdults) {
        this.numAdults = numAdults;
    }

    public int getNumChildren() {
        return numChildren;
    }

    public void setNumChildren(int numChildren) {
        this.numChildren = numChildren;
    }

    public List<FlightSlice> getFlightSlices() {
        return flightSlices;
    }

    public void addFlightSlice(FlightSlice flightSlice) {
        flightSlices.add(flightSlice);
    }
}
