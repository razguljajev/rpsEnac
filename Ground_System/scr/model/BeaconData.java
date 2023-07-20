package model;

import fr.enac.sita.visuradar.model.IBeacon;
import javafx.beans.property.*;

import java.util.Objects;

/**
 * When a beacon of the route is retrieved, we generate its data from the Airspace Database + Rejeu data
 */
public class BeaconData {

    private IBeacon beacon;
    private String time;
    private SimpleStringProperty flightLevel;
    private final SimpleStringProperty distanceToBeacon;
    private final SimpleStringProperty distanceToBeaconNM;

    public BeaconData(IBeacon beacon, String time, SimpleStringProperty flightLevel) {
        this.beacon = beacon;
        this.time = time;
        this.flightLevel = flightLevel;
        distanceToBeacon = new SimpleStringProperty("");
        distanceToBeaconNM = new SimpleStringProperty("");
    }

    public StringProperty getDistance() {
        return distanceToBeacon;
    }
    public StringProperty getDistanceNM() {
        return distanceToBeaconNM;
    }

    /**
     * We compute the distance in double to make it as a StringProperty
     * @param distance Distance for the Aircraft to reach the beacon
     */
    public void setDistance(double distance) {
        distance = Math.round(distance * 100);
        distance = distance / 100;
        distanceToBeaconNM.set(distance + " NM");
        distanceToBeacon.set(String.valueOf(distance));
    }

    public SimpleStringProperty getFlightLevel() {
        return flightLevel;
    }

    public void setFlightLevel(SimpleStringProperty flightLevel) {
        this.flightLevel = flightLevel;
    }

    public String getTime() {
        return this.time;
    }

    public IBeacon getBeacon() {
        return beacon;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setBeacon(IBeacon beacon) {
        this.beacon = beacon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BeaconData beaconData = (BeaconData) o;

        return Objects.equals(getBeacon().getCode(), beaconData.getBeacon().getCode());
    }
}
