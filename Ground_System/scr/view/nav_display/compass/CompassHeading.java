package view.nav_display.compass;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * Fixed component to show the heading of the Aircraft
 */
public class CompassHeading extends Polygon {

    public CompassHeading(double radius){
        double offset = 17.5;
        double headingMarkerX = radius / 2;
        double headingMarkerY = radius / 2 - radius;
        double triangleHeight = offset * Math.sqrt(3) / 2;

        setStroke(Color.CYAN);
        setStrokeWidth(2);
        getPoints().addAll(
                headingMarkerX, headingMarkerY,
                headingMarkerX - offset / 2, headingMarkerY - triangleHeight,
                headingMarkerX + offset / 2, headingMarkerY - triangleHeight
        );
        setFill(Color.TRANSPARENT);

    }
}
