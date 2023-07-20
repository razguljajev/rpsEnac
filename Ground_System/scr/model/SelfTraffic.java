package model;

import fr.enac.sita.visuradar.model.FlightPlan;
import fr.enac.sita.visuradar.model.IBeacon;
import fr.enac.sita.visuradar.model.Point;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * SelfTraffic is related to all the data of the aircraft.
 * In fact, every component of the aircraft system are present here.
 * @see FlightData
 */
public class SelfTraffic {

    private final String flightID;
    private final Point position;
    private final DoubleProperty heading;
    private final DoubleProperty altitude;
    private final DoubleProperty speed;
    private final StringProperty callSign;
    private final StringProperty aircraftType;
    private String time;
    private final FlightPlan flightPlan;
    private final ListProperty<BeaconData> route;
    private final SimpleBooleanProperty isFollowingRoute;
    private final DoubleProperty totalRange;

    public SelfTraffic(double x, double y, String flightID) {
        position = new Point(x, y);
        heading = new SimpleDoubleProperty(0.);
        altitude = new SimpleDoubleProperty(0.);
        speed = new SimpleDoubleProperty(0.);
        callSign = new SimpleStringProperty("");
        aircraftType = new SimpleStringProperty("");
        flightPlan = new FlightPlan();
        route = new SimpleListProperty<>(FXCollections.observableArrayList());
        isFollowingRoute = new SimpleBooleanProperty(true);
        this.flightID = flightID;
        totalRange = new SimpleDoubleProperty(0);

        setUpRange();
    }

    /**
     * Total range is not computed as the other parameters.
     * In brief, it adds all the distance Aircraft - Beacon of the route, but because this distance is a StringProperty,
     * it needs to be converted, adding the fact that those distances are constantly updating
     */
    private void setUpRange() {
        // Create a listener to update totalRange whenever the route or distances change
        ChangeListener<Object> listener = (obs, oldValue, newValue) -> {
            double sum = 0.0;
            for (BeaconData data : route) {
                if(!data.getDistance().get().isEmpty()) sum += Double.parseDouble(data.getDistance().get());
            }
            totalRange.set(sum);
        };

        // Add the listener to the route list and each BeaconData's distance property
        route.addListener((ListChangeListener<BeaconData>) change -> {
            while (change.next()) {
                if (change.wasAdded() || change.wasRemoved()) {
                    for (BeaconData data : change.getAddedSubList()) {
                        data.getDistance().addListener(listener);
                    }
                    for (BeaconData data : change.getRemoved()) {
                        data.getDistance().removeListener(listener);
                    }
                }
            }
        });

        // Initialize the listener for existing data in the route list
        for (BeaconData data : route) {
            data.getDistance().addListener(listener);
        }
    }

    private double calculateDistance(double x, double y) {
        double deltaX = x - position.getX();
        double deltaY = y - position.getY();

        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    public void updatePosition(double x, double y) {
        position.setX(x);
        position.setY(y);
        updateDistanceToTheBeacon();
    }

    public void updateHeading(double heading) {
        this.heading.set(heading);
    }
    public void updateAltitude(double altitude) {
        this.altitude.set(altitude);
    }

    public String getFlightID(){
        return flightID;
    }

    public Point getPosition() {
        return position;
    }

    public DoubleProperty getHeading() {
        return heading;
    }

    public DoubleProperty getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude.set(altitude);
    }

    public DoubleProperty getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed.set(speed);
    }

    public StringProperty getCallSign() {
        return callSign;
    }

    public void setCallSign(String callSign) {
        this.callSign.set(callSign);
    }

    public StringProperty getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(String aircraftType) {
        this.aircraftType.set(aircraftType);
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public FlightPlan getFlightPlan(){
        return flightPlan;
    }

    public ListProperty<BeaconData> getRoute() {
        return route;
    }

    public DoubleProperty getTotalRange() {
        return totalRange;
    }

    public void addBeaconToRoute(IBeacon beacon, String time, SimpleStringProperty flightLevel) {
        ObservableList<BeaconData> list = route.get();
        list.add(new BeaconData(beacon, time, flightLevel));
        route.set(list);
    }

    public void deleteBeaconFromRoute(String code){
        route.getValue().removeIf(data -> data.getBeacon().getCode().equals(code));
    }

    private void updateDistanceToTheBeacon() {
        Platform.runLater(() -> {
            for (BeaconData bd : route) {
                bd.setDistance(calculateDistance(bd.getBeacon().getX(), bd.getBeacon().getY()));
            }
        });
    }

    public BooleanProperty getIsFollowingRoute(){
        return isFollowingRoute;
    }

    public void clearRoute() {
        route.clear();
    }
}