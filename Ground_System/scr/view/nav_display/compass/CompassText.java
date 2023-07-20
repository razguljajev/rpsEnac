package view.nav_display.compass;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import model.SelfTraffic;

/**
 * Each text of the compass is created separately to give a rotation independent of the compass's
 */
public class CompassText extends Text {

    public CompassText(int angle, double radius, SelfTraffic selfTraffic) {
        setMouseTransparent(true);
        setText(Integer.toString(angle));
        setStroke(Color.BLACK);
        setFont(new Font("Arial", 12));
        Point2D labelPosition = calculateLabelPosition(angle, radius);

        setX(labelPosition.getX() / 2);
        setY(labelPosition.getY() / 2);

        Rotate rot = new Rotate();
        rot.pivotXProperty().bind(selfTraffic.getPosition().xProperty().negate());
        rot.pivotYProperty().bind(selfTraffic.getPosition().yProperty().negate());

        getTransforms().add(rot);
    }

    /**
     * To position the text, we need the actual angle
     * @param angle Current angle of the compass text we want to create
     * @param radius Radius of the compass
     * @return Cardinal point of the text object created
     */
    private Point2D calculateLabelPosition(int angle, double radius) {
        double centerX = radius;
        double centerY = radius;
        radius = radius * 2 + 10;

        double adjustedAngle = 90 - angle;
        if (adjustedAngle < 0) {
            adjustedAngle += 360;
        }

        double labelX = centerX + radius * Math.cos(Math.toRadians(adjustedAngle));
        double labelY = centerY - radius * Math.sin(Math.toRadians(adjustedAngle));

        return new Point2D(labelX, labelY);
    }
}
