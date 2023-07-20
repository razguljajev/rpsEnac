package view.nav_display.map;

import javafx.scene.transform.Rotate;
import model.SelfTraffic;
import view.nav_display.resources.TranslatableHomotheticPane;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class that contains the elements dedicated to instantiate and inverse scales the panes containing the beacons.
 * The idea is to create 2 classes with different information
 * @see BeaconPane Contains all the beacon, and have the interaction with click on action
 * @see FlightPane Contains the flight plan beacons and lines
 */
public abstract class BeaconView extends TranslatableHomotheticPane{
    List<BeaconMarker> beaconMarkerList = new ArrayList<>();

    public void instantiate(SelfTraffic selfTraffic) {
        layoutXProperty().bind(selfTraffic.getPosition().xProperty().negate());
        layoutYProperty().bind(selfTraffic.getPosition().yProperty().negate());

        Rotate rot = new Rotate();
        rot.pivotXProperty().bind(selfTraffic.getPosition().xProperty());
        rot.pivotYProperty().bind(selfTraffic.getPosition().yProperty());
        rot.angleProperty().bind(selfTraffic.getHeading().negate());
        getTransforms().add(rot);
    }

    public void setInverseScaling(double scaling) {
        for(BeaconMarker b : beaconMarkerList) b.setInverseScaling(scaling);
    }
}
