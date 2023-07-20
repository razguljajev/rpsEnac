package view;

import Controller.MessageControllerGround;
import fr.enac.sita.visuradar.data.param.VisualParameters;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.FlightData;
import view.nav_display.NavDisplayConstructor;

import java.io.IOException;


/**
 * This class allow us to create the entire view, and getting the controller of the static field
 * There is 2 main components on our HMI :
 *      - The static field which corresponds to the element that will not dynamically change and also the place where the user inputs will be done
 *      - The Navigation Display field which corresponds to the map (including the France map, the beacons, and the self-traffic) and its compass.
 * @see NavDisplayConstructor
 * @see SceneController
 */
public class ViewConstructor extends Pane {

    public ViewConstructor(FXMLLoader loader, FlightData flightData, MessageControllerGround messageController, VisualParameters visualParameters) {

        try {
            //Generate and load the 2 main views
            StackPane staticField = loader.load();
            SceneController sceneController = loader.getController();
            staticField.setStyle("-fx-background-color: #c4c4c4;" +
                                 "-fx-border-color: grey");

            NavDisplayConstructor navDisplay = new NavDisplayConstructor(flightData, sceneController, visualParameters);
            sceneController.initialize(navDisplay, flightData, messageController, visualParameters);


            //Creation of the right side container for the navigation Display
            VBox navDisplayContainer = new VBox();
            navDisplayContainer.setFillWidth(true);

            //This pane is to adjust the position of the navDisplay
            Pane pane = new Pane();
            pane.setMinHeight(staticField.getPrefHeight() * 0.95);

            navDisplayContainer.getChildren().addAll(pane, navDisplay);

            //The container is the principal view containing both the static and the navDisplay fields
            BorderPane container = new BorderPane();
            container.setRight(navDisplayContainer);
            container.setCenter(staticField);
            container.setStyle("-fx-background-color: #393939");

            //Here we set a margin to separate the both fields using the staticField space
            BorderPane.setMargin(navDisplayContainer, new Insets(0, 0, 0, staticField.getPrefWidth() + 50));

            getChildren().add(container);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
