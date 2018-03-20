package io.github.thegeekybaniya.myapplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by muralidharan on 6/14/16.
 */
public class FlightSlice implements Serializable {

    protected List<FlightSegment> flightSegments;
    protected List<Integer> connectionsMins;
    protected int durationMins;

    public FlightSlice(FlightSegment firstFlightSegment) {
        flightSegments = new ArrayList<>();
        connectionsMins = new ArrayList<>();

        flightSegments.add(firstFlightSegment);
    }

    public void addFlight(FlightSegment flightSegment, int connectionMins) {
        flightSegments.add(flightSegment);
        connectionsMins.add(connectionMins);
    }

    public List<FlightSegment> getFlightSegments() {
        return flightSegments;
    }

    public List<Integer> getConnectionsMins() {
        return connectionsMins;
    }

    public int getDurationMins() {
        return durationMins;
    }

    public void setDurationMins(int durationMins) {
        this.durationMins = durationMins;
    }
}
