package view.nav_display.map;

import javafx.beans.property.SimpleDoubleProperty;
import view.nav_display.resources.TranslatableHomotheticPane;

/**
 * This pane is the parent of all the other pane corresponding of the map.
 * On it, we add the event Scroll allowing us to zoom on all the elements.
 * For the element we do not want to zoom such as the Beacons and the Self Traffic, we perform an inverse zoom.
 * @see RadarPane
 */
public class MapTHPane extends TranslatableHomotheticPane {

    RadarPane radarPane;

    public MapTHPane(RadarPane radarPane){
        this.radarPane = radarPane;
        double baseScale=3;
        appendScale(baseScale);
        radarPane.setInverseScaling(baseScale);

        setOnScroll(event -> {
            double factor = Math.pow(1.0005, event.getDeltaY());
            appendScale(factor);
            radarPane.setInverseScaling(scaleProperty().get());
            event.consume();
        });

        getChildren().add(this.radarPane);
    }
}
