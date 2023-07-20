package view.nav_display.map;

import fr.enac.sita.visuradar.data.param.VisualParameters;
import fr.enac.sita.visuradar.model.IBeacon;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import model.SelfTraffic;

import java.util.List;

/**
 * Pane containing every beacon provided.
 * When a zoom is performed, the pane is zoomed allowing the good positioned of the beacons on the pane,
 * even if each of the same doesn't change in size.
 * It is the same thing for the rotation event.
 * @see BeaconMarker
 */
public class BeaconPane extends BeaconView {
    private final SimpleStringProperty selectedBeaconCode = new SimpleStringProperty();
    private BeaconMarker selectedBeaconMarker = null;
    private Line selectedBeaconLine;
    private SelfTraffic selfTraffic;
    private boolean firstLine = true;

    public void setBeacons(List<IBeacon> beacons, SelfTraffic selfTraffic, VisualParameters visualParameters){
        this.selfTraffic = selfTraffic;
        for(IBeacon b : beacons) {
            BeaconMarker beaconMarker = new BeaconMarker(b, selfTraffic, this, visualParameters);
            beaconMarker.setLayoutX(b.getX());
            beaconMarker.setLayoutY(b.getY());
            beaconMarker.setVisualParameters(Color.PURPLE);
            beaconMarkerList.add(beaconMarker);
        }
        for(BeaconMarker beaconMarker : beaconMarkerList) {
            getChildren().add(beaconMarker);
        }
    }

    /**
     * When a beacon is selected, we verify if it is the one selected before.
     * If it's the case, we unselect it.
     * Otherwise, we unselect the previous selected beacon,
     * and we change the color of the new one, and use it to create a line between it and the selfTraffic.
     * @param beaconMarker Selected Beacon object
     */
    public void selectBeacon(BeaconMarker beaconMarker) {
        if(beaconMarker == selectedBeaconMarker) {
            unselect();
        }
        else {
            if(selectedBeaconMarker != null) unselect();
            System.out.println("Beacon selected : " + beaconMarker.getBeacon().getCode());
            selectedBeaconMarker = beaconMarker;
            beaconMarker.setVisualParameters(Color.YELLOW);
            createLineSelection();
            selectedBeaconCode.set(beaconMarker.getBeacon().getCode());
        }
    }

    /**
     * Contrary to selectBeacon(BeaconMarker beaconMarker), this method is performed thanks to the String Code of the beacon
     * We found the concerned beacon thanks to its code, and verify if it exists on our beaconMarkerList.
     * If there were a selected beacon already, we unselect it, and we change the color of the new one,
     * and use it to create a line between it and the selfTraffic.
     * @param beaconCode Selected Beacon String code
     */
    public void selectBeacon(String beaconCode) {
        if(selectedBeaconMarker != null) unselect();
        System.out.println("Beacon selected : " + beaconCode);
        for(BeaconMarker beaconMarker : beaconMarkerList) {
            if(beaconMarker.getBeacon().getCode().equals(beaconCode)) {
                selectedBeaconMarker = beaconMarker;
                selectedBeaconMarker.setVisualParameters(Color.YELLOW);
                createLineSelection();
                selectedBeaconCode.set(selectedBeaconMarker.getBeacon().getCode());
                break;
            }
        }
    }

    /**
     * When a beacon is selected, we want to show to the user which line the Aircraft will take when the Direct To is confirmed.
     * The line created must be bind to the selfTraffic, to make it dynamic.
     * If this is not the first line created, we remove it beforehand
     */
    private void createLineSelection() {
        if(firstLine) firstLine = false;
        else getChildren().remove(selectedBeaconLine);
        selectedBeaconLine = new Line();
        selectedBeaconLine.setStroke(Color.DARKGOLDENROD);
        selectedBeaconLine.startXProperty().bind(selfTraffic.getPosition().xProperty());
        selectedBeaconLine.startYProperty().bind(selfTraffic.getPosition().yProperty());
        selectedBeaconLine.setEndX(selectedBeaconMarker.getLayoutX());
        selectedBeaconLine.setEndY(selectedBeaconMarker.getLayoutY());
        getChildren().add(0, selectedBeaconLine);
    }

    public SimpleStringProperty getSelectedBeacon() {
        return selectedBeaconCode;
    }

    /**
     * This method is called when we want to unselect manually the beacon, and when we perform a Direct To order.
     * To unselect a beacon, we set the selected marker to null, and change its state.
     * We remove the line drawn from the pane.
     * @see view.SceneController
     */
    public void unselect(){
        if(selectedBeaconMarker != null) {
            selectedBeaconMarker.unselect();
            selectedBeaconMarker = null;
            firstLine = true;
            getChildren().remove(selectedBeaconLine);
        }
    }
    public  void hideBeacons(){
        setVisible(false);
    }
    public  void showBeacons(){
        setVisible(true);
    }
}
