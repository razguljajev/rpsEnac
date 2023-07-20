package view.nav_display.compass;

import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import view.nav_display.resources.TranslatableHomotheticPane;

/**
 * Component allowing to compute a Heading Order when being moved (the event itself is on Rotate_Pane), its parent
 * @see RotatePane
 */
public class CompassOrder extends TranslatableHomotheticPane {
    Text text = new Text("0.");

    public CompassOrder(){
        Polygon polygon = new Polygon();

        double offsetTri = 20;
        double triangleHeight = offsetTri * Math.sqrt(3) / 2;
        double offsetRec = 25;

        polygon.setStroke(Color.BLACK);
        polygon.setFill(Color.LIGHTGRAY);
        polygon.getPoints().addAll(
                0., 0.,
                0. - offsetTri / 2, 0. + triangleHeight,
                0. - offsetTri / 2 - offsetRec, 0. + triangleHeight,
                0. - offsetTri / 2 - offsetRec, 0. + triangleHeight + offsetRec,
                0. + offsetTri / 2 + offsetRec, 0. + triangleHeight + offsetRec,
                0. + offsetTri / 2 + offsetRec, 0. + triangleHeight,
                0. + offsetTri / 2, 0. + triangleHeight
        );

        text.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        Bounds bounds = polygon.getBoundsInLocal();
        double centerX = bounds.getMinX() + (bounds.getWidth() / 2);
        double centerY = bounds.getMinY() + (bounds.getHeight() / 2);
        double textX = centerX - (text.getBoundsInLocal().getWidth() / 2);
        double textY = centerY + (text.getBoundsInLocal().getHeight() / 2);
        text.setX(textX);
        text.setY(textY);

        getChildren().addAll(polygon);
        getChildren().add(text);
    }

    public Text getText() {
        return text;
    }
}
