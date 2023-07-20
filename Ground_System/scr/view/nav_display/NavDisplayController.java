package view.nav_display;

import javafx.beans.binding.Bindings;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import view.SceneController;

/**
 * This controller allows us to create a link between the non-static view and the static view.
 * In clear, this class exists to create some interactive events between the Scene,
 * such as the Dragging Heading Functionality and also the Click Beacon Functionality
 */
public class NavDisplayController {
    private final SceneController sceneController;
    private final NavDisplayConstructor navDisplay;

    public NavDisplayController(SceneController sceneController, NavDisplayConstructor navDisplay) {
        this.sceneController = sceneController;
        this.navDisplay = navDisplay;
        initialize();
    }

    private void initialize() {
        bindingData();

        navDisplay.getBeaconPane().getSelectedBeacon().addListener((observable, oldValue, newValue) ->
                sceneController.setCommand("DIRECT TO " + newValue));
    }

    private void bindingData(){
        // We want to bind the rotation pane of the compass with the text field order of Heading, so they can change accordingly
        Text rotateText = navDisplay.getCompass().getRotatePane().getCompassOrder().getText();
        TextField hdgTF = sceneController.getHdgTF();

        Bindings.bindBidirectional(hdgTF.textProperty(), rotateText.textProperty());

        // When hdgTF is modified, it modifies also the rotation angle of the rotatePane of the compass
        hdgTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                navDisplay.getCompass().getRotatePane().setRotationAngle(Double.parseDouble(newValue));
            }
        });
    }
}
