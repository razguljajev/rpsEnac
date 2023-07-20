package view.nav_display.compass;

import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Rotate;
import model.SelfTraffic;
import view.nav_display.resources.TranslatableHomotheticPane;

/**
 * This pane allows the heading dragged functionality.
 */
public class RotatePane extends TranslatableHomotheticPane {
    private double initialAngle = 0;
    private final CompassOrder compassOrder;
    private final Rotate rotate;
    private final SelfTraffic selfTraffic;

    public RotatePane(double radius, SelfTraffic selfTraffic){
        setPickOnBounds(false);
        this.selfTraffic = selfTraffic;

        // Create the Order Heading Element
        compassOrder = new CompassOrder();
        compassOrder.setLayoutX(0);
        compassOrder.setLayoutY(-radius);
        getChildren().add(compassOrder);

        //Add a Rotate component for the next event
        rotate = new Rotate(0, 0, 0);
        getTransforms().add(rotate);

        //We register the first point clicked, to help with the Action on Dragged Move Event
        addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> initialAngle = calculateAngle(mouseEvent.getX(), mouseEvent.getY()));

        //We calculate the angle with the current angle, thanks to the position computed with the initialAngle retrieve when clicking
        addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
            double currentAngle = calculateAngle(mouseEvent.getX(), mouseEvent.getY());
            double deltaAngle = currentAngle - initialAngle;
            double newAngle = (rotate.getAngle() + deltaAngle) % 360;
            if (newAngle < 0) newAngle += 360;
            int newHdg = (int)(newAngle + selfTraffic.getHeading().get()) % 360;

            //We modify there the 2 components that use the new angle acquired
            rotate.angleProperty().set(newHdg - selfTraffic.getHeading().get());
            compassOrder.getText().setText(String.format("%03d", newHdg));
        });
    }

    /**
     * Calculates the angle in degrees between the positive x-axis and the point (x, y) in the Cartesian coordinate system.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @return Angle in degrees between the positive x-axis and the given point
     */
    private double calculateAngle(double x, double y) {
        return Math.toDegrees(Math.atan2(y, x));
    }

    public CompassOrder getCompassOrder() {
        return compassOrder;
    }

    public void setRotationAngle(double angle) {
        rotate.angleProperty().set(angle - selfTraffic.getHeading().get());
    }
}
