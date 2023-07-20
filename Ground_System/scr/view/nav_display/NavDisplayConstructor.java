package view.nav_display;

import fr.enac.sita.visuradar.data.param.VisualParameters;
import javafx.scene.layout.Pane;
import model.FlightData;
import view.SceneController;
import view.nav_display.compass.Compass;
import view.nav_display.map.*;

/**
 * Allow the creation of every pane that are not considered "static", which corresponds every pane of the Navigation Display
 * @see MapTHPane
 * @see Compass
 */
public class NavDisplayConstructor extends Pane {
    private final Compass compass;
    private final BeaconPane beaconPane;
    private final FlightPane flightPane;

    public NavDisplayConstructor(FlightData flightData, SceneController sceneController, VisualParameters visualParameters) {

        MapPane mapPane = new MapPane(flightData.getSelfTraffic());
        mapPane.setMap(flightData.getAirspace().getBaseMap().getZones(), visualParameters);

        beaconPane = new BeaconPane();
        beaconPane.instantiate(flightData.getSelfTraffic());
        beaconPane.setBeacons(flightData.getAirspace().getPublishedBeacons(), flightData.getSelfTraffic(), visualParameters);

        flightPane = new FlightPane();
        flightPane.instantiate(flightData.getSelfTraffic());

        SelfTrafficMarker selfTrafficMarker = new SelfTrafficMarker();

        RadarPane radarPane = new RadarPane(selfTrafficMarker, beaconPane, flightPane, mapPane);

        MapTHPane mapTHPane = new MapTHPane(radarPane);

        double radius = 420;
        compass = new Compass(flightData.getSelfTraffic(), radius);

        //Position the map and UI around point indicated
        double compassX = mapTHPane.getLayoutX() - (radius / 2);
        double compassY = mapTHPane.getLayoutY() - (radius / 2);

        compass.setLayoutX(compassX);
        compass.setLayoutY(compassY);

        //Add everything to the pane
        getChildren().add(mapTHPane);
        getChildren().add(compass);

        //Creation of its controller
        new NavDisplayController(sceneController, this);
    }

    public Compass getCompass() {
        return compass;
    }

    public BeaconPane getBeaconPane() {
        return beaconPane;
    }

    public FlightPane getFlightPane() {
        return flightPane;
    }
}
