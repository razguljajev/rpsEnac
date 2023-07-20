package view.nav_display.map;

import fr.enac.sita.visuradar.model.IBeacon;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import model.BeaconData;
import model.SelfTraffic;

import java.util.ArrayList;
import java.util.List;

/**
 * Pane dedicated to the Flight Plan. In other terms, it displays the beacons of the flight and a line between them.
 * It is updated each time a Plan event or a beacon event occurs.
 * it can be compared to the Beacon Pane, which is only composed of Beacons and different interaction.
 */
public class FlightPane extends BeaconView {

    List<Line> lineList = new ArrayList<>();

    /**
     * When an element is added to the selfTraffic route, a new element is added, composed of a Line and a BeaconMarker
     * @param selfTraffic Info of the flight
     * @see BeaconMarker
     */
    public void addElement(BeaconData b, SelfTraffic selfTraffic){
        setMouseTransparent(true);

        IBeacon beacon = b.getBeacon();
        BeaconMarker beaconMarker = new BeaconMarker(b.getBeacon(), selfTraffic);
        beaconMarker.setLayoutX(beacon.getX());
        beaconMarker.setLayoutY(beacon.getY());
        beaconMarker.setVisualParameters(Color.GREEN);

        Line line;

        // If there is no line yet, it means that we need to create the first one from the SelfTraffic to the first next beacon
        if(lineList.isEmpty()) {
            line = createLine(
                    selfTraffic.getPosition().getX(),
                    selfTraffic.getPosition().getY(),
                    beaconMarker.getLayoutX(),
                    beaconMarker.getLayoutY());
            // We bind the start of the first line with the SelfTraffic, to make it "dynamic"
            line.startXProperty().bind(selfTraffic.getPosition().xProperty());
            line.startYProperty().bind(selfTraffic.getPosition().yProperty());
        }
        // Otherwise, we create simply the line between the beacon to the last one
        else {
            line = createLine(
                    beaconMarkerList.get(beaconMarkerList.size() - 1).getLayoutX(),
                    beaconMarkerList.get(beaconMarkerList.size() - 1).getLayoutY(),
                    beaconMarker.getLayoutX(),
                    beaconMarker.getLayoutY()
            );
        }
        lineList.add(line);

        beaconMarkerList.add(beaconMarker);
        // By problematics of threads, we need to perform a  Platform.runLater, otherwise,
        // the action will be performed on the IVY Thread, which is irrelevant
        Platform.runLater(() -> getChildren().add(0, line));
        Platform.runLater(() -> getChildren().add(beaconMarker));

        setMouseTransparent(true);
    }

    /**
     * This method is called when a Beacon Event is triggered, meaning that the Aircraft has reached a beacon of the flightPlan.
     * Here, We delete the concerned beacon and line, and bind the next line to the selfTraffic
     * @param selfTraffic Mandatory to connect the next line to it
     */
    public void deleteElement(SelfTraffic selfTraffic){
        if(!lineList.isEmpty() && !beaconMarkerList.isEmpty()) {
            Platform.runLater(() -> {
                getChildren().remove(lineList.get(0));
                getChildren().remove(beaconMarkerList.get(0));
                lineList.remove(0);
                beaconMarkerList.remove(0);
                lineList.get(0).startXProperty().bind(selfTraffic.getPosition().xProperty());
                lineList.get(0).startYProperty().bind(selfTraffic.getPosition().yProperty());
            });
        }
    }

    private Line createLine(double x1, double y1, double x2, double y2){
        Line line = new Line(x1, y1, x2, y2);
        line.setStroke(Color.DARKGREEN);
        line.setStrokeWidth(0.5);
        return line;
    }

    /**
     * Has the BeaconPane can be zoomed, we need to keep the initial size of the BeaconMarker, but also the lines
     * @param scaling Parameter of zoom scaled
     */
    @Override
    public void setInverseScaling(double scaling) {
        super.setInverseScaling(scaling); // for beacons
        for (Line line : lineList){ // for lines
            line.setStrokeWidth(line.getStrokeWidth()/scaling);
        }
    }

    /**
     * This method is called when we want to erase the FlightPane.
     * Actually, we call it when a Direct To order is performed, stirring the flightPlan data to empty
     */
    public void clearFlightPane() {
        Platform.runLater(() -> getChildren().clear());
        beaconMarkerList.clear();
        lineList.clear();
    }
}
