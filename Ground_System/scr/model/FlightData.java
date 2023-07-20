package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

/**
 * FlightData is a hub for every parameter of the flight in course. There is no data on it, but access to these parameters
 * @see SelfTraffic Contains all the data of the Aircraft parameters
 * @see Airspace Contains all the data of the navigation, such as the sectors and beacons
 * @see Order Contains all the orders that has been declared during the flight
 */
public class FlightData {

    private final SelfTraffic selfTraffic;
    private final Airspace airspace;
    private final ObservableList<Order> orderList;

    public FlightData(String flightID, Airspace airspace){
        selfTraffic = new SelfTraffic(0,0, flightID);
        orderList = FXCollections.observableArrayList(new ArrayList<>());
        this.airspace = airspace;
    }

    public SelfTraffic getSelfTraffic() {
        return selfTraffic;
    }

    public Airspace getAirspace() {
        return airspace;
    }

    public ObservableList<Order> getOrderList() {
        return orderList;
    }

    public void addNewOrder(Order order){
        getOrderList().add(order);
    }

    public void addBeaconToRoute(String code, String time, String level) {
        for(int i = 0; i < airspace.getPublishedBeacons().size(); i++){
            if(airspace.getPublishedBeacons().get(i).getCode().equals(code)) {
                selfTraffic.addBeaconToRoute(airspace.getPublishedBeacons().get(i), time, new SimpleStringProperty(level));
            }
        }
    }

    public void addBeaconToRoute(String code){
        for(int i = 0; i < airspace.getPublishedBeacons().size(); i++){
            if(airspace.getPublishedBeacons().get(i).getCode().equals(code)) {
                selfTraffic.addBeaconToRoute(airspace.getPublishedBeacons().get(i), "", new SimpleStringProperty());
            }
        }
    }

    public void clearRoute() {
        if (selfTraffic.getRoute() != null) {
            selfTraffic.clearRoute();
        }
    }
}
