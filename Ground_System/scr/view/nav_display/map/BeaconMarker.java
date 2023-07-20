package view.nav_display.map;

import fr.enac.sita.visuradar.data.param.VisualParameters;
import fr.enac.sita.visuradar.model.IBeacon;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import model.SelfTraffic;
import view.nav_display.resources.TranslatableHomotheticPane;

/**
 * Each independent beacon are created with its triangle and its code ID. When a rotation is performed by its parents, it rotates at the opposite to stay "fixed", and it is the same for the zoom event
 */
public class BeaconMarker extends TranslatableHomotheticPane {
    private final Polygon poly;
    private final Text text;
    private final IBeacon beacon;
    private VisualParameters visualParameters;

    public BeaconMarker(IBeacon beacon, SelfTraffic selfTraffic, BeaconPane parent, VisualParameters visualParameters){
        this.beacon = beacon;
        this.visualParameters = visualParameters;
        poly = new Polygon();
        float offset = 5;
        poly.getPoints().setAll(
                0., 0. - offset,
                0. + offset, 0. + offset / 2,
                0. - offset, 0. + offset / 2);
        poly.setFill(visualParameters.getBeaconFillColor());
        poly.setStrokeWidth(visualParameters.getBeaconStrokeWidth());
        poly.setStroke(visualParameters.getBeaconStrokeColor());
        poly.setScaleX(visualParameters.getBeaconSize());
        poly.setScaleY(visualParameters.getBeaconSize());
        poly.setScaleZ(visualParameters.getBeaconSize());
        getChildren().add(poly);

        text = new Text(beacon.getCode());
        text.setX(0. - offset * 3);
        text.setY(0. + offset * 3);
        text.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        text.setFill(visualParameters.getBeaconTextFillColor());

        getChildren().add(text);

        setOnMouseClicked(event -> parent.selectBeacon(this));

        Rotate rot = new Rotate();
        rot.angleProperty().bind(selfTraffic.getHeading());
        getTransforms().add(rot);
    }

    public BeaconMarker(IBeacon beacon, SelfTraffic selfTraffic){
        this.beacon = beacon;
        poly = new Polygon();
        float offset = 5;
        poly.getPoints().setAll(
                0., 0. - offset,
                0. + offset, 0. + offset / 2,
                0. - offset, 0. + offset / 2);
        getChildren().add(poly);

        text = new Text(beacon.getCode());
        text.setX(0. - offset * 3);
        text.setY(0. + offset * 3);

        getChildren().add(text);

        Rotate rot = new Rotate();
        rot.angleProperty().bind(selfTraffic.getHeading());
        getTransforms().add(rot);
    }

    public void setVisualParameters(Color color){
        poly.setFill(color);
        text.setFill(color);
    }

    public void setInverseScaling(double scaling) {
        appendScale(1/scaling);
    }

    public IBeacon getBeacon() {
        return beacon;
    }

    public void unselect() {
        setVisualParameters(visualParameters.getBeaconFillColor());
    }
}