package view.nav_display.map;

import view.nav_display.resources.TranslatableHomotheticPane;

/**
 * Radar Pane is composed of the 3 main elements of the map : the SelfTraffic, the Map Pane and the Beacon Pane. It is necessary because we do not want to modify the size of the beacons markers and the selftraffic marker
 * @see MapPane
 * @see BeaconPane
 * @see SelfTrafficMarker
 */
public class RadarPane extends TranslatableHomotheticPane {

    SelfTrafficMarker selfTrafficMarker;
    BeaconPane beaconPane;
    FlightPane flightPane;

    public RadarPane(SelfTrafficMarker selfTrafficMarker, BeaconPane beaconPane, FlightPane flightPane, MapPane mapPane){
        this.beaconPane = beaconPane;
        this.selfTrafficMarker = selfTrafficMarker;
        this.flightPane = flightPane;
        getChildren().add(mapPane);
        getChildren().add(this.beaconPane);
        getChildren().add(this.flightPane);
        getChildren().add(this.selfTrafficMarker);
    }

    public void setInverseScaling(double scaling) {
        beaconPane.setInverseScaling(scaling);
        flightPane.setInverseScaling(scaling);
        selfTrafficMarker.setInverseScaling(scaling);
    }
}
