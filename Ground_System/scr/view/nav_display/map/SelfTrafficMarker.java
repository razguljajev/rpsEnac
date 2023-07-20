package view.nav_display.map;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import view.nav_display.resources.TranslatableHomotheticPane;

/**
 * This pane corresponds to the Aircraft Icon. It must not move or must not change with the different modification on the parents pane
 */
public class SelfTrafficMarker extends TranslatableHomotheticPane {

    public SelfTrafficMarker(){

        Image plane = new Image("view/nav_display/resources/airplane-mode-2.png");
        ImageView imgView = new ImageView(plane);

        double imageWidth = plane.getWidth();
        double imageHeight = plane.getHeight();

        double centerX = (getWidth() - imageWidth) / 2;
        double centerY = (getHeight() - imageHeight) / 2;

        imgView.setX(centerX);
        imgView.setY(centerY);
        getChildren().add(imgView);
    }

    public void setInverseScaling(double scaling) {
        appendScale(1/scaling);
    }
}
